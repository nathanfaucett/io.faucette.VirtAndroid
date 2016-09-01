package io.faucette.virtandroid.javascript;


import android.app.Activity;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSException;
import org.liquidplayer.webkit.javascriptcore.JSFunction;

import java.util.ArrayList;

import io.faucette.virtandroid.messenger.SimpleAdapter;


/**
 * Created by nathan on 8/10/16.
 */
public class JSRuntime extends JSContext implements IJSRuntime {
    private JSModule _rootModule;

    private boolean _running;
    private SimpleAdapter _clientSocket;
    private ArrayList<JSEventCallback> _timeoutCallbacks;


    public JSRuntime(Activity activity, SimpleAdapter clientSocket) {

        super(IJSRuntime.class);

        _running = false;
        _clientSocket = clientSocket;
        _timeoutCallbacks = new ArrayList<>();

        _init();

        _rootModule = new JSModule(this, ".", null);
        _rootModule.setActivity(activity);
        _rootModule.require(".");
    }

    public void setActivity(Activity activity) {
        _rootModule.setActivity(activity);
    }

    public void start() {
        if (!_running) {
            _running = true;

            while (_running) {
                tick();
            }
        }
    }
    public void stop() {
        if (_running) {
            _running = false;
        }
    }

    public boolean isRunning() {
        return _running;
    }

    public SimpleAdapter getClientSocket() {
        return _clientSocket;
    }

    public long setTimeout(final JSFunction fn, final long delay) {
        JSEventCallback callback = new JSEventCallback(fn, delay);
        _timeoutCallbacks.add(callback);
        return callback.id;
    }
    public long setImmediate(final JSFunction fn) {
        return setTimeout(fn, 0);
    }
    public void clearTimeout(final long id) {
        int index = 0;

        for (JSEventCallback callback : _timeoutCallbacks) {
            if (callback.id == id) {
                _timeoutCallbacks.remove(index);
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

    public void tick() {
        if (_timeoutCallbacks.size() > 0) {
            _handleEventLoop();
        }
    }

    private void _handleEventLoop() {
        final ArrayList<JSEventCallback> timeoutCallbacks = _timeoutCallbacks;
        final long currentTime = System.currentTimeMillis();

        this.sync(new Runnable() {
            @Override
            public void run() {
                for (int i = timeoutCallbacks.size() - 1; i >= 0; i--) {
                    final JSEventCallback callback = timeoutCallbacks.get(i);

                    if (callback.timeout <= currentTime) {
                        timeoutCallbacks.remove(i);
                        callback.function.call();
                    }
                }
            }
        });
    }
}
