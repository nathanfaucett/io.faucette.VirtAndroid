package io.faucette.virtandroid.renderer;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nathan on 9/5/16.
 */
public class Properties {
    private static final Pattern re4Ints = Pattern.compile("([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)");


    public static void setLayoutWidth(View view, JSONObject props, String origProp, String normProp) throws Exception {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int layout = _getLayout(props.getString(origProp));

        if (params == null) {
            params = new ViewGroup.LayoutParams(layout, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        } else {
            params.width = _getLayout(props.getString(origProp));
        }
    }

    public static void setLayoutHeight(View view, JSONObject props, String origProp, String normProp) throws Exception {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int layout = _getLayout(props.getString(origProp));

        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, layout);
            view.setLayoutParams(params);
        } else {
            params.height = layout;
        }
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

    public static void setText(View view, JSONObject props, String origProp, String normProp) throws Exception {
        if (view instanceof TextView) {
            ((TextView) view).setText(props.getString(origProp));
        }
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

    private static int[] _parseLeftTopRightBottom(JSONObject props, String prop) throws JSONException {
        Matcher m = re4Ints.matcher(props.getString(prop));
        int[] ints = new int[]{0, 0, 0, 0};

        if (m.matches()) {
            String tmp = m.group(1);
            ints[0] = tmp != null ? Integer.valueOf(tmp) : _getPropInt(props, prop + "Left");
            tmp = m.group(2);
            ints[1] = tmp != null ? Integer.valueOf(tmp) : _getPropInt(props, prop + "Top");
            tmp = m.group(3);
            ints[2] = tmp != null ? Integer.valueOf(tmp) : _getPropInt(props, prop + "Right");
            tmp = m.group(4);
            ints[3] = tmp != null ? Integer.valueOf(tmp) : _getPropInt(props, prop + "Bottom");
            return ints;
        } else {
            return ints;
        }
    }

    private static int _getPropInt(JSONObject props, String prop) {
        if (props.has(prop)) {
            try {
                return props.getInt(prop);
            } catch (Exception ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
