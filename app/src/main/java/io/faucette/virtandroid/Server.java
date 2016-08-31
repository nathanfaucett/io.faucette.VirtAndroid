package io.faucette.virtandroid;


import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Collection;

import io.faucette.virtandroid.messenger.Callback;

/**
 * Created by nathan on 8/29/16.
 */
public class Server extends WebSocketServer {
    private Callback _callback;

    public Server(int port) {

        super(new InetSocketAddress(port));

        _callback = null;
    }
    public Server() {
        this(9999);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.i("Server", "WebSocket Opened");
    }
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.i("Server", "WebSocket Closed");
    }
    @Override
    public void onMessage(WebSocket conn, String message) {
        //onMessage(message);
    }
    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.e("Server", ex.toString());
    }

    public void addListener(Callback callback) {
        _callback = callback;
    }
    public void onMessage(String data) {
        _callback.call(data);
    }

    public void sendToAll(String string) {
        Collection<WebSocket> connections = connections();

        synchronized (connections) {
            for (WebSocket conn : connections) {
                conn.send(string);
            }
        }
    }
}
