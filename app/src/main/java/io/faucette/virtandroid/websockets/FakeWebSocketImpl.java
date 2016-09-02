package io.faucette.virtandroid.websockets;

import java.util.HashMap;

/**
 * Created by nathan on 9/1/16.
 */
public class FakeWebSocketImpl {
    private final static HashMap<Integer, FakeWebSocketServer> _servers = new HashMap<>();
    private static long SOCKET_ID = 0;

    public final static void listen(FakeWebSocketServer server, int port) throws Exception {
        if (_servers.containsKey(port)) {
            throw new Exception("port already in use");
        } else {
            _servers.put(port, server);
        }
    }

    public final static long getSocketId() {
        return SOCKET_ID++;
    }

    public final static void open(int port, FakeWebSocketClient clientWebSocket) throws Exception {
        if (_servers.containsKey(port)) {
            FakeWebSocketServer server = _servers.get(port);
            server.createWebSocket(port, clientWebSocket);
        } else {
            throw new Exception("port is not being listening to");
        }
    }

    public final static void close(long id, int port, boolean remote) {
        if (_servers.containsKey(port)) {
            FakeWebSocketServer server = _servers.get(port);
            server.removeWebSocket(id, remote);
        }
    }
}
