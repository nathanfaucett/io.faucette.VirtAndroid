package io.faucette.virtandroid.javascript;


import android.app.Activity;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSException;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by nathan on 8/10/16.
 */
public class JSRuntime extends JSContext implements IJSRuntime {
    public JSObject global;
    public JSObject console;
    public JSObject process;
    public JSEvent Event;
    public JSEventTarget EventTarget;
    public JSBuffer Buffer;
    public JSWebSocket WebSocket;
    public JSXMLHttpRequest XMLHttpRequest;
    private Activity _activity;
    private final Thread _thread;
    private JSModule _rootModule;
    private List<JSEventCallback> _timeoutCallbacks;


    public JSRuntime(Activity activity) {

        super(IJSRuntime.class);

        _activity = activity;
        _thread = Thread.currentThread();
        _timeoutCallbacks = new ArrayList<>();

        _init();

        _rootModule = new JSModule(this, ".", null);
        _rootModule.setActivity(activity);
        _rootModule.require(".");
    }

    public final Activity getActivity() {
        return _activity;
    }

    public final void setActivity(Activity activity) {
        _activity = activity;
        _rootModule.setActivity(activity);
    }

    public synchronized final long setTimeout(final JSFunction fn, final long delay) {
        JSEventCallback callback = new JSEventCallback(fn, delay);

        _timeoutCallbacks.add(callback);
        Collections.sort(_timeoutCallbacks);

        // force a tick of the event loop
        _thread.interrupt();

        return callback.id;
    }

    public synchronized final long setImmediate(final JSFunction fn) {
        return setTimeout(fn, 0);
    }

    public synchronized final void clearTimeout(final long id) {
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

        setExceptionHandler(new IJSExceptionHandler() {
            @Override
            public void handle(JSException exception) {
                Log.e("JSRuntime", exception.toString());
            }
        });

        global = this;
        console = new JSConsole(this);
        process = new JSProcess(this);

        try {
            Event = new JSEvent(this);
            EventTarget = new JSEventTarget(this);
            Buffer = new JSBuffer(this);
            WebSocket = new JSWebSocket(this);
            XMLHttpRequest = new JSXMLHttpRequest(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        property("global", global);
        property("console", console);
        property("process", process);

        property("Event", Event);
        property("EventTarget", EventTarget);
        property("Buffer", Buffer);
        property("WebSocket", WebSocket);
        property("XMLHttpRequest", XMLHttpRequest);
    }

    private synchronized long _tick() {
        final List<JSEventCallback> _callbacks = new ArrayList<>();
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
            return Long.MAX_VALUE; // sleep "forever"
        } else {
            return _timeoutCallbacks.get(0).timeout - currentTime;
        }
    }

    public final void loop() {
        final JSRuntime _this = this;
        long timeout;

        while (true) {
            timeout = _tick();

            try {
                _thread.sleep(timeout);
            } catch (InterruptedException ex) {
            }
        }
    }
}
