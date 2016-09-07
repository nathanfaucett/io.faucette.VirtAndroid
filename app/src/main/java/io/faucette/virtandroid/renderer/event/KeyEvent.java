package io.faucette.virtandroid.renderer.event;

import android.view.View;

/**
 * Created by nathan on 9/6/16.
 */
public class KeyEvent extends Event {
    private int _keyCode;
    private android.view.KeyEvent _nativeKeyEvent;

    public KeyEvent(View view, String topLevelType, int keyCode, android.view.KeyEvent nativeKeyEvent) {

        super(view, topLevelType);

        _keyCode = keyCode;
        _nativeKeyEvent = nativeKeyEvent;
    }
}
