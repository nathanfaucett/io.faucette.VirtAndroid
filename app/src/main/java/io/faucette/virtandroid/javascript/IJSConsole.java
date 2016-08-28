package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by nathan on 8/27/16.
 */
public interface IJSConsole {
    public void log(final JSValue value);
    public void warn(final JSValue value);
    public void error(final JSValue value);
}
