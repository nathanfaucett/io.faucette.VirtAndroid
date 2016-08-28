package io.faucette.virtandroid.javascript;


import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by nathan on 8/27/16.
 */
public class JSConsole extends JSObject implements IJSConsole {

    public JSConsole(JSContext ctx) {
        super(ctx, IJSConsole.class);
    }

    @Override
    public void log(final JSValue value) {
        Log.i("js://console.log()", value.toString());
    }

    @Override
    public void warn(final JSValue value) {
        Log.d("js://console.warn()", value.toString());
    }

    @Override
    public void error(final JSValue value) {
        Log.e("js://console.error()", value.toString());
    }
}
