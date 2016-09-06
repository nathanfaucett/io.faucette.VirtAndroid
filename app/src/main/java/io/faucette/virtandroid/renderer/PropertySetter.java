package io.faucette.virtandroid.renderer;

import android.view.View;

import org.json.JSONObject;

/**
 * Created by nathan on 9/5/16.
 */
public interface PropertySetter {
    void set(View view, JSONObject props, String origProp, String normProp) throws Exception;
}
