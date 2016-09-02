package io.faucette.virtandroid.websockets;

import android.util.Log;

import java.net.URI;

/**
 * Created by nathan on 9/1/16.
 */
public abstract class FakeWebSocketClient {
    private URI _uri;
    private FakeSocket _socket;

    public FakeWebSocketClient(URI uri) {
        _uri = uri;
        _socket = null;
    }

    public final void _setSocket(FakeSocket socket) {
        _socket = socket;
    }

    public final void connect() throws Exception {
        FakeWebSocketImpl.open(_uri.getPort(), this);
    }

    public final int getPort() {
        return _uri.getPort();
    }

    public final void send(String data) {
        _socket.postMessage(data);
    }

    public final void close() {
        FakeWebSocketImpl.close(_socket.getId(), _socket.getPort(), false);
        onClose(false);
    }

    public abstract void onOpen();
    public abstract void onMessage(String data);
    public abstract void onClose(boolean remote);
    public abstract void onError(Exception ex);
}
