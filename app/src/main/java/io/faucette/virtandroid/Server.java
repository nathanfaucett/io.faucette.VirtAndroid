package io.faucette.virtandroid;


import android.app.Activity;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import io.faucette.messenger.Callback;

/**
 * Created by nathan on 8/29/16.
 */
public class Server extends WebSocketServer {
    private Activity _activity;
    private ArrayList<Callback> _callbacks;


    public Server(int port, Activity activity) {

        super(new InetSocketAddress(port));

        _callbacks = new ArrayList<>();
        _activity = activity;
    }

    public Server(Activity activity) {
        this(9999, activity);
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.i("Server", "WebSocket connected");
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.i("Server", "WebSocket closed - code: " + code + " reason: " + reason + " remote: " + remote);
    }

    public void onMessage(WebSocket conn, String message) {
        onMessage(message);
    }

    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public void addListener(Callback callback) {
        _callbacks.add(callback);
    }

    public void removeListener(Callback callback) {
        _callbacks.remove(callback);
    }

    public void onMessage(final String data) {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Callback callback : _callbacks) {
                    callback.call(data);
                }
            }
        });
    }

    public void sendToAll(final String data) {
        Collection<WebSocket> connections = connections();

        for (WebSocket conn : connections) {
            conn.send(data);
        }
    }
}