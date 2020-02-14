package com.example.ausschankundkoch;


import android.Manifest;
import android.os.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import androidx.core.content.PermissionChecker;
import at.orderlibrary.NotfiyPositionsFinishedRequest;
import at.orderlibrary.Order;

public class Server {
    private String ipAddress;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ReentrantLock lock;

    private int port;

    public Server(String ipAddress,  int port){
        this.ipAddress=ipAddress;
        this.port=port;
        lock=new ReentrantLock();
    }
    public void readOrderFromServer(final Consumer<Order> callback){
        Thread t=new Thread(() -> {
            while(socket!=null&&inputStream!=null){
                try {
                    Object objectFromServer=inputStream.readObject();
                    if(objectFromServer instanceof Order){
                        Order order=(Order) objectFromServer;
                        callback.accept(order);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        t.start();
    }
    public void notifyServerPositionsFinished(int positions){
        NotfiyPositionsFinishedRequest request=new NotfiyPositionsFinishedRequest();
        request.count=positions;
            new Thread(() -> {
                try {
                    outputStream.writeObject(request);
                    outputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
    }
    public boolean connect(){
        try {
            Thread t=new Thread(() -> {
                try {
                    lock.lock();
                    socket=new Socket(ipAddress,port);
                    System.out.println("");
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    lock.unlock();
                    System.out.println();
                } catch (IOException e) {
                    e.printStackTrace();
                    lock.unlock();
                }

            });
            t.start();
            Thread.sleep(100);
            lock.lock();
            if(socket!=null&&inputStream!=null&&outputStream!=null) {
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
}
