package io.faucette.virtandroid.renderer;


import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;

import io.faucette.messenger.Messenger;
import io.faucette.virtandroid.renderer.event.Event;

/**
 * Created by nathan on 9/6/16.
 */
public class EventHandler {
    private Messenger _messenger;
    private HashMap<String, EventSetter> _setters;


    public EventHandler(Messenger messenger) {

        _messenger = messenger;
        _setters = new HashMap<>();

        try {
            _init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void _init() throws Exception {
        addEventSetter("topClick", new EventSetter() {
            @Override
            public void set(View view, EventHandler eventHandler) throws Exception {
                Events.setOnClickListener(view, "topClick", eventHandler);
            }
        });
        addEventSetter("topLongClick", new EventSetter() {
            @Override
            public void set(View view, EventHandler eventHandler) throws Exception {
                Events.setOnLongClickListener(view, "topLongClick", eventHandler);
            }
        });
        addEventSetter("topFocusChange", new EventSetter() {
            @Override
            public void set(View view, EventHandler eventHandler) throws Exception {
                Events.setOnFocusChangeListener(view, "topFocusChange", eventHandler);
            }
        });
        addEventSetter("topKey", new EventSetter() {
            @Override
            public void set(View view, EventHandler eventHandler) throws Exception {
                Events.setOnKeyListener(view, "topKey", eventHandler);
            }
        });
        addEventSetter("topTouch", new EventSetter() {
            @Override
            public void set(View view, EventHandler eventHandler) throws Exception {
                Events.setOnTouchListener(view, "topTouch", eventHandler);
            }
        });
        addEventSetter("topCreateContextMenu", new EventSetter() {
            @Override
            public void set(View view, EventHandler eventHandler) throws Exception {
                Events.setOnTouchListener(view, "topCreateContextMenu", eventHandler);
            }
        });
    }

    public final void addEventSetter(String topLevelType, EventSetter setter) throws Exception {
        if (_setters.containsKey(topLevelType)) {
            throw new Exception("Event Setter for " + topLevelType + " already set");
        } else {
            _setters.put(topLevelType, setter);
        }
    }

    public final void listenTo(String id, String topLevelType) throws Exception {
        View view = Views.getViewById(id);

        if (_setters.containsKey(topLevelType)) {
            _setters.get(topLevelType).set(view, this);
        } else {
            throw new Exception("Invalid Event " + topLevelType + " bound to id " + id);
        }
    }

    public void handleEvent(Event event) {
        JSONObject json = new JSONObject();

        try {
            json.put("targetId", event.getId());
            json.put("topLevelType", event.getTopLevelTypeType());
            json.put("nativeEvent", event.toJSON());
            _messenger.send("virt.android.handleEventDispatch", json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
