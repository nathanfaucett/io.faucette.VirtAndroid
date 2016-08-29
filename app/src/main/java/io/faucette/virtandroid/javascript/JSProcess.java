package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;

/**
 * Created by nathan on 8/28/16.
 */
public class JSProcess extends JSObject implements IJSProcess {
    JSRuntime _runtime;

    public JSProcess(JSRuntime ctx) {
        super(ctx, IJSProcess.class);
        _runtime = ctx;
    }

    @Override
    public void nextTick(final JSFunction fn) {
        _runtime.setTimeout(fn, 0);
    }
}
