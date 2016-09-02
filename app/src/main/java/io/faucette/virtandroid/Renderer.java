package io.faucette.virtandroid;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import io.faucette.virtandroid.messenger.Callback;
import io.faucette.virtandroid.messenger.Messenger;
import io.faucette.virtandroid.messenger.SimpleAdapter;

/**
 * Created by nathan on 8/29/16.
 */
public class Renderer {
    private Messenger _messenger;


    public Renderer(Server server) {
        final Renderer _this = this;

        _messenger = new Messenger(new WebSocketAdapter(server));

        _messenger.on("virt.handleTransaction", new Callback() {
            @Override
            public void call(JSONObject data, Callback callback) {
                try {
                    _this._applyPatches(data.getJSONObject("patches"));
                    _this._applyEvents(data.getJSONObject("events"));
                    _this._applyPatches(data.getJSONObject("removes"));
                } catch (JSONException e) {
                    Log.i("Renderer", e.toString());
                }

                callback.call(null, (JSONObject) null);
            }
        });
    }

    private void _applyPatches(JSONObject patches) throws JSONException {
        Iterator<String> it = patches.keys();

        while (it.hasNext()) {
            String id = it.next();
            JSONArray array = patches.getJSONArray(id);

            for (int i = 0, il = array.length(); i < il; i++) {
                _applyPatch(id, array.getJSONObject(i));
            }
        }
    }
    private void _applyEvents(JSONObject events) {
    }

    private void _applyPatch(String id, JSONObject patch) throws JSONException {
        String type = patch.getString("type");

        if (type.equals("MOUNT")) {
            _mount(patch.getJSONObject("next"), id);
        } else if (type.equals("UNMOUNT")) {
            // unmount patch
        } else if (type.equals("INSERT")) {
            // insert patch
        } else if (type.equals("REMOVE")) {
            // remove patch
        } else if (type.equals("REPLACE")) {
            // replace patch
        } else if (type.equals("TEXT")) {
            _text(patch.getString("next"), patch.getInt("index"), id);
        } else if (type.equals("ORDER")) {
            // order patch
        } else if (type.equals("PROPS")) {
            // props patch
        }
    }

    private void _mount(JSONObject next, String id) {
        Log.i("Renderer", "MOUNT next: " + next + " id: " + id);
    }
    private void _text(String text, int index, String id) {
        Log.i("Renderer", "TEXT next: " + text + " child index: " + index + " id: " + id);
    }
}
