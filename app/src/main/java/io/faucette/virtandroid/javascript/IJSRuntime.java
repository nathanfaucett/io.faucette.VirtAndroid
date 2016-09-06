package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by nathan on 8/31/16.
 */
public interface IJSRuntime {

    long setTimeout(final JSFunction fn, final long delay);

    long setImmediate(final JSFunction fn);

    void clearTimeout(final long id);
}
