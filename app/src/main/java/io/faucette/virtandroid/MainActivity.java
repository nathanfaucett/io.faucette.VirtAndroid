package io.faucette.virtandroid;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.faucette.virtandroid.javascript.JSRuntime;
import io.faucette.virtandroid.messenger.ServerClient;
import io.faucette.virtandroid.messenger.SimpleAdapter;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ServerClient serverClient = SimpleAdapter.createServerClient();
        final Activity _this = this;

        Renderer renderer = new Renderer(serverClient.server);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSRuntime runtime = new JSRuntime(_this, serverClient.client);
                runtime.start();
            }
        });
        thread.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
