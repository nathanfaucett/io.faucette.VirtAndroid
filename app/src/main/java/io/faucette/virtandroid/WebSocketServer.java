package io.faucette.virtandroid;


import android.app.Activity;
import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.ArrayList;
import java.util.List;

import io.faucette.messenger.Callback;

/**
 * Created by nathan on 8/29/16.
 */
public class WebSocketServer {
    private Activity _activity;
    private List<Callback> _callbacks;
    private List<WebSocket> _sockets;
    private AsyncHttpServer _server;
    private int _port;


    public WebSocketServer(int port, Activity activity) {

        _activity = activity;
        _callbacks = new ArrayList<>();
        _sockets = new ArrayList<>();
        _server = new AsyncHttpServer();
        _port = port;

        _server.setErrorCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                ex.printStackTrace();
            }
        });

        final WebSocketServer _this = this;
        _server.websocket("/", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket websocket, AsyncHttpServerRequest request) {

                _sockets.add(websocket);

                websocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            _this.onError(websocket, ex);
                        } finally {
                            _sockets.remove(websocket);
                        }
                    }
                });
                websocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        _this.onMessage(websocket, s);
                    }
                });

                _this.onOpen(websocket);
            }
        });
    }

    public WebSocketServer(Activity activity) {
        this(9999, activity);
    }

    public void start() {
        _server.listen(_port);
    }

    public void onOpen(WebSocket conn) {
        Log.i("Server", "WebSocket connected");
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.i("Server", "WebSocket closed - code: " + code + " reason: " + reason + " remote: " + remote);
    }

    public void onMessage(WebSocket conn, String message) {
        handleMessage(message);
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

    public void handleMessage(final String data) {
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
        synchronized (_sockets) {
            for (WebSocket conn : _sockets) {
                synchronized (conn) {
                    conn.send(data);
                }
            }
        }
    }
}