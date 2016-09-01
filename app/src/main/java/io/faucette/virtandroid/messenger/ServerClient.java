package io.faucette.virtandroid.messenger;


/**
 * Created by nathan on 8/31/16.
 */
public class ServerClient {
    public SimpleAdapter server;
    public SimpleAdapter client;

    public ServerClient(SimpleAdapter s, SimpleAdapter c) {
        server = s;
        client = c;
    }
}
