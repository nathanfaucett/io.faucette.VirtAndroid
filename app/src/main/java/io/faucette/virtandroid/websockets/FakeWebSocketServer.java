package io.faucette.virtandroid.websockets;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by nathan on 9/1/16.
 */
public abstract class FakeWebSocketServer {
    private HashMap<Long, FakeWebSocket> _sockets;


    public FakeWebSocketServer() {
        _sockets = new HashMap<>();
    }

    public final void listen(int port) {
        try {
            FakeWebSocketImpl.listen(this, port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final void createWebSocket(final int port, final FakeWebSocketClient clientWebSocket) {
        final FakeWebSocketServer _this = this;
        long id = FakeWebSocketImpl.getSocketId();

        final FakeSocket serverSocket = new FakeSocket(id, port, FakeSocket.Role.SERVER);
        final FakeSocket clientSocket = new FakeSocket(id, port, FakeSocket.Role.CLIENT);

        serverSocket.socket = clientSocket;
        clientSocket.socket = serverSocket;

        final FakeWebSocket webSocket = new FakeWebSocket(clientSocket);
        clientWebSocket._setSocket(serverSocket);

        serverSocket.setListener(new IListener() {
            @Override
            public void call(String data) {
                clientWebSocket.onMessage(data);
            }
        });
        clientSocket.setListener(new IListener() {
            @Override
            public void call(String data) {
                _this.onMessage(webSocket, data);
            }
        });

        _sockets.put(id, webSocket);

        clientWebSocket.onOpen();
        onOpen(webSocket);
    }

    public final void removeWebSocket(long id, boolean remote) {
        FakeWebSocket webSocket = _sockets.get(id);
        _sockets.remove(webSocket);
        onClose(webSocket, remote);
    }

    public final Collection<FakeWebSocket> getWebSockets() {
        return _sockets.values();
    }

    public abstract void onOpen(FakeWebSocket webSocket);

    public abstract void onClose(FakeWebSocket webSocket, boolean remote);

    public abstract void onMessage(FakeWebSocket webSocket, String data);

    public abstract void onError(FakeWebSocket webSocket, Exception ex);
}
