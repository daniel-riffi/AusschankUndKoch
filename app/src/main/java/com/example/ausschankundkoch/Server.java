package com.example.ausschankundkoch;


import android.Manifest;
import android.os.Debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import androidx.core.content.PermissionChecker;
import at.orderlibrary.NotfiyPositionsFinishedRequest;
import at.orderlibrary.Order;
import at.orderlibrary.Type;
import at.orderlibrary.TypeRequest;

public class Server {
    private String ipAddress;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ReentrantLock lock;
    private Type type;
    private int port;
    private static Server instance;
    private ArrayList<Consumer> callbacks;

    public Server(){
        lock=new ReentrantLock();
        callbacks = new ArrayList<>();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void readOrderFromServer(Consumer<Order> callback){
        Thread t=new Thread(() -> {
            while(socket!=null&&reader!=null){
                try {
                    String json=reader.readLine();
                    Order order=new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE).create()
                            .fromJson(json, Order.class);
                    order.reinitPositionsWithOrder();
                    callback.accept(order);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        t.start();
    }
    public void notifyServerPositionsFinished(int positions){
            new Thread(() -> {
                NotfiyPositionsFinishedRequest request=new NotfiyPositionsFinishedRequest();
                request.count=positions;

                String json=new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE).create()
                        .toJson(request);
                writer.print(json+"\r\n");
                writer.flush();

            }).start();
    }
    public boolean connect(){
        try {
            if(isOpen()){
                close();
            }
            Thread t=new Thread(() -> {
                try {
                    lock.lock();
                    socket=new Socket();
                    socket.connect(new InetSocketAddress(ipAddress,port),5*1000);
                    reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));


                    TypeRequest request=new TypeRequest();
                    request.type= type;

                    String json=new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE).create()
                            .toJson(request);
                    writer.print(json+"\r\n");
                    writer.flush();

                    callbacks.forEach(x -> x.accept(null));
                    lock.unlock();
                    System.out.println();
                } catch (Exception e) {
                    lock.unlock();
                }

            });
            t.start();

            Thread.sleep(100);
            lock.lock();
            if(socket!=null&&reader!=null&&writer!=null) {
                lock.unlock();
                return true;
            }
            lock.unlock();

        } catch (Exception e) {
            e.printStackTrace();
            lock.unlock();
        }

        return false;
    }

    public void close() {
        try {
            socket.close();
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
        writer = null;
        reader = null;
    }

    public synchronized void onOpen(Consumer c){
        if(!isOpen()){
            if(callbacks==null){
                callbacks=new ArrayList<>();
            }
            callbacks.add(c);
        }else{
            c.accept(null);
        }

    }

    public boolean isOpen(){
        return reader!=null&&writer!=null;
    }

    public static Server getInstance(){
        if(instance == null) instance = new Server();
        return instance;
    }
}
