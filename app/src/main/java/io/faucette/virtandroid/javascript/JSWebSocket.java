package io.faucette.virtandroid.javascript;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.liquidplayer.webkit.javascriptcore.JSArrayBuffer;
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

    private URI _uri;
    private WebSocketClient _client;

    /* required by AndroidJSCore */
    public JSWebSocket() {}

    public JSWebSocket(JSContext ctx) throws NoSuchMethodException {

        super(ctx, JSWebSocket.class.getMethod("constructor", String.class), JSWebSocket.class);

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
        final JSObject _this = getThis();

        String url = "ws://localhost:9999";
        String protocol = "ws";

        try {
            _uri = new URI(uriString);
            url = _uri.toString();
            protocol = _uri.getScheme();
        } catch (URISyntaxException e) {
            Log.e("JSWebSocket", e.toString());
        }

        _this.property("binaryType", new JSValue(ctx, ""));
        _this.property("bufferedAmount", new JSValue(ctx, 0));
        _this.property("extensions", new JSValue(ctx, ""));
        _this.property("onclose", new JSValue(ctx, null));
        _this.property("onerror", new JSValue(ctx, null));
        _this.property("onmessage", new JSValue(ctx, null));
        _this.property("onopen", new JSValue(ctx, null));
        _this.property("protocol", new JSValue(ctx, protocol));
        _this.property("readyState", new JSValue(ctx, CONNECTING));
        _this.property("url", new JSValue(ctx, url));

        final JSWebSocket _thisWebSocket = this;
        _client = new WebSocketClient(_uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                _thisWebSocket.onOpen(_this, handshakedata);
            }

            @Override
            public void onMessage(String message) {
                _thisWebSocket.onMessage(_this, message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                _thisWebSocket.onClose(_this, code, reason, remote);
            }

            @Override
            public void onError(Exception ex) {
                _thisWebSocket.onError(_this, ex);
            }
        };

        _client.connect();
    }

    public void onOpen(JSObject _this, ServerHandshake handshakedata) {
        JSValue fn = _this.property("onopen");

        if (!fn.isNull()) {
            fn.toFunction().call(_this);
        }
    }
    public void onMessage(JSObject _this, String message) {
        JSValue fn = _this.property("onmessage");

        if (!fn.isNull()) {
            fn.toFunction().call(_this, new JSValue(_this.getContext(), message));
        }
    }
    public void onClose(JSObject _this, int code, String reason, boolean remote) {
        JSValue fn = _this.property("onclose");

        if (!fn.isNull()) {
            JSContext ctx = _this.getContext();
            fn.toFunction().call(_this,
                    new JSValue(ctx, code),
                    new JSValue(ctx, reason),
                    new JSValue(ctx, remote)
            );
        }
    }
    public void onError(JSObject _this, Exception ex) {
        JSValue fn = _this.property("onerror");

        if (!fn.isNull()) {
            Log.e("JSWebSocket", ex.toString());
            fn.toFunction().call(_this, new JSError(_this.getContext(), ex.getMessage()));
        }
    }

    public void close() {
        _client.close();
    }
    public void send(String data) {
        _client.send(data);
    }
}
