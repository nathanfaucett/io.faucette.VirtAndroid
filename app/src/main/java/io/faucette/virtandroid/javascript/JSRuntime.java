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

import static java.lang.Thread.State.RUNNABLE;


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
    private List<JSEventCallback> _callbacks;
    private double _timeout = Double.MAX_VALUE;


    public JSRuntime(Activity activity) {

        super(IJSRuntime.class);

        _activity = activity;
        _thread = Thread.currentThread();
        _callbacks = new ArrayList<>();

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

    public final long setTimeout(final JSFunction fn, final long delay) {
        JSEventCallback callback = new JSEventCallback(fn, delay);

        synchronized (_callbacks) {
            _callbacks.add(callback);
            Collections.sort(_callbacks);
        }

        // force a tick of the event loop
        if (_thread.getState() != RUNNABLE) {
            _thread.interrupt();
        } else {
            synchronized (this) {
                _timeout = _timeout < delay ? _timeout : delay;
            }
        }

        return callback.id;
    }

    public final long setImmediate(final JSFunction fn) {
        return setTimeout(fn, 0);
    }

    public final void clearTimeout(final long id) {
        synchronized (_callbacks) {
            int index = 0;

            for (JSEventCallback callback : _callbacks) {
                if (callback.id == id) {
                    _callbacks.remove(index);
                    break;
                }
                index++;
            }
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

    private synchronized void _tick() {
        final List<JSEventCallback> callbacks = new ArrayList<>();
        final double currentTime = (System.nanoTime() * 1e-6);

        synchronized (_callbacks) {
            while (_callbacks.size() > 0 && _callbacks.get(0).timeout <= currentTime) {
                callbacks.add(_callbacks.remove(0));
            }
        }

        synchronized (this) {
            if (_callbacks.size() != 0) {
                _timeout = _callbacks.get(0).timeout - currentTime;
            } else {
                _timeout = Double.MAX_VALUE;
            }
        }

        if (callbacks.size() != 0) {
            for (JSEventCallback callback : callbacks) {
                callback.call();
            }
        }
    }

    public final void loop() {
        final JSRuntime _this = this;

        while (true) {
            try {
                _thread.sleep((long) _timeout);
            } catch (InterruptedException ex) {
            } finally {
                _tick();
            }
        }
    }
}
