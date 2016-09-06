package io.faucette.virtandroid;

import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nathan on 9/6/16.
 */
public class Utils {
    private static final Pattern _reInflect = Pattern.compile("[^A-Z-_ ]+|[A-Z][^A-Z-_ ]+|[^a-z-_ ]+");


    public static boolean hasFile(AssetManager assetManager, String path) {
        try {
            assetManager.open(normalize(path));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static String loadFile(AssetManager assetManager, String path) {
        String contents = null;
        try {
            Scanner scanner = new Scanner(assetManager.open(normalize(path))).useDelimiter("\\A");
            contents = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            e.printStackTrace();
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
            return normalize(file.getPath());
        }
    }

    public static String dirname(String path) {
        String dirname = new File(path).getParent();
        return dirname == null ? "." : normalize(dirname);
    }

    public static String normalize(String path) {
        path = path == null ? "." : path;
        try {
            path = new URI(path).normalize().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            path = ".";
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

    public static String capitalizeString(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String camelCase(String string, boolean lowFirstLetter) {
        Matcher matcher = _reInflect.matcher(string);
        ArrayList<String> parts = new ArrayList<>();

        while (matcher.find()) {
            parts.add(matcher.group());
        }

        int i = 0;
        int il = parts.size();

        if (lowFirstLetter) {
            i = 1;
        }

        for (; i < il; i++) {
            parts.set(i, capitalizeString(parts.get(i)));
        }

        return TextUtils.join("", parts);
    }

    public static String camelCase(String string) {
        return camelCase(string, true);
    }
}
