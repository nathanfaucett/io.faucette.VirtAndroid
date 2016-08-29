package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSArrayBuffer;

/**
 * Created by nathan on 8/28/16.
 */
public interface IJSWebSocket {

    public void close();
    public void close(int code);
    public void close(int code, String reason);

    public void send(String data);
    public void send(JSArrayBuffer data);
}
