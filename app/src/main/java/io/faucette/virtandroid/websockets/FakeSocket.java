package io.faucette.virtandroid.websockets;


import android.util.Log;

/**
 * Created by nathan on 9/1/16.
 */
public class FakeSocket {
    public enum Role {
        SERVER,
        CLIENT
    }

    private long _id;
    private int _port;
    private Role _role;
    public FakeSocket socket;
    public IListener listener;

    public FakeSocket(long id, int port, Role role) {
        _id = id;
        _port = port;
        _role = role;
        socket = null;
        listener = null;
    }

    public final long getId() {
        return _id;
    }
    public final int getPort() {
        return _port;
    }
    public final Role getRole() {
        return _role;
    }

    public final void setListener(IListener l) {
        listener = l;
    }
    public final void onMessage(String data) {
        synchronized (listener) {
            listener.call(data);
        }
    }
    public final void postMessage(String data) {
        socket.onMessage(data);
    }
}
