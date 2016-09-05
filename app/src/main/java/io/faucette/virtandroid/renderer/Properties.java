package io.faucette.virtandroid.renderer;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nathan on 9/5/16.
 */
public class Properties {
    private static final Pattern re4Ints = Pattern.compile("([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)");


    public static final void setLayoutWidth(View view, JSONObject props, String prop) throws Exception {
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }

        params.width = _getLayout(props.getString("layout_width"));
    }
    public static final void setLayoutHeight(View view, JSONObject props, String prop) throws Exception {
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }

        params.height = _getLayout(props.getString("layout_height"));
    }

    public static final void setPadding(View view, JSONObject props, String prop) throws Exception {
        int[] padding = _parseInts4(props.getString("padding"));
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }
    public static final void setPaddingRelative(View view, JSONObject props, String prop) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int[] padding = _parseInts4(props.getString("padding_relative"));
            view.setPaddingRelative(padding[0], padding[1], padding[2], padding[3]);
        }
    }

    public static final void setZ(View view, JSONObject props, String prop) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setZ(new Float(props.getDouble("z")));
        }
    }

    public static final void setAlpha(View view, JSONObject props, String prop) throws Exception {
        view.setAlpha((float) props.getDouble("alpha"));
    }
    public static final void setBackgroundColor(View view, JSONObject props, String prop) throws Exception {
        view.setBackgroundColor(props.getInt("background_color"));
    }


    private static final int _getLayout(String layout) {
        if (layout.equals("fill_parent")) {
            return ViewGroup.LayoutParams.FILL_PARENT;
        } else if (layout.equals("match_parent")) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }
    private static final int[] _parseInts4(String s) {
        Matcher m = re4Ints.matcher(s);
        int[] ints = new int[] { 0, 0, 0, 0 };

        if (m.matches()) {
            String tmp = m.group(1);
            ints[0] = tmp != null ? new Integer(tmp) : 0;
            tmp = m.group(2);
            ints[1] = tmp != null ? new Integer(tmp) : 0;
            tmp = m.group(3);
            ints[2] = tmp != null ? new Integer(tmp) : 0;
            tmp = m.group(4);
            ints[3] = tmp != null ? new Integer(tmp) : 0;
            return ints;
        } else {
            return ints;
        }
    }
}
