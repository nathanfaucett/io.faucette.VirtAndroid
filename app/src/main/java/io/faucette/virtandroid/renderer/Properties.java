package io.faucette.virtandroid.renderer;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nathan on 9/5/16.
 */
public class Properties {
    private static final Pattern re4Ints = Pattern.compile("([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)");


    public static Object jsonGet(JSONObject json, String key) {
        if (json.has(key)) {
            try {
                return json.get(key);
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }
    public static Object jsonGet(JSONObject json, String key1, String key2) {
        Object value = jsonGet(json, key1);

        if (value == null) {
            return jsonGet(json, key2);
        } else {
            return value;
        }
    }

    public static void setLayoutWidth(View view, JSONObject props, String origProp, String normProp) throws Exception {
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }

        params.width = _getLayout(props.getString(origProp));
    }
    public static void setLayoutHeight(View view, JSONObject props, String origProp, String normProp) throws Exception {
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }

        params.height = _getLayout(props.getString(origProp));
    }

    public static void setPadding(View view, JSONObject props, String origProp, String normProp) throws Exception {
        int[] padding = _parseLeftTopRightBottom(props, origProp);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }
    public static void setPaddingRelative(View view, JSONObject props, String origProp, String normProp) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int[] padding = _parseLeftTopRightBottom(props, origProp);
            view.setPaddingRelative(padding[0], padding[1], padding[2], padding[3]);
        }
    }

    public static void setZ(View view, JSONObject props, String origProp, String normProp) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setZ(new Float(props.getDouble(origProp)));
        }
    }

    public static void setAlpha(View view, JSONObject props, String origProp, String normProp) throws Exception {
        view.setAlpha((float) props.getDouble(origProp));
    }
    public static void setBackgroundColor(View view, JSONObject props, String origProp, String normProp) throws Exception {
        view.setBackgroundColor(props.getInt(origProp));
    }


    private static int _getLayout(String layout) {
        if (layout.equals("fill_parent")) {
            return ViewGroup.LayoutParams.FILL_PARENT;
        } else if (layout.equals("match_parent")) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }
    private static int[] _parseLeftTopRightBottom(JSONObject props, String prop) {
        Matcher m = re4Ints.matcher((String) jsonGet(props, prop));
        int[] ints = new int[] { 0, 0, 0, 0 };

        if (m.matches()) {
            String tmp = m.group(1);
            ints[0] = tmp != null ? Integer.valueOf(tmp) : 0;
            tmp = m.group(2);
            ints[1] = tmp != null ? Integer.valueOf(tmp) : 0;
            tmp = m.group(3);
            ints[2] = tmp != null ? Integer.valueOf(tmp) : 0;
            tmp = m.group(4);
            ints[3] = tmp != null ? Integer.valueOf(tmp) : 0;
            return ints;
        } else {
            return ints;
        }
    }
}
