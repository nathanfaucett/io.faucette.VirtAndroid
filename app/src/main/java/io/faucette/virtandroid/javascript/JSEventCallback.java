package io.faucette.virtandroid.javascript;

import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by nathan on 8/31/16.
 */
public class JSEventCallback implements Comparable<JSEventCallback> {
    private static long ID = 0;

    public long id;
    public double timeout;
    public JSFunction function;


    public JSEventCallback(final JSFunction callback, final long delay) {
        id = ID++;
        function = callback;
        timeout = (System.nanoTime() * 1e-6) + (delay > 0 ? delay : 0);
    }

    public JSEventCallback(final JSFunction callback) {
        id = ID++;
        function = callback;
        timeout = 0;
    }

    public void call() {
        function.call(null);
    }

    @Override
    public int compareTo(JSEventCallback other) {
        if (timeout < other.timeout) {
            return -1;
        } else if (timeout > other.timeout) {
            return 1;
        } else {
            return 0;
        }
    }
}
