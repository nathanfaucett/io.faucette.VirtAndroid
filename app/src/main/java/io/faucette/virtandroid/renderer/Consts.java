package io.faucette.virtandroid.renderer;

import java.util.HashMap;

/**
 * Created by nathan on 9/6/16.
 */
public class Consts {
    public static HashMap<String, String> topLevelTypes = new HashMap<>();
    public static HashMap<String, String> propNames = new HashMap<>();
    public static HashMap<String, String> topLevelToEvent = new HashMap<>();
    public static HashMap<String, String> propNameToTopLevel = new HashMap<>();
    public static String[] eventTypes = new String[]{
            "topClick",
            "topLongClick",
            "topFocusChange",
            "topKey",
            "topTouch",
            "topCreateContextMenu"
    };

    public final static void init() {
        for (String key : eventTypes) {
            topLevelTypes.put(key, key);
            propNames.put(key, replaceTopWithOn(key));
            propNameToTopLevel.put(replaceTopWithOn(key), key);
            topLevelToEvent.put(key, removeTop(key).toLowerCase());
        }
    }

    private static String removeTop(String string) {
        return string.replaceAll("^top", "");
    }

    private static String replaceTopWithOn(String string) {
        return string.replaceAll("^top", "setOn") + "Listener";
    }
}
