package io.faucette.virtandroid.javascript;


import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nathan on 9/5/16.
 */
public class JSXMLHttpRequest extends JSEventTarget {
    public static int UNSENT = 0;
    public static int OPENED = 1;
    public static int HEADERS_RECEIVED = 2;
    public static int LOADING = 3;
    public static int DONE = 4;

    private HashMap<JSObject, HttpURLConnection> _connections;
    private JSRuntime _runtime;

    /* required by AndroidJSCore */
    public JSXMLHttpRequest() {
    }

    public JSXMLHttpRequest(JSRuntime ctx) throws NoSuchMethodException {
        super(ctx, JSXMLHttpRequest.class.getMethod("constructor"), JSXMLHttpRequest.class);
        _runtime = ctx;
        _connections = new HashMap<>();
    }

    public void constructor() {

        super.constructor();

        final JSContext ctx = _runtime;
        final JSXMLHttpRequest _this = this;
        final JSObject _jsThis = getThis();

        _jsThis.property("onreadystatechange", new JSValue(ctx, null));
        _jsThis.property("readyState", new JSValue(ctx, UNSENT));
        _jsThis.property("response", new JSValue(ctx, ""));
        _jsThis.property("responseText", new JSValue(ctx, ""));
        _jsThis.property("responseType", new JSValue(ctx, ""));
        _jsThis.property("responseURL", new JSValue(ctx, ""));
        _jsThis.property("responseXML", new JSValue(ctx, ""));
        _jsThis.property("status", new JSValue(ctx, UNSENT));
        _jsThis.property("statusText ", new JSValue(ctx, "UNSENT"));
        _jsThis.property("timeout", new JSValue(ctx, 0));
        _jsThis.property("ontimeout", new JSValue(ctx, null));
        _jsThis.property("withCredentials", new JSValue(ctx, false));

        createPrototype(_jsThis);
    }

    public JSObject createPrototype(JSObject proto) {
        final JSContext ctx = getContext();
        final JSXMLHttpRequest _this = this;

        super.createPrototype(proto);

        proto.property("abort", new JSFunction(ctx, "abort") {
            public void abort() {
                _this.abort(getThis());
            }
        });
        proto.property("getAllResponseHeaders", new JSFunction(ctx, "getAllResponseHeaders") {
            public JSValue getAllResponseHeaders() {
                return new JSValue(ctx, _this.getAllResponseHeaders(getThis()));
            }
        });
        proto.property("getResponseHeader", new JSFunction(ctx, "getResponseHeader") {
            public JSValue getResponseHeader(JSValue key) {
                return new JSValue(ctx, _this.getResponseHeader(getThis(), key.toString()));
            }
        });
        proto.property("open", new JSFunction(ctx, "open") {
            public void open(JSValue method, JSValue url) {
                _this.open(getThis(), method.toString(), url.toString());
            }
        });
        proto.property("overrideMimeType", new JSFunction(ctx, "overrideMimeType") {
            public void overrideMimeType(JSValue mimetype) {
                _this.overrideMimeType(getThis(), mimetype.toString());
            }
        });
        proto.property("send", new JSFunction(ctx, "send") {
            public void send(JSValue data) {
                if (data != null) {
                    _this.send(getThis(), data.toString());
                } else {
                    _this.send(getThis(), null);
                }
            }
        });
        proto.property("setRequestHeader", new JSFunction(ctx, "setRequestHeader") {
            public void setRequestHeader(JSValue key, JSValue value) {
                _this.setRequestHeader(getThis(), key.toString(), value.toString());
            }
        });

        return proto;
    }

    public void abort(JSObject _jsThis) {
        if (_connections.containsKey(_jsThis)) {
            JSContext ctx = _jsThis.getContext();
            _connections.get(_jsThis).disconnect();
            _connections.remove(_jsThis);
        }
    }

    public String getAllResponseHeaders(JSObject _jsThis) {
        String out = "";

        if (_connections.containsKey(_jsThis)) {
            JSContext ctx = _jsThis.getContext();
            HttpURLConnection connection = _connections.get(_jsThis);

            Map<String, List<String>> headers = connection.getHeaderFields();

            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                out += entry.getKey() + ": " + entry.getValue().toString() + "\n";
            }
        }

        return out;
    }

    public String getResponseHeader(JSObject _jsThis, String key) {
        if (_connections.containsKey(_jsThis)) {
            JSContext ctx = _jsThis.getContext();
            HttpURLConnection connection = _connections.get(_jsThis);

            return connection.getHeaderField(key);
        } else {
            return "";
        }
    }

    public void open(JSObject _jsThis, String method, String url) {
        if (!_connections.containsKey(_jsThis)) {
            try {
                URL u = new URL(url);
                url = u.toString();
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();

                connection.setRequestMethod(method);
                connection.setChunkedStreamingMode(0);

                _connections.put(_jsThis, connection);

                onReadyStateChange(_jsThis, "open", 0, OPENED, "OPENED");

            } catch (Exception e) {
                onError(_jsThis, e);
            }
        }
    }

    public void overrideMimeType(JSObject _jsThis, String mimetype) {
    }

    public void send(JSObject _jsThis, String data) {
        if (_connections.containsKey(_jsThis)) {
            HttpURLConnection connection = _connections.get(_jsThis);

            try {
                connection.connect();

                onReadyStateChange(_jsThis, "progress", 0, HEADERS_RECEIVED, "HEADERS_RECEIVED");
                onReadyStateChange(_jsThis, "progress", 0, LOADING, "LOADING");

                int statusCode = connection.getResponseCode();
                String response = "";
                InputStream in;

                if (statusCode < 400) {
                    in = new BufferedInputStream(connection.getInputStream());
                } else {
                    in = new BufferedInputStream(connection.getErrorStream());
                }

                byte[] contents = new byte[1024];
                int bytesRead = 0;

                while ((bytesRead = in.read(contents)) != -1) {
                    response += new String(contents, 0, bytesRead);
                }

                _jsThis.property("response", new JSValue(_runtime, response));
                _jsThis.property("responseText", new JSValue(_runtime, response));
                _jsThis.property("responseType", new JSValue(_runtime, ""));
                _jsThis.property("responseXML", new JSValue(_runtime, response));
                _jsThis.property("statusCode", new JSValue(_runtime, response));

                onReadyStateChange(_jsThis, "load", statusCode, DONE, "DONE");

            } catch (IOException e) {
                onError(_jsThis, e);
            } finally {
                _connections.remove(_jsThis);
            }
        }
    }

    public void setRequestHeader(JSObject _jsThis, String key, String value) {
        if (_connections.containsKey(_jsThis)) {
            HttpURLConnection connection = _connections.get(_jsThis);
            connection.setRequestProperty(key, value);
        }
    }

    public void onReadyStateChange(final JSObject _jsThis, final String type, final int status, final int readyState, final String statusText) {
        _runtime.setImmediate(new JSFunction(_runtime, "onReadyStateChange") {
            public void onReadyStateChange() {
                JSValue fn = _jsThis.property("onreadystatechange");
                JSEvent event = (JSEvent) _runtime.Event.newInstance("readystatechange");

                _jsThis.property("status", new JSValue(_runtime, status));
                _jsThis.property("readyState", new JSValue(_runtime, readyState));
                _jsThis.property("statusText ", new JSValue(_runtime, statusText));

                if (!fn.isNull()) {
                    fn.toFunction().call(_jsThis, event);
                }

                dispatchEvent(_jsThis, event);
                dispatchEvent(_jsThis, (JSEvent) _runtime.Event.newInstance(type));
            }
        });
    }

    public void onError(final JSObject _jsThis, final Exception ex) {
        _runtime.setImmediate(new JSFunction(_runtime, "onError") {
            public void onError() {
                JSEvent event = (JSEvent) _runtime.Event.newInstance("error");
                event.property("data", ex.toString());
                dispatchEvent(_jsThis, event);
            }
        });
    }
}
