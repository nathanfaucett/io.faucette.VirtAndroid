package io.faucette.virtandroid;

import org.json.JSONObject;

import io.faucette.virtandroid.messenger.Adapter;
import io.faucette.virtandroid.messenger.Callback;

/**
 * Created by nathan on 8/29/16.
 */
public class WebSocketAdapter implements Adapter {
    private Server _server;

    public WebSocketAdapter(Server server) {
        _server = server;
    }

    public void addMessageListener(Callback callback) {
        _server.addListener(callback);
    }
    public void onMessage(String data) {
        _server.onMessage(data);
    }
    public void postMessage(String data) {
        _server.postMessage(data);
    }
}