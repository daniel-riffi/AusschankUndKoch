package com.example.ausschankundkoch;

import org.junit.Test;
import static junit.framework.TestCase.*;

public class ServerTest {
    int port=17723;


    @Test
    public void Test_Connection_Fail(){
        Server server=new Server("1.2.3.4",port);
        assertFalse(server.connect());
    }

    @Test
    public void Test_Connect(){
        Server server=new Server("localhost", port);
        assertTrue(server.connect());
    }
}
/*    ActivityScenario<RunningActivity> scenario=ActivityScenario.launch(RunningActivity.class);

        scenario.onActivity(activity->{
                Server server=new Server("localhost", activity,port);
                assertTrue(server.connect());
                });*/