package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;

/**
 * Created by nathan on 8/29/16.
 */
public class JSProcess extends JSObject implements IJSProcess {
    private JSRuntime _runtime;


    public JSProcess(JSRuntime ctx) {
        super(ctx, IJSProcess.class);
        _runtime = ctx;

        property("env", new JSObject(ctx));
    }

    public void nextTick(JSFunction fn) {
        _runtime.setTimeout(fn, 0);
    }
}
