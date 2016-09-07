package io.faucette.virtandroid.renderer.event;

import android.view.View;

/**
 * Created by nathan on 9/6/16.
 */
public class FocusEvent extends Event {
    private boolean _focused;

    public FocusEvent(View view, String topLevelType, boolean b) {
        super(view, topLevelType);
        _focused = b;
    }
}
