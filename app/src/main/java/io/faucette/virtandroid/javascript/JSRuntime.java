package io.faucette.virtandroid.javascript;


import android.app.Activity;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.util.ArrayList;


/**
 * Created by nathan on 8/10/16.
 */
public class JSRuntime extends JSContext {
    private JSModule _rootModule;

    private long _id;
    private long _startTime;

    private boolean _running;
    private Thread _thread;
    private ArrayList<JSEventCallback> _eventCallbacks;


    private class JSEventCallback {
        public long id;
        public long ms;
        public JSFunction function;

        public JSEventCallback(long _id, JSFunction _function, long _ms) {
            id = _id;
            function = _function;
            ms = _ms > 0 ? _ms : 0;
        }
    }


    public JSRuntime(Activity activity) {

        super();

        _eventCallbacks = new ArrayList<>();
        _startTime = System.currentTimeMillis();

        _init();

        _rootModule = new JSModule(this, ".", null);
        _rootModule.setActivity(activity);
        _rootModule.require("./index");
    }

    public void setActivity(Activity activity) {
        _rootModule.setActivity(activity);
    }

    public void start() {
        if (!_running) {
            _running = true;
            _thread.start();
        }
    }

    public void stop() {
        if (_running) {
            _running = false;
        }
    }

    private void _init() {
        final JSRuntime _this = this;

        _thread = new Thread(new Runnable() {
            @Override
            public void run() {
                _this._run();
            }
        });

        property("console", new JSConsole(this));

        property("setTimeout", new JSFunction(this, "setTimeout") {
            public long setTimeout(final JSFunction fn, final JSValue delay) {
                return _this._setTimeout(fn, delay.toNumber().longValue());
            }
        });

        property("setImmediate", new JSFunction(this, "setImmediate") {
            public long setImmediate(final JSFunction fn) {
                return _this._setTimeout(fn, 0);
            }
        });

        property("clearTimeout", new JSFunction(this, "clearTimeout") {
            public void clearTimeout(final long id) {
                _this._clearTimeout(id);
            }
        });
    }

    private void _run() {
        while (_running) {
            if (_eventCallbacks.size() > 0) {
                _handleEventLoop();
            }
        }
    }

    private long _setTimeout(final JSFunction fn, long delay) {
        long id = _id++;
        _eventCallbacks.add(new JSEventCallback(id, fn, delay));
        return id;
    }

    private void _clearTimeout(final long id) {
        int index = 0;

        for (JSEventCallback callback : _eventCallbacks) {
            if (callback.id == id) {
                _eventCallbacks.remove(index);
            }
            index++;
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
