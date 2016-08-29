package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by nathan on 8/28/16.
 */
public interface IJSProcess {
    public void nextTick(JSFunction fn);
}
