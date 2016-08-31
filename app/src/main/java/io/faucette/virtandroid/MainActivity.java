package io.faucette.virtandroid;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.java_websocket.WebSocketImpl;

import io.faucette.virtandroid.javascript.JSRuntime;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity _this = this;

        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");

        final Server server = new Server(9999);
        server.start();

        Renderer renderer = new Renderer(server);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSRuntime runtime = new JSRuntime(_this);
                runtime.start();
            }
        });
        thread.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
