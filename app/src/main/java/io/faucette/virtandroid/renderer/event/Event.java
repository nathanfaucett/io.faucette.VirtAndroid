package io.faucette.virtandroid.renderer.event;


import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import io.faucette.virtandroid.renderer.Views;

/**
 * Created by nathan on 9/6/16.
 */
public class Event {
    private String _id;
    private View _view;
    private String _topLevelType;


    public Event(View view, String topLevelType) {
        _id = Views.getIdByView(view);
        _view = view;
        _topLevelType = topLevelType;
    }

    public final String getId() {
        return _id;
    }

    public final String getTopLevelTypeType() {
        return _topLevelType;
    }

    public final JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", _id);
        json.put("topLevelType", _topLevelType);
        return json;
    }
}
