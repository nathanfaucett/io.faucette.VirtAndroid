package io.faucette.virtandroid.javascript;

import android.os.Handler;
import android.os.Looper;

import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by nathan on 8/31/16.
 */
public class JSEventCallback implements Comparable<JSEventCallback> {
    private static long ID = 0;

    public long id;
    public long timeout;
    public JSFunction function;

    public JSEventCallback(final JSFunction callback, final long delay) {
        id = ID++;
        function = callback;
        timeout = System.currentTimeMillis() + (delay > 0 ? delay : 0);
    }

    public JSEventCallback(final JSFunction callback) {
        id = ID++;
        function = callback;
        timeout = 0;
    }

    @Override
    public int compareTo(JSEventCallback other) {
        return (int) (timeout - other.timeout);
    }
}
