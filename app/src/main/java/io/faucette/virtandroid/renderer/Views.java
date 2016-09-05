package io.faucette.virtandroid.renderer;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;


/**
 * Created by nathan on 9/1/16.
 */
public class Views {
    private static final Pattern re4Ints = Pattern.compile("([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)[^0-9]+([0-9]+)");

    private static final HashMap<String, View> _views = new HashMap<>();
    private static final HashMap<String, PropertySetter> _properties = new HashMap<>();
    private static final HashMap<String, Class<? extends View>> _viewClasses = new HashMap<>();


    public static final View getViewById(String id) {
        return _views.get(id);
    }

    public static final void setPropertySetter(String prop, PropertySetter setter) throws Exception {
        if (_properties.containsKey(prop)) {
            throw new Exception("PropertySetter " + prop + " already added");
        } else {
            _properties.put(prop, setter);
        }
    }

    public static final void addViewClass(String type, Class<? extends View> view) throws Exception {
        if (_viewClasses.containsKey(type)) {
            throw new Exception("View " + type + " already added");
        } else {
            _viewClasses.put(type, view);
        }
    }

    public static final Class<? extends View> getViewClass(String type) throws Exception {
        if (!_viewClasses.containsKey(type)) {
            throw new Exception("View " + type + " does not exists, add it with Views.addViewClass(String type, Class<? extends View> view)");
        } else {
            return _viewClasses.get(type);
        }
    }

    public static final View create(Context context, String typeString, String id) throws Exception {
        Class<? extends View> type = getViewClass(typeString);
        Constructor<? extends View> constructor = type.getConstructor(Context.class);
        View view = constructor.newInstance(context);
        _views.put(id, view);
        return view;
    }

    public static final void init() throws Exception {
        addViewClass("TextView", TextView.class);
        addViewClass("Button", Button.class);
        addViewClass("LinearLayout", LinearLayout.class);
        addViewClass("RelativeLayout", RelativeLayout.class);

        setPropertySetter("layout_width", new PropertySetter() {
            @Override
            public void set(View view, JSONObject props, String prop) throws Exception {
                Properties.setLayoutWidth(view, props, prop);
            }
        });
        setPropertySetter("layout_height", new PropertySetter() {
            @Override
            public void set(View view, JSONObject props, String prop) throws Exception {
                Properties.setLayoutHeight(view, props, prop);
            }
        });

        setPropertySetter("padding", new PropertySetter() {
            @Override
            public void set(View view, JSONObject props, String prop) throws Exception {
                Properties.setPadding(view, props, prop);
            }
        });
        setPropertySetter("z", new PropertySetter() {
            @Override
            public void set(View view, JSONObject props, String prop) throws Exception {
                Properties.setZ(view, props, prop);
            }
        });

        setPropertySetter("alpha", new PropertySetter() {
            @Override
            public void set(View view, JSONObject props, String prop) throws Exception {
                Properties.setAlpha(view, props, prop);
            }
        });
        setPropertySetter("background_color", new PropertySetter() {
            @Override
            public void set(View view, JSONObject props, String prop) throws Exception {
                Properties.setBackgroundColor(view, props, prop);
            }
        });
    }

    public static final void setProps(View view, JSONObject props) throws Exception {
        Iterator<String> it = props.keys();

        while (it.hasNext()) {
            String prop = it.next();

            if (!props.isNull(prop) && _properties.containsKey(prop)) {
                PropertySetter setter = _properties.get(prop);
                setter.set(view, props, prop);
            }
        }
    }

    public static final View createFromJSON(Context context, JSONObject view, JSONObject parentProps, String id) throws Exception {
        String type = view.getString("type");
        JSONObject props = view.getJSONObject("props");
        JSONArray children = view.getJSONArray("children");

        ViewGroup viewChildren = null;
        View viewNoChildren = null;

        if (ViewGroup.class.isAssignableFrom(getViewClass(type))) {
            viewChildren = (ViewGroup) create(context, type, id);
        } else {
            viewNoChildren = (View) create(context, type, id);
        }

        if (viewChildren != null) {
            setProps(viewChildren, props);
            _addJSONChildren(context, viewChildren, props, id, children);
            return viewChildren;
        } else {
            setProps(viewNoChildren, props);
            return viewNoChildren;
        }
    }

    public static final View createFromJSON(Context context, String view, JSONObject parentProps, String id) throws Exception {

        TextView textView = (TextView) create(context, "TextView", id);
        textView.setText(view);

        return textView;
    }

    public static final View createFromJSON(Context context, Number view, JSONObject parentProps, String id) throws Exception {
        return createFromJSON(context, view.toString(), parentProps, id);
    }

    private static void _addJSONChildren(Context context, ViewGroup view, JSONObject props, String id, JSONArray children) throws Exception {
        for (int i = 0, il = children.length(); i < il; i++) {
            Object child = children.get(i);

            if (child instanceof JSONObject) {
                JSONObject jsonChild = (JSONObject) child;
                view.addView(createFromJSON(context, jsonChild, props, id + "." + _getViewKey(jsonChild, i)));
            } else if (child instanceof Number) {
                view.addView(createFromJSON(context, (Number) child, props, id + "." + i));
            } else {
                view.addView(createFromJSON(context, (String) child, props, id + "." + i));
            }
        }
    }

    private static String _getViewKey(JSONObject view, int index) {
        if (!view.isNull("key")) {
            try {
                return view.getString("key").replace("\\.", "$");
            } catch (JSONException e) {
                return index + "";
            }
        } else {
            return index + "";
        }
    }
}
