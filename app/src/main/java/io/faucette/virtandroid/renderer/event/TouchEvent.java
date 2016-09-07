package io.faucette.virtandroid.renderer.event;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by nathan on 9/6/16.
 */
public class TouchEvent extends Event {
    private MotionEvent _motionEvent;

    public TouchEvent(View view, String topLevelType, MotionEvent motionEvent) {

        super(view, topLevelType);

        _motionEvent = motionEvent;
    }
}
