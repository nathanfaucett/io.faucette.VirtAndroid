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
import org.liquidplayer.webkit.javascriptcore.JSON;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by nathan on 8/10/16.
 */
public class JSModule extends JSObject {
    private Activity _activity;
    private AssetManager _assetManager;


    public JSModule(JSContext context, String id, JSModule parent) {

        super(context);

        if (parent != null) {
            setActivity(parent._activity);
        }

        property("id", id);
        property("exports", new JSObject(context));
        property("parent", parent);

        if (parent != null) {
            parent.property("children").toJSArray().add(this);
        }

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

        try {
            path = new URI(path).normalize().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (isNodeModule) {
            return requireModule(path);
        } else {
            return requireRelative(path);
        }
    }

    private JSValue requireModule(String path) {
        throwJSError("Modules not supported yet");
        return new JSObject();
    }

    private JSValue requireRelative(String path) {
        String parentFilePath = new File(property("id").toString()).getParent();
        parentFilePath = parentFilePath == null ? "." : parentFilePath;
        String joinedPath = new File(parentFilePath, path).getPath();
        String contents = Utils.loadFile(_assetManager, Utils.ensureExt(joinedPath));

        if (contents != null) {
            return runContents(joinedPath, contents);
        } else {
            String indexPath = Utils.ensureExt(joinedPath + "/index");
            contents = Utils.loadFile(_assetManager, indexPath);

            if (contents != null) {
                return runContents(indexPath, contents);
            } else {
                indexPath = joinedPath + "/package.json";
                contents = Utils.loadFile(_assetManager, indexPath);

                if (contents != null) {
                    try {
                        return requirePackageJSON(indexPath, contents);
                    } catch (JSONException e) {
                        throwJSError("no file found name " + path + " " + e.toString());
                        return new JSObject();
                    }
                } else {
                    throwJSError("no file found name " + path + " from " + parentFilePath);
                    return new JSObject();
                }
            }
        }
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

        return requireRelative(index);
    }

    private JSValue runContents(String path, String contents) {
        JSContext context = getContext();

        JSFunction function = new JSFunction(
                context,
                path.replace('.', '_').replace('/', '_').replace('\\', '_'),
                new String[] {"module", "exports", "require"},
                contents,
                path,
                1
        );

        final JSModule module = new JSModule(context, path, this);
        JSFunction require = new JSFunction(context, "require") {
            public JSValue require(String path) {
                return module.require(path.toString());
            }
        };

        function.call(module, module, module.property("exports"), require);

        return module.property("exports");
    }

    private JSValue throwJSError(String message) {
        JSContext context = getContext();
        context.throwJSException(new JSException(new JSValue(context, message)));
        return new JSObject();
    }
}
