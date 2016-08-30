package io.faucette.virtandroid.javascript;

import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by nathan on 8/29/16.
 */
public class JSBuffer extends JSFunction {

    /* required by AndroidJSCore */
    public JSBuffer() {}

    public JSBuffer(JSContext ctx) throws NoSuchMethodException {

        super(ctx, JSBuffer.class.getMethod("constructor"), JSBuffer.class);

        JSObject proto = new JSObject(ctx);

        proto.property("isBuffer", new JSFunction(ctx, "isBuffer") {
            public boolean isBuffer(JSValue value) {
                return value instanceof JSBuffer;
            }
        });

        prototype(proto);
    }

    public void constructor() {}
}
