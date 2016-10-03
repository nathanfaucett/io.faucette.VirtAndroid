package io.faucette.virtandroid.javascript;


import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.net.URI;
import java.net.URISyntaxException;


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
    private WebSocket _websocket;

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

        AsyncHttpClient.getDefaultInstance().websocket("http://localhost:9999/", null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(final Exception ex, final WebSocket websocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    _runtime.setImmediate(new JSFunction(_runtime, "onError") {
                        public void onError() {
                            _this.onError(_jsThis, ex);
                        }
                    });
                } else {
                    _websocket = websocket;

                    websocket.setClosedCallback(new CompletedCallback() {
                        @Override
                        public void onCompleted(final Exception ex) {
                            _runtime.setImmediate(new JSFunction(_runtime, "onError") {
                                public void onError() {
                                    _this.onError(_jsThis, ex);
                                }
                            });
                        }
                    });

                    websocket.setStringCallback(new WebSocket.StringCallback() {
                        public void onStringAvailable(final String s) {
                            _runtime.setImmediate(new JSFunction(_runtime, "onMessage") {
                                public void onMessage() {
                                    _this.onMessage(_jsThis, s);
                                }
                            });
                        }
                    });

                    Log.i("JSWebSocket", "connected");
                    _runtime.setImmediate(new JSFunction(_runtime, "onOpen") {
                        public void onOpen() {
                            _this.onOpen(_jsThis);
                        }
                    });
                }
            }
        });
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
                _this.send(value);
            }
        });
        proto.property("close", new JSFunction(ctx, "close") {
            public void close() {
                _this.close();
            }
        });

        return proto;
    }

    public final void send(final JSValue value) {
        if (_websocket != null) {
            _websocket.send(value.toString());
        }
    }

    public final void close() {
        getThis().property("readyState", new JSValue(_runtime, CLOSING));
        if (_websocket != null) {
            _websocket.close();
        }
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