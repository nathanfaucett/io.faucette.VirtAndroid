package io.faucette.virtandroid;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.faucette.virtandroid.javascript.JSRuntime;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity _this = this;

        Server server = new Server();
        server.listen(9999);

        Renderer renderer = new Renderer(server);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSRuntime runtime = new JSRuntime(_this);
                runtime.loop();
            }
        });
        thread.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
