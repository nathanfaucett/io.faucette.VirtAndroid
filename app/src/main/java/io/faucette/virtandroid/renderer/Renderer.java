package io.faucette.virtandroid.renderer;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import io.faucette.messenger.Callback;
import io.faucette.messenger.Messenger;
import io.faucette.virtandroid.Server;
import io.faucette.virtandroid.WebSocketAdapter;

/**
 * Created by nathan on 8/29/16.
 */
public class Renderer {
    private ViewGroup _root;
    private Activity _activity;
    private Context _context;
    private Messenger _messenger;
    private EventHandler _eventHandler;


    public Renderer(Activity activity, ViewGroup root, Server server) {
        final Renderer _this = this;

        _root = root;
        _activity = activity;
        _context = activity.getApplicationContext();
        _messenger = new Messenger(new WebSocketAdapter(activity, server));
        _eventHandler = new EventHandler(_messenger);

        _messenger.on("virt.handleTransaction", new Callback() {
            @Override
            public void call(final JSONObject data) {
                try {
                    _this._applyPatches(data.getJSONObject("patches"));
                    _this._applyEvents(data.getJSONObject("events"));
                    _this._applyPatches(data.getJSONObject("removes"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void _applyPatches(JSONObject patches) throws Exception {
        Iterator<String> it = patches.keys();

        while (it.hasNext()) {
            String id = it.next();
            JSONArray array = patches.getJSONArray(id);

            for (int i = 0, il = array.length(); i < il; i++) {
                try {
                    _applyPatch(id, array.getJSONObject(i));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void _applyEvents(JSONObject events) throws Exception {
        Iterator<String> keys = events.keys();

        while (keys.hasNext()) {
            String id = keys.next();
            JSONArray eventArray = events.getJSONArray(id);

            for (int i = 0, il = eventArray.length(); i < il; i++) {
                _eventHandler.listenTo(id, eventArray.getString(i));
            }
        }
    }

    private void _applyPatch(String id, JSONObject patch) throws Exception {
        String type = patch.getString("type");

        if (type.equals("MOUNT")) {
            _mount(patch.getJSONObject("next"), id);
        } else if (type.equals("UNMOUNT")) {
            _unmount(id);
        } else if (type.equals("INSERT")) {
            _insert(patch.getInt("index"), patch.getJSONObject("next"), patch.getString("childId"), patch.getString("id"));
        } else if (type.equals("REMOVE")) {
            String childId = null;
            if (patch.has("childId")) {
                childId = patch.getString("childId");
            }
            _remove(patch.getInt("index"), id, childId);
        } else if (type.equals("REPLACE")) {
            // replace patch
        } else if (type.equals("TEXT")) {
            _text(patch.getString("next"), patch.getInt("index"), id);
        } else if (type.equals("ORDER")) {
            // order patch
        } else if (type.equals("PROPS")) {
            _props(id, patch.getJSONObject("next"), patch.getJSONObject("previous"));
        }
    }

    private void _mount(JSONObject next, String id) throws Exception {
        _root.removeAllViews();
        _root.addView(Views.createFromJSON(_context, next, null, id));
    }

    private void _unmount(String id) throws Exception {
        View view = _root.getChildAt(0);

        if (view != null) {
            Views.removeViews(_root.getChildAt(0));
        }

        _root.removeAllViews();
    }

    private void _insert(int index, JSONObject next, String childId, String id) throws Exception {
        ViewGroup parentView = (ViewGroup) Views.getViewById(id);
        View view = Views.createFromJSON(_context, next, null, id);
        parentView.addView(view);
    }

    private void _remove(int index, String id, String childId) throws Exception {
        View parentView = Views.getViewById(id);

        if (parentView instanceof ViewGroup) {
            ViewGroup parentViewGroup = ((ViewGroup) parentView);
            Views.removeViews(parentViewGroup.getChildAt(index));
            parentViewGroup.removeViewAt(index);
        }
    }

    private void _text(String text, int index, String id) throws Exception {
        View view = Views.getViewById(id);

        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            TextView textView = (TextView) viewGroup.getChildAt(index);
            textView.setText(text);
        }
    }

    private void _props(String id, JSONObject next, JSONObject previous) throws Exception {
        Views.setProps(Views.getViewById(id), next);
    }
}
