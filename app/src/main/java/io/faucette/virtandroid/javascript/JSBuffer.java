package io.faucette.virtandroid.javascript;


import org.liquidplayer.webkit.javascriptcore.JSArray;
import org.liquidplayer.webkit.javascriptcore.JSArrayBuffer;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSTypedArray;
import org.liquidplayer.webkit.javascriptcore.JSUint8Array;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.UnsupportedEncodingException;


/**
 * Created by nathan on 8/28/16.
 */
public class JSBuffer extends JSUint8Array implements IJSBUffer {

    public JSBuffer(JSContext ctx, JSArray<JSValue> array) {
        super(ctx, array);
    }
    public JSBuffer(JSContext ctx, JSBuffer buffer) {
        super(ctx, buffer);
    }
    public JSBuffer(JSContext ctx, int size) {
        super(ctx, size);
    }

    public JSBuffer(JSArrayBuffer buffer, int byteOffset, int length) {
        super(buffer, byteOffset, length);
    }
    public JSBuffer(JSArrayBuffer buffer, int byteOffset) {
        super(buffer, byteOffset);
    }
    public JSBuffer(JSArrayBuffer buffer) {
        super(buffer);
    }

    public JSBuffer(JSContext ctx, JSValue string, JSValue encoding) {
        super(Utils.toUint8Array(ctx, string.toString(), encoding.toString()));
    }
    public JSBuffer(JSContext ctx, JSValue string) {
        super(Utils.toUint8Array(ctx, string.toString(), "utf-8"));
    }

    public boolean isBuffer(JSValue buffer) {
        return buffer instanceof JSBuffer;
    }
}
