package io.faucette.virtandroid.javascript;


import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.liquidplayer.webkit.javascriptcore.JSArray;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSException;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by nathan on 8/10/16.
 */
public class JSModule extends JSObject {
    private final static Pattern _reModule = Pattern.compile("(@[^\\\\/]*)?(\\\\|/)?([^\\\\/]*)(.*)?");

    private JSModule _root;
    private Activity _activity;
    private AssetManager _assetManager;


    public JSModule(JSContext context, String id, JSModule parent) {

        super(context);

        if (parent != null) {
            _root = parent._root;

            setActivity(parent._activity);
            parent.property("children").toJSArray().add(this);
        } else {
            _root = this;
        }

        property("id", id);
        property("exports", new JSObject(context));
        property("parent", parent);

        property("filename", null);
        property("loaded", false);
        property("children", new JSArray<JSModule>(context, JSModule.class));
    }

    public void setActivity(Activity activity) {
        _activity = activity;
        _assetManager = _activity.getAssets();
    }

    public JSValue require(String path) {
        boolean isNodeModule = Utils.isNodeModule(path);

        if (isNodeModule) {
            return requireModule(path);
        } else {
            return requireRelative(path);
        }
    }

    private JSValue requireModule(String path) {
        String parentFilePath = Utils.dirname(property("id").toString());
        Matcher matcher = _reModule.matcher(path);

        String scopeName = null;
        String moduleName = null;
        String relativePath = null;

        if (matcher.matches()) {
            scopeName = matcher.group(1);
            moduleName = (scopeName != null ? (scopeName + File.separatorChar) : "") + matcher.group(3);
            relativePath = matcher.group(4);
        }

        if (relativePath != null && Utils.isAbsolute(relativePath)) {
            relativePath = relativePath.substring(1);
        }

        if (moduleName == null) {
            return throwJSError("no node module found named " + path + " required from " + parentFilePath);
        } else {
            String pkgFullPath = Utils.findNodeModulePackageJSON(_assetManager, moduleName, parentFilePath);

            if (pkgFullPath != null) {
                if (relativePath != null) {
                    String fullPath = Utils.ensureExt(Utils.joinPath(Utils.dirname(pkgFullPath), relativePath));
                    String contents = Utils.loadFile(_assetManager, fullPath);

                    if (contents != null) {
                        return runContents(fullPath, contents);
                    } else {
                        return throwJSError("no file found named " + path + " required from " + parentFilePath);
                    }
                } else {
                    String contents = Utils.loadFile(_assetManager, pkgFullPath);

                    try {
                        return requirePackageJSON(pkgFullPath, contents);
                    } catch (JSONException e) {
                        return throwJSError("no node module found named " + path + " required from " + parentFilePath + " " + e.toString());
                    }
                }
            } else {
                return throwJSError("no node module found named " + path + " required from " + parentFilePath);
            }
        }
    }

    private JSValue requirePath(String path, String parentFilePath) {
        String contents = Utils.loadFile(_assetManager, Utils.ensureExt(path));

        if (contents != null) {
            return runContents(path, contents);
        } else {
            String fullPath = Utils.ensureExt(path + File.separatorChar + "index");
            contents = Utils.loadFile(_assetManager, fullPath);

            if (contents != null) {
                return runContents(fullPath, contents);
            } else {
                fullPath = path + File.separatorChar + "package.json";
                contents = Utils.loadFile(_assetManager, fullPath);

                if (contents != null) {
                    try {
                        return requirePackageJSON(fullPath, contents);
                    } catch (JSONException e) {
                        return throwJSError("no file found named " + path + " " + e.toString());
                    }
                } else {
                    return throwJSError("no file found named " + path + " required from " + parentFilePath);
                }
            }
        }
    }

    private JSValue requireRelative(String path) {
        String parentFilePath = Utils.dirname(property("id").toString());
        String joinedPath = Utils.joinPath(parentFilePath, path);
        return requirePath(joinedPath, parentFilePath);
    }

    private JSValue requirePackageJSON(String path, String contents) throws JSONException {
        JSONObject packageJSON = new JSONObject(contents);
        String index;

        if (packageJSON.has("android")) {
            index = packageJSON.getString("android");
        } else if (packageJSON.has("browser")) {
            index = packageJSON.getString("browser");
        } else {
            index = packageJSON.getString("main");
        }

        return requirePath(Utils.joinPath(Utils.dirname(path), index), Utils.dirname(path));
    }

    private JSValue runContents(String path, String contents) {
        JSContext context = getContext();

        JSFunction function = new JSFunction(
                context,
                path.replaceAll("[^a-zA-Z0-9-_]+", "_"),
                new String[] {"module", "exports", "require"},
                contents,
                path,
                1
        );

        final JSModule module = new JSModule(context, path, this);

        JSFunction require = new JSFunction(context, "require") {
            public JSValue require(JSValue path) {
                return module.require(path.toString());
            }
        };

        function.call(module, module, module.property("exports"), require);

        return module.property("exports");
    }

    private JSValue throwJSError(String message) {
        JSContext context = getContext();
        context.throwJSException(new JSException(new JSValue(context, message)));
        return new JSObject(context);
    }
}
