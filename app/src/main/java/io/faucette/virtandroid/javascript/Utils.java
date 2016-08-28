package io.faucette.virtandroid.javascript;


import android.content.res.AssetManager;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.net.URI;
import java.util.Scanner;


/**
 * Created by nathan on 8/10/16.
 */
public class Utils {

    public static String loadFile(AssetManager assetManager, String path) {
        try {
            Scanner scanner = new Scanner(
                    assetManager.open(
                            new URI(path).normalize().toString()
                    )
            ).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {}
        return null;
    }

    public static boolean isNodeModule(String path) {
        return path.charAt(0) != '.' && path.charAt(0) != '/';
    }

    public static String ensureExt(String path) {
        try {
            path = new URI(path).normalize().toString();
        } catch (Exception e) {}

        int i = path.lastIndexOf('.');

        if (i >= 0) {
            return path;
        } else {
            return path + ".js";
        }
    }

    public static String arrayToString(Object[] array) {
        String message = "";

        if (array != null) {
            for (int i = 0, il = array.length; i < il; i++) {
                Object arg = array[i];

                if (arg != null) {
                    message += " " + arg.toString();
                }
            }
        }

        return message;
    }
}
