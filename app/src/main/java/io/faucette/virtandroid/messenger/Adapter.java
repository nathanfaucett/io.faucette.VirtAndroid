package io.faucette.virtandroid.messenger;


import org.json.JSONObject;

public interface Adapter {
    public void addMessageListener(Callback callback);
    public void onMessage(String data);
    public void postMessage(String data);
}
