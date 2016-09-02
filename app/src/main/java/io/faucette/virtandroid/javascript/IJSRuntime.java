package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by nathan on 8/31/16.
 */
public interface IJSRuntime {

    public long setTimeout(final JSFunction fn, final long delay);

    public long setImmediate(final JSFunction fn);

    public void clearTimeout(final long id);
}
