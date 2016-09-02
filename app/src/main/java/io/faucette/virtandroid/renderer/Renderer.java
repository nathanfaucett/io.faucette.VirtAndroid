package io.faucette.virtandroid.renderer;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

import io.faucette.virtandroid.Server;
import io.faucette.virtandroid.WebSocketAdapter;
import io.faucette.virtandroid.messenger.Callback;
import io.faucette.virtandroid.messenger.Messenger;

/**
 * Created by nathan on 8/29/16.
 */
public class Renderer {
    private ViewGroup _root;
    private Activity _activity;
    private Context _context;
    private Messenger _messenger;


    public Renderer(final Activity activity, ViewGroup root, Server server) {
        final Renderer _this = this;

        _root = root;
        _activity = activity;
        _context = (Context) activity.getApplicationContext();
        _messenger = new Messenger(new WebSocketAdapter(activity, server));

        _messenger.on("virt.handleTransaction", new Callback() {
            @Override
            public void call(final JSONObject data, final Callback callback) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            _this._applyPatches(data.getJSONObject("patches"));
                            _this._applyEvents(data.getJSONObject("events"));
                            _this._applyPatches(data.getJSONObject("removes"));
                        } catch (Exception e) {
                            Log.i("Renderer", e.toString());
                        }
                    }
                });
                callback.call(null, (JSONObject) null);
            }
        });
    }

    private void _applyPatches(JSONObject patches) throws Exception {
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

    private void _applyPatch(String id, JSONObject patch) throws Exception {
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

    private void _mount(JSONObject next, String id) throws Exception {
        View view = Views.createFromJSON(_context, next, null, id);
        _root.addView(view);
    }

    private void _text(String text, int index, String id) throws Exception {
        TextView view = (TextView) Views.getViewById(id);
        view.setText(text);
    }
}