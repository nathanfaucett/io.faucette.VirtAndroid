package io.faucette.virtandroid;

import android.os.Looper;
import android.util.Log;

import io.faucette.virtandroid.messenger.Callback;
import io.faucette.virtandroid.websockets.FakeWebSocket;
import io.faucette.virtandroid.websockets.FakeWebSocketServer;

/**
 * Created by nathan on 9/1/16.
 */
public class Server extends FakeWebSocketServer {
    private Callback _callback;

    @Override
    public void onOpen(FakeWebSocket webSocket) {
        Log.i("Server", "Open");
    }

    @Override
    public void onClose(FakeWebSocket webSocket, boolean remote) {
        Log.i("Server", "Close remote:" + remote);
    }

    @Override
    public void onMessage(FakeWebSocket webSocket, String data) {
        handleMessage(data);
    }

    @Override
    public void onError(FakeWebSocket webSocket, Exception ex) {
        Log.e("Server", ex.toString());
    }

    public void addListener(Callback callback) {
        _callback = callback;
    }

    public void handleMessage(String data) {
        _callback.call(data);
    }

    public void sendToAll(String data) {
        for (FakeWebSocket webSocket : getWebSockets()) {
            webSocket.send(data);
        }
    }
}
