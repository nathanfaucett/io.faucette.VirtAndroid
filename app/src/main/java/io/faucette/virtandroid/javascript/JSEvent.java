package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by nathan on 9/5/16.
 */
public class JSEvent extends JSFunction {

    public JSEvent() {
    }

    public JSEvent(JSRuntime ctx) throws NoSuchMethodException {
        super(ctx, JSEvent.class.getMethod("constructor", JSValue.class), JSEvent.class);
    }

    public void constructor(JSValue type) {
        JSObject _this = getThis();
        _this.property("type", type.toString());
    }
}
