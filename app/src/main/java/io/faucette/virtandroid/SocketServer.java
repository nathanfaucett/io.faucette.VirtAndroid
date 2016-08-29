package io.faucette.virtandroid;


import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by nathan on 8/29/16.
 */
public class SocketServer {
    private WebSocketServer _server;


    public SocketServer(InetSocketAddress address) {
        final SocketServer _this = this;

        _server = new WebSocketServer(address) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                _this.onOpen(conn, handshake);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                _this.onClose(conn, code, reason, remote);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                _this.onMessage(conn, message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                _this.onError(conn, ex);
            }
        };
    }
    public SocketServer(int port) {
        this(new InetSocketAddress(port));
    }

    public void start() {
        _server.start();
    }
    public void stop() {
        try {
            _server.stop();
        } catch (Exception e) {}
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Hello, world!");
    }
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }
    public void onMessage(WebSocket conn, String message) {

    }
    public void onError(WebSocket conn, Exception ex) {
        Log.e("SocketServer", ex.toString());
    }
}
