package io.faucette.virtandroid.javascript;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSError;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by nathan on 8/28/16.
 */
public class JSWebSocket extends JSFunction {
    public static int CONNECTING = 0;
    public static int OPEN = 1;
    public static int CLOSING = 2;
    public static int CLOSED = 3;

    private JSRuntime _runtime;

    private URI _uri;
    private WebSocketClient _client;

    /* required by AndroidJSCore */
    public JSWebSocket() {}

    public JSWebSocket(JSRuntime ctx) throws NoSuchMethodException {

        super(ctx, JSWebSocket.class.getMethod("constructor", String.class), JSWebSocket.class);

        _runtime = ctx;

        JSObject proto = new JSObject(ctx);

        proto.property("CONNECTING", CONNECTING);
        proto.property("OPEN", OPEN);
        proto.property("CLOSING", CLOSING);
        proto.property("CLOSED", CLOSED);

        final JSWebSocket _this = this;

        proto.property("send", new JSFunction(ctx, "send") {
            public void send(JSValue value) {
                _this.send(value.toString());
            }
        });
        proto.property("close", new JSFunction(ctx, "close") {
            public void close() {
                _this.close();
            }
        });

        prototype(proto);
    }

    public void constructor(String uriString) {
        JSContext ctx = getContext();
        final JSObject _jsThis = getThis();

        String url = "ws://localhost:9999";
        String protocol = "ws";

        try {
            _uri = new URI(uriString);
            url = _uri.toString();
            protocol = _uri.getScheme();
        } catch (URISyntaxException e) {
            Log.e("JSWebSocket", e.toString());
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

        final JSWebSocket _this = this;
        _client = new WebSocketClient(_uri) {

            @Override
            public void onOpen(final ServerHandshake handshakedata) {

                Log.i("JSWebSocket", "WebSocket Opened");

                _runtime.setImmediate(new JSFunction(_runtime, "onOpen") {
                    public void onOpen() {
                        _this.onOpen(_jsThis, handshakedata);
                    }
                });
            }

            @Override
            public void onMessage(final String message) {
                _runtime.setImmediate(new JSFunction(_runtime, "onMessage") {
                    public void onMessage() {
                        _this.onMessage(_jsThis, message);
                    }
                });
            }

            @Override
            public void onClose(final int code, final String reason, final boolean remote) {

                Log.i("JSWebSocket", "WebSocket Closed code: " + code + " reason: " + reason + " remote: " + remote);

                _runtime.setImmediate(new JSFunction(_runtime, "onClose") {
                    public void onClose() {
                        _this.onClose(_jsThis, code, reason, remote);
                    }
                });
            }

            @Override
            public void onError(final Exception ex) {

                Log.e("JSWebSocket", ex.toString());

                _runtime.setImmediate(new JSFunction(_runtime, "onError") {
                    public void onError() {
                        _this.onError(_jsThis, ex);
                    }
                });
            }
        };

        _client.connect();
    }

    public void onOpen(JSObject _jsThis, ServerHandshake handshakedata) {
        JSValue fn = _jsThis.property("onopen");

        _jsThis.property("readyState", new JSValue(_runtime, OPEN));

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis);
        }
    }

    public void onMessage(JSObject _jsThis, String message) {
        JSValue fn = _jsThis.property("onmessage");

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis, new JSValue(_jsThis.getContext(), message));
        }
    }

    public void onClose(JSObject _jsThis, int code, String reason, boolean remote) {
        JSValue fn = _jsThis.property("onclose");

        _jsThis.property("readyState", new JSValue(_runtime, CLOSED));

        if (!fn.isNull()) {
            JSContext ctx = _jsThis.getContext();
            fn.toFunction().call(_jsThis,
                    new JSValue(ctx, code),
                    new JSValue(ctx, reason),
                    new JSValue(ctx, remote)
            );
        }
    }

    public void onError(JSObject _jsThis, Exception ex) {
        JSValue fn = _jsThis.property("onerror");

        if (!fn.isNull()) {
            fn.toFunction().call(_jsThis, new JSError(_jsThis.getContext(), ex.getMessage()));
        }
    }

    public void close() {
        getThis().property("readyState", new JSValue(_runtime, CLOSING));
        _client.close();
    }
    public void send(String data) {
        _client.send(data);
    }
}
