package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by nathan on 8/27/16.
 */
public interface IJSConsole {
    void log(final JSValue value);

    void warn(final JSValue value);

    void error(final JSValue value);
}
