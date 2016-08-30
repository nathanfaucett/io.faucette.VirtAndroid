package io.faucette.virtandroid;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.faucette.virtandroid.javascript.JSRuntime;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Server server = new Server(9999);
        server.start();

        Renderer renderer = new Renderer(server);

        final Activity _this = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSRuntime runtime = new JSRuntime(_this);

                while (runtime.isRunning()) {
                    runtime.tick();
                }
            }
        });

        thread.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
