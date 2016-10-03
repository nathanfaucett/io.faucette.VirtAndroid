package io.faucette.virtandroid;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import io.faucette.virtandroid.javascript.JSRuntime;
import io.faucette.virtandroid.renderer.Consts;
import io.faucette.virtandroid.renderer.Renderer;
import io.faucette.virtandroid.renderer.Views;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity _this = this;

        Server server = new Server(9999, _this);
        //server.start();

        Consts.init();

        try {
            Views.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSRuntime runtime = new JSRuntime(_this);
                runtime.loop();
            }
        });
        thread.start();

        Renderer renderer = new Renderer(_this, (ViewGroup) findViewById(android.R.id.content), server);
    }
}
