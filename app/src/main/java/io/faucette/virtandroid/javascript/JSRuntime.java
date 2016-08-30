package io.faucette.virtandroid.javascript;


import android.app.Activity;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * Created by nathan on 8/10/16.
 */
public class JSRuntime extends JSContext {
    private JSModule _rootModule;

    private long _id;
    private long _startTime;

    private boolean _running;
    private ArrayList<JSEventCallback> _eventCallbacks;


    private class JSEventCallback {
        public long id;
        public long ms;
        public JSFunction function;

        public JSEventCallback(long _id, JSFunction _function, long _ms) {
            id = _id;
            function = _function;
            ms = System.currentTimeMillis() + (_ms > 0 ? _ms : 0);
        }
    }


    public JSRuntime(Activity activity) {

        super();

        _eventCallbacks = new ArrayList<>();
        _startTime = System.currentTimeMillis();

        _init();

        _rootModule = new JSModule(this, ".", null);
        _rootModule.setActivity(activity);
        _rootModule.require(".");
    }

    public void setActivity(Activity activity) {
        _rootModule.setActivity(activity);
    }

    public boolean isRunning() {
        return _running;
    }

    public long setTimeout(final JSFunction fn, long delay) {
        long id = _id++;
        _eventCallbacks.add(new JSEventCallback(id, fn, delay));
        return id;
    }

    public void clearTimeout(final long id) {
        int index = 0;

        for (JSEventCallback callback : _eventCallbacks) {
            if (callback.id == id) {
                _eventCallbacks.remove(index);
            }
            index++;
        }
    }

    private void _init() {
        final JSRuntime _this = this;

        property("global", getThis());
        property("console", new JSConsole(this));
        property("process", new JSProcess(this));

        try {
            property("Buffer", new JSBuffer(this));
            property("WebSocket", new JSWebSocket(this));
        } catch (NoSuchMethodException e) {
            Log.e("JSRuntime", e.toString());
        }

        property("setTimeout", new JSFunction(this, "setTimeout") {
            public long setTimeout(final JSFunction fn, final JSValue delay) {
                return _this.setTimeout(fn, delay.toNumber().longValue());
            }
        });

        property("setImmediate", new JSFunction(this, "setImmediate") {
            public long setImmediate(final JSFunction fn) {
                return _this.setTimeout(fn, 0);
            }
        });

        property("clearTimeout", new JSFunction(this, "clearTimeout") {
            public void clearTimeout(final long id) {
                _this.clearTimeout(id);
            }
        });
    }

    public void tick() {
        if (_eventCallbacks.size() > 0) {
            _handleEventLoop();
        }
    }

    private void _handleEventLoop() {
        for (int i = _eventCallbacks.size() - 1; i >= 0; i--) {
            JSEventCallback callback = _eventCallbacks.get(i);

            if (callback.ms <= (System.currentTimeMillis() - _startTime)) {
                callback.function.call();
                _eventCallbacks.remove(i);
            }
        }
    }
}
