package io.faucette.virtandroid;


import android.app.Activity;

import io.faucette.messenger.Adapter;
import io.faucette.messenger.Callback;


/**
 * Created by nathan on 8/29/16.
 */
public class WebSocketAdapter implements Adapter {
    private Activity _activity;
    private Server _server;

    public WebSocketAdapter(Activity activity, Server server) {
        _activity = activity;
        _server = server;
    }

    public void addMessageListener(Callback callback) {
        _server.addListener(callback);
    }

    public void onMessage(final String data) {
        _server.onMessage(data);
    }

    public void postMessage(final String data) {
        _server.sendToAll(data);
    }
}