package io.faucette.virtandroid;


import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import io.faucette.virtandroid.messenger.Callback;

/**
 * Created by nathan on 8/29/16.
 */
public class Server extends WebSocketServer {
    private WebSocket _conn;
    private Callback _callback;

    public Server(int port) {

        super(new InetSocketAddress(port));

        _conn = null;
        _callback = null;
    }
    public Server() {
        this(9999);
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        _conn = conn;
    }
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        _conn = null;
    }
    public void onMessage(WebSocket conn, String message) {
        if (_conn == conn) {
            onMessage(message);
        }
    }
    public void onError(WebSocket conn, Exception ex) {
        if (_conn == conn) {
            Log.e("Server", ex.toString());
        }
    }

    public void addListener(Callback callback) {
        _callback = callback;
    }
    public void onMessage(String data) {
        if (_callback != null) {
            _callback.call(data);
        }
    }
    public void postMessage(String data) {
        if (_conn != null) {
            _conn.send(data);
        }
    }
}
