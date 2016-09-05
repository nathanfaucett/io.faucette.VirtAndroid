package io.faucette.virtandroid.javascript;

import org.liquidplayer.webkit.javascriptcore.JSArray;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nathan on 9/5/16.
 */
public class JSEventTarget extends JSFunction {
    private HashMap<String, ArrayList<JSFunction>> _listeners;


    public JSEventTarget() {
    }

    public JSEventTarget(JSRuntime ctx, Method constructor, Class<? extends JSObject> instanceObject) throws NoSuchMethodException {
        super(ctx, constructor, instanceObject);
    }

    public JSEventTarget(JSRuntime ctx) throws NoSuchMethodException {
        super(ctx, JSEventTarget.class.getMethod("constructor"), JSEventTarget.class);
    }

    public void constructor() {
        JSContext ctx = getContext();
        JSObject _this = getThis();

        JSObject listeners = new JSObject(ctx);

        _this.property("[[listeners]]", listeners,
                JSPropertyAttributeDontDelete | JSPropertyAttributeDontEnum | JSPropertyAttributeReadOnly);

        createPrototype(_this);
    }

    public JSObject createPrototype(JSObject proto) {
        final JSEventTarget _this = this;
        JSContext ctx = getContext();

        proto.property("addEventListener", new JSFunction(ctx, "addEventListener") {
            public void addEventListener(JSValue type, JSFunction listener) {
                _this.addEventListener(getThis(), type.toString(), listener);
            }
        }, JSPropertyAttributeDontDelete | JSPropertyAttributeDontEnum | JSPropertyAttributeReadOnly);
        proto.property("removeEventListener", new JSFunction(ctx, "removeEventListener") {
            public void removeEventListener(JSValue type, JSFunction listener) {
                _this.removeEventListener(getThis(), type.toString(), listener);
            }
        }, JSPropertyAttributeDontDelete | JSPropertyAttributeDontEnum | JSPropertyAttributeReadOnly);
        proto.property("dispatchEvent", new JSFunction(ctx, "dispatchEvent") {
            public void dispatchEvent(JSEvent event) {
                _this.dispatchEvent(getThis(), event);
            }
        }, JSPropertyAttributeDontDelete | JSPropertyAttributeDontEnum | JSPropertyAttributeReadOnly);

        return proto;
    }

    public void addEventListener(JSObject _this, String type, JSFunction listener) {
        JSContext ctx = getContext();

        JSObject allListeners = _this.property("[[listeners]]").toObject();
        JSArray<JSFunction> listeners;

        if (allListeners.hasProperty(type)) {
            listeners = (JSArray<JSFunction>) allListeners.property(type).toJSArray();
        } else {
            listeners = new JSArray<JSFunction>(ctx, JSFunction.class);
            allListeners.property(type, listeners);
        }

        listeners.add(listener);
    }

    public boolean removeEventListener(JSObject _this, String type, JSFunction listener) {
        JSObject allListeners = _this.property("[[listeners]]").toObject();

        if (allListeners.hasProperty(type)) {
            JSArray<JSFunction> listeners = (JSArray<JSFunction>) allListeners.property(type).toJSArray();

            boolean removed = listeners.remove(listener);

            if (listeners.size() == 0) {
                listeners.deleteProperty(type);
            }

            return removed;
        } else {
            return false;
        }
    }

    public void dispatchEvent(JSObject _this, JSEvent event) {
        String type = event.property("type").toString();

        JSObject allListeners = _this.property("[[listeners]]").toObject();
        JSArray<JSFunction> listeners;

        if (allListeners.hasProperty(type)) {
            listeners = (JSArray<JSFunction>) allListeners.property(type).toJSArray();

            for (JSValue listener : listeners) {
                listener.toFunction().call(_this, event);
            }
        }
    }
}
