package io.faucette.virtandroid;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.faucette.virtandroid.javascript.JSRuntime;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSRuntime runtime = new JSRuntime(this);
        runtime.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
