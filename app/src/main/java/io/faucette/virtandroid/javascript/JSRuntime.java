package io.faucette.virtandroid.javascript;


import android.app.Activity;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSException;
import org.liquidplayer.webkit.javascriptcore.JSFunction;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by nathan on 8/10/16.
 */
public class JSRuntime extends JSContext implements IJSRuntime {
    private JSModule _rootModule;
    private ArrayList<JSEventCallback> _timeoutCallbacks;


    public JSRuntime(Activity activity) {

        super(IJSRuntime.class);

        _timeoutCallbacks = new ArrayList<>();

        _init();

        _rootModule = new JSModule(this, ".", null);
        _rootModule.setActivity(activity);
        _rootModule.require(".");
    }

    public final void setActivity(Activity activity) {
        _rootModule.setActivity(activity);
    }

    public final long setTimeout(final JSFunction fn, final long delay) {
        JSEventCallback callback = new JSEventCallback(fn, delay);

        _timeoutCallbacks.add(callback);
        Collections.sort(_timeoutCallbacks);

        // force a tick of the event loop
        Thread.currentThread().interrupt();

        return callback.id;
    }

    public final long setImmediate(final JSFunction fn) {
        return setTimeout(fn, 0);
    }

    public final void clearTimeout(final long id) {
        int index = 0;

        for (JSEventCallback callback : _timeoutCallbacks) {
            if (callback.id == id) {
                _timeoutCallbacks.remove(index);
                break;
            }
            index++;
        }
    }

    private void _init() {
        final JSRuntime _this = this;

        setExceptionHandler(new IJSExceptionHandler() {
            @Override
            public void handle(JSException exception) {
                Log.e("JSRuntime", exception.toString());
            }
        });

        property("global", this);
        property("console", new JSConsole(this));
        property("process", new JSProcess(this));

        try {
            property("Buffer", new JSBuffer(this));
            property("WebSocket", new JSWebSocket(this));
        } catch (NoSuchMethodException e) {
            Log.e("JSRuntime", e.toString());
        }
    }

    private long _tick() {
        final ArrayList<JSEventCallback> _callbacks = new ArrayList<>();
        final long currentTime = System.currentTimeMillis();

        while (_timeoutCallbacks.size() > 0 && _timeoutCallbacks.get(0).timeout <= currentTime) {
            _callbacks.add(_timeoutCallbacks.remove(0));
        }

        if (_callbacks.size() != 0) {
            this.sync(new Runnable() {
                @Override
                public void run() {
                    for (JSEventCallback callback : _callbacks) {
                        callback.function.call(null);
                    }
                }
            });
        }

        if (_timeoutCallbacks.size() == 0) {
            return Long.MAX_VALUE; // sleep forever
        } else {
            return _timeoutCallbacks.get(0).timeout - currentTime;
        }
    }

    public final void loop() {
        while (true) {
            long timeout = _tick();

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
            }
        }
    }
}
