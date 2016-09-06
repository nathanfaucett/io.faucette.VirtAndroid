package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by nathan on 8/29/16.
 */
public interface IJSProcess {
    void nextTick(final JSFunction fn);
}
