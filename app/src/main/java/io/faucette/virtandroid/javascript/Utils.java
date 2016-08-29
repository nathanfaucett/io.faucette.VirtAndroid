package io.faucette.virtandroid.javascript;


import android.content.res.AssetManager;
import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSArrayBuffer;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSUint8Array;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;


/**
 * Created by nathan on 8/10/16.
 */
public class Utils {

    public static boolean hasFile(AssetManager assetManager, String path) {
        try {
            assetManager.open(Utils.normalize(path));
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static String loadFile(AssetManager assetManager, String path) {
        String contents = null;
        try {
            Scanner scanner = new Scanner(assetManager.open(Utils.normalize(path))).useDelimiter("\\A");
            contents = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            Log.e("Utils", e.toString());
        }
        return contents;
    }

    public static boolean isAbsolute(String path) {
        File file = new File(path);
        return file == null ? false : file.isAbsolute();
    }

    public static boolean isNodeModule(String path) {
        return path == null ? false : path.charAt(0) != '.' && path.charAt(0) != File.separatorChar;
    }

    public static String joinPath(String a, String b) {
        File file = new File(a, b);

        if (file == null) {
            return ".";
        } else {
            return Utils.normalize(file.getPath());
        }
    }

    public static String dirname(String path) {
        String dirname = new File(path).getParent();
        return dirname == null ? "." : Utils.normalize(dirname);
    }

    public static String normalize(String path) {
        path = path == null ? "." : path;
        try {
            path = new URI(path).normalize().toString();
        } catch (URISyntaxException e) {
            path = ".";
            Log.e("Utils", e.toString());
        }
        return path.equals("") ? "." : path;
    }

    public static String ensureExt(String path) {
        int i = path.lastIndexOf('.');

        if (i >= 0) {
            return path;
        } else {
            return path + ".js";
        }
    }

    public static String findNodeModulePackageJSON(AssetManager assetManager, String moduleName, String requiredFromDirname) {
        String id = Utils.joinPath("node_modules", Utils.joinPath(moduleName, "package.json"));
        String root = requiredFromDirname;
        int depth = Utils.normalize(root).split(File.separator).length;
        String fullPath = Utils.joinPath(root, id);

        if (Utils.loadFile(assetManager, fullPath) != null) {
            return fullPath;
        } else {
            while (depth-- >= 0) {
                fullPath = Utils.joinPath(root, id);
                root = Utils.joinPath(root, "..");

                if (Utils.loadFile(assetManager, fullPath) != null) {
                    return fullPath;
                }
            }
            return null;
        }
    }

    public static String arrayToString(Object[] array) {
        String message = "";

        if (array != null) {
            Object arg = array[0];

            if (arg != null) {
                message += arg.toString();
            }

            for (int i = 1, il = array.length; i < il; i++) {
                arg = array[i];

                if (arg != null) {
                    message += ", " + arg.toString();
                }
            }
        }

        return message;
    }

    public static String getEncodeing(String encoding) {
        encoding = encoding.toLowerCase();

        if (encoding.equals("ascii")) {
            return "US-ASCII";
        } else {
            return "UTF-8";
        }
    }

    public static JSUint8Array toUint8Array(JSContext ctx, String string, String encoding) {
        byte[] bytes = null;

        try {
            bytes = string.getBytes(getEncodeing(encoding));
        } catch (UnsupportedEncodingException e) {
            Log.e("Utils", e.toString());
        }

        if (bytes != null) {
            return new JSUint8Array(ctx, bytes);
        } else {
            return new JSUint8Array(ctx, 0);
        }
    }
}
