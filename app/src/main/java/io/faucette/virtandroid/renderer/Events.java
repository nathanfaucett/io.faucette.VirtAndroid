package io.faucette.virtandroid.renderer;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;

import io.faucette.virtandroid.renderer.event.Event;
import io.faucette.virtandroid.renderer.event.FocusEvent;
import io.faucette.virtandroid.renderer.event.KeyEvent;
import io.faucette.virtandroid.renderer.event.TouchEvent;

/**
 * Created by nathan on 9/6/16.
 */
public class Events {

    public static void setOnClickListener(View view, final String topLevelTop, final EventHandler eventHandler) throws Exception {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventHandler.handleEvent(new Event(view, topLevelTop));
            }
        });
    }

    public static void setOnLongClickListener(View view, final String topLevelTop, final EventHandler eventHandler) throws Exception {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                eventHandler.handleEvent(new Event(view, topLevelTop));
                return false;
            }
        });
    }

    public static void setOnFocusChangeListener(View view, final String topLevelTop, final EventHandler eventHandler) throws Exception {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                eventHandler.handleEvent(new FocusEvent(view, topLevelTop, b));
            }
        });
    }

    public static void setOnKeyListener(View view, final String topLevelTop, final EventHandler eventHandler) throws Exception {
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, android.view.KeyEvent keyEvent) {
                eventHandler.handleEvent(new KeyEvent(view, topLevelTop, keyCode, keyEvent));
                return false;
            }
        });
    }

    public static void setOnTouchListener(View view, final String topLevelTop, final EventHandler eventHandler) throws Exception {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                eventHandler.handleEvent(new TouchEvent(view, topLevelTop, motionEvent));
                return false;
            }
        });
    }

    public static void setOnCreateContextMenuListener(View view, final String topLevelTop, final EventHandler eventHandler) throws Exception {
        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                eventHandler.handleEvent(new Event(view, topLevelTop));
            }
        });
    }
}
