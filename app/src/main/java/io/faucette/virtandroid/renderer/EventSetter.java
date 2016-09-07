package io.faucette.virtandroid.renderer;


import android.view.View;


/**
 * Created by nathan on 9/6/16.
 */
public interface EventSetter {
    void set(View view, EventHandler eventHandler) throws Exception;
}
