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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.faucette.virtandroid.Utils;


/**
 * Created by nathan on 8/10/16.
 */
public class JSModule extends JSObject {
    private final static Pattern _reModule = Pattern.compile("(@[^\\\\/]*)?(\\\\|/)?([^\\\\/]*)(.*)?");

    private JSModule _root;
    private HashMap<String, JSModule> _cache;
    private Activity _activity;
    private AssetManager _assetManager;


    public JSModule(JSContext ctx, String id, JSModule parent) {

        super(ctx);

        if (parent != null) {
            _root = parent._root;
            _cache = _root._cache;

            setActivity(parent._activity);
            parent.property("children").toJSArray().add(this);
        } else {
            _root = this;
            _cache = new HashMap<>();
        }

        property("id", id);
        property("exports", new JSObject(ctx));
        property("parent", parent);

        property("filename", id);
        property("loaded", false);
        property("children", new JSArray<JSModule>(ctx, JSModule.class));
    }

    private static String _findNodeModulePackageJSON(AssetManager assetManager, String moduleName, String requiredFromDirname) {
        String id = Utils.joinPath("node_modules", Utils.joinPath(moduleName, "package.json"));
        String root = requiredFromDirname;
        int depth = Utils.normalize(root).split(File.separator).length;
        String fullPath = Utils.joinPath(root, id);

        if (Utils.hasFile(assetManager, fullPath)) {
            return fullPath;
        } else {
            while (depth-- >= 0) {
                fullPath = Utils.joinPath(root, id);
                root = Utils.joinPath(root, "..");

                if (Utils.hasFile(assetManager, fullPath)) {
                    return fullPath;
                }
            }
            return null;
        }
    }

    public void setActivity(Activity activity) {
        _activity = activity;
        _assetManager = _activity.getAssets();
    }

    public JSValue require(String path) {
        boolean isNodeModule = Utils.isNodeModule(path);
        String origPath = path;

        if (isNodeModule) {
            path = resolveModule(path);
        } else {
            path = resolveRelative(path);
        }

        if (path != null) {
            if (_cache.containsKey(path)) {
                return _cache.get(path).property("exports");
            } else {
                JSModule module = new JSModule(getContext(), path, this);
                _cache.put(path, module);
                module.run();
                return module.property("exports");
            }
        } else {
            throwJSError(
                    "no " + (isNodeModule ? "node module" : "file") +
                            " found named " + origPath + " required from " +
                            Utils.dirname(property("id").toString())
            );
            return null;
        }
    }

    private String resolveModule(String path) {
        String parentDirname = Utils.dirname(property("id").toString());
        Matcher matcher = _reModule.matcher(path);

        String scopeName = null;
        String moduleName = null;
        String relativePath = null;

        if (matcher.matches()) {
            scopeName = matcher.group(1);
            moduleName = (scopeName != null ? (scopeName + File.separatorChar) : "") + matcher.group(3);
            relativePath = matcher.group(4);
        }

        if (relativePath != null && relativePath.equals("")) {
            relativePath = null;
        }
        if (relativePath != null && Utils.isAbsolute(relativePath)) {
            relativePath = relativePath.substring(1);
        }

        if (moduleName != null) {
            String pkgFullPath = _findNodeModulePackageJSON(_assetManager, moduleName, parentDirname);

            if (pkgFullPath != null) {
                if (relativePath != null) {
                    String fullPath = Utils.ensureExt(Utils.joinPath(Utils.dirname(pkgFullPath), relativePath));

                    if (Utils.hasFile(_assetManager, fullPath)) {
                        return fullPath;
                    } else {
                        return null;
                    }
                } else {
                    String contents = Utils.loadFile(_assetManager, pkgFullPath);

                    try {
                        return resolvePackageJSON(pkgFullPath, contents);
                    } catch (JSONException e) {
                        throwJSError(e.toString());
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private String resolvePath(String path) {
        String fullPath = Utils.ensureExt(path);

        if (!Utils.hasFile(_assetManager, fullPath)) {
            fullPath = Utils.ensureExt(path + File.separatorChar + "index");

            if (!Utils.hasFile(_assetManager, fullPath)) {
                fullPath = path + File.separatorChar + "package.json";

                if (Utils.hasFile(_assetManager, fullPath)) {
                    String contents = Utils.loadFile(_assetManager, fullPath);

                    try {
                        return resolvePackageJSON(fullPath, contents);
                    } catch (JSONException e) {
                        throwJSError(e.toString());
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return fullPath;
            }
        } else {
            return fullPath;
        }
    }

    private String resolveRelative(String path) {
        String parentFilePath = Utils.dirname(property("id").toString());
        String joinedPath = Utils.joinPath(parentFilePath, path);
        return resolvePath(joinedPath);
    }

    private String resolvePackageJSON(String path, String contents) throws JSONException {
        JSONObject packageJSON = new JSONObject(contents);
        String index;

        if (packageJSON.has("android")) {
            index = packageJSON.getString("android");
        } else if (packageJSON.has("browser")) {
            index = packageJSON.getString("browser");
        } else {
            index = packageJSON.getString("main");
        }

        return resolvePath(Utils.joinPath(Utils.dirname(path), index));
    }

    private void run() {
        final JSModule _this = this;
        String id = property("id").toString();
        String contents = Utils.loadFile(_assetManager, id);
        JSContext ctx = getContext();

        JSFunction function = new JSFunction(
                ctx,
                id.replaceAll("[^a-zA-Z0-9]+", "_"),
                new String[]{"module", "exports", "require"},
                contents,
                id,
                1
        );

        JSFunction require = new JSFunction(ctx, "require") {
            public JSValue require(JSValue path) {
                return _this.require(path.toString());
            }
        };

        function.call(_this, _this, _this.property("exports"), require);

        _this.property("loaded", new JSValue(ctx, true));
    }

    private void throwJSError(String message) {
        JSContext ctx = getContext();
        Log.e("JSModule", message);
        ctx.throwJSException(new JSException(new JSValue(ctx, message)));
    }
}
