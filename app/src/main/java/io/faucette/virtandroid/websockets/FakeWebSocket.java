package io.faucette.virtandroid.websockets;

/**
 * Created by nathan on 9/1/16.
 */
public class FakeWebSocket {
    private FakeSocket _socket;


    public FakeWebSocket(FakeSocket socket) {
        _socket = socket;
    }

    public final void send(String data) {
        _socket.postMessage(data);
    }

    public final void close() {
        FakeWebSocketImpl.close(_socket.getId(), _socket.getPort(), true);
    }
}
