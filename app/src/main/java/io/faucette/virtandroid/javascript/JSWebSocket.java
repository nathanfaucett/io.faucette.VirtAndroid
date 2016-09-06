package io.faucette.virtandroid.javascript;

import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.net.URI;
import java.net.URISyntaxException;

import io.faucette.virtandroid.websockets.FakeWebSocketClient;


/**
 * Created by nathan on 8/28/16.
 */
public class JSWebSocket extends JSEventTarget {
    public static int CONNECTING = 0;
    public static int OPEN = 1;
    public static int CLOSING = 2;
    public static int CLOSED = 3;

    private JSRuntime _runtime;

    private URI _uri;
    private FakeWebSocketClient _webSocket;

    /* required by AndroidJSCore */
    public JSWebSocket() {
    }

    public JSWebSocket(JSRuntime ctx) throws NoSuchMethodException {
        super(ctx, JSWebSocket.class.getMethod("constructor", String.class), JSWebSocket.class);
        _runtime = ctx;
    }

    public void constructor(String uriString) {

        super.constructor();

        final JSWebSocket _this = this;
        final JSObject _jsThis = getThis();
        JSContext ctx = getContext();

        String url = "ws://localhost:9999";
        String protocol = "ws";

        try {
            _uri = new URI(uriString);
            url = _uri.toString();
            protocol = _uri.getScheme();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        _jsThis.property("binaryType", new JSValue(ctx, ""));
        _jsThis.property("bufferedAmount", new JSValue(ctx, 0));
        _jsThis.property("extensions", new JSValue(ctx, ""));
        _jsThis.property("onclose", new JSValue(ctx, null));
        _jsThis.property("onerror", new JSValue(ctx, null));
        _jsThis.property("onmessage", new JSValue(ctx, null));
        _jsThis.property("onopen", new JSValue(ctx, null));
        _jsThis.property("protocol", new JSValue(ctx, protocol));
        _jsThis.property("readyState", new JSValue(ctx, CONNECTING));
        _jsThis.property("url", new JSValue(ctx, url));

        createPrototype(_jsThis);

        _webSocket = new FakeWebSocketClient(_uri) {
            @Override
            public void onOpen() {
                Log.i("JSWebSocket", "Open");
                _runtime.setImmediate(new JSFunction(_runtime, "onOpen") {
                    public void onOpen() {
                        _this.onOpen(_jsThis);
                    }
                });
            }

            @Override
            public void onMessage(final String data) {
                _runtime.setImmediate(new JSFunction(_runtime, "onMessage") {
                    public void onMessage() {
                        _this.onMessage(_jsThis, data);
                    }
                });
            }

            @Override
            public void onClose(final boolean remote) {
                Log.i("JSWebSocket", "Close remote: " + remote);
                _runtime.setImmediate(new JSFunction(_runtime, "onClose") {
                    public void onClose() {
                        _this.onClose(_jsThis, 0, "unknown", remote);
                    }
                });
            }

            @Override
            public void onError(final Exception ex) {
                ex.printStackTrace();
                _runtime.setImmediate(new JSFunction(_runtime, "onError") {
                    public void onError() {
                        _this.onError(_jsThis, ex);
                    }
                });
            }
        };


        try {
            _webSocket.connect();
        } catch (final Exception ex) {
            ex.printStackTrace();
            _runtime.setImmediate(new JSFunction(_runtime, "onError") {
                public void onError() {
                    _this.onError(_jsThis, ex);
                }
            });
        }
    }

    public JSObject createPrototype(JSObject proto) {
        JSContext ctx = getContext();
        super.createPrototype(proto);

        proto.property("CONNECTING", CONNECTING);
        proto.property("OPEN", OPEN);
        proto.property("CLOSING", CLOSING);
        proto.property("CLOSED", CLOSED);

        final JSWebSocket _this = this;

        proto.property("send", new JSFunction(ctx, "send") {
            public void send(JSValue value) {
                _webSocket.send(value.toString());
            }
        });
        proto.property("close", new JSFunction(ctx, "close") {
            public void close() {
                getThis().property("readyState", new JSValue(_runtime, CLOSING));
            }
        });

        return proto;
    }

    public final void onOpen(JSObject _jsThis) {
        JSValue fn = _jsThis.property("onopen");

        JSEvent event = (JSEvent) _runtime.Event.newInstance("open");

        _jsThis.property("readyState", new JSValue(_runtime, OPEN));

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis, event);
        }

        dispatchEvent(_jsThis, event);
    }

    public final void onMessage(JSObject _jsThis, String data) {
        JSValue fn = _jsThis.property("onmessage");

        JSEvent event = (JSEvent) _runtime.Event.newInstance("message");
        event.property("data", data);

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis, event);
        }

        dispatchEvent(_jsThis, event);
    }

    public final void onClose(JSObject _jsThis, int code, String reason, boolean remote) {
        JSValue fn = _jsThis.property("onclose");

        JSEvent event = (JSEvent) _runtime.Event.newInstance("close");
        event.property("code", code);
        event.property("reason", reason);
        event.property("remote", remote);

        _jsThis.property("readyState", new JSValue(_runtime, CLOSED));

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis, event);
        }

        dispatchEvent(_jsThis, event);
    }

    public final void onError(JSObject _jsThis, Exception ex) {
        JSValue fn = _jsThis.property("onerror");

        JSEvent event = (JSEvent) _runtime.Event.newInstance("error");
        event.property("data", ex.getMessage());

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis, event);
        }

        dispatchEvent(_jsThis, event);
    }
}