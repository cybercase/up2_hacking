package me.brilli.stefano.up2test;

import java.util.Arrays;

/**
 * Created by stefano on 7/4/15.
 */
public class JawboneResponse {

    public static class Continuation {
        public int index;  // counter
        public byte[] d;  // payload

        Continuation(byte[] data, int len) {
            this.index = data[0];

            d = new byte[len];
            System.arraycopy(data, 1, d, 0, len);
        }
    }


    byte[] source;
    int index;
    int g;  // zero
    int h;  // proto counter
    int i;  // payload length
    byte[] k; // payload

    Continuation continuation = null;

    JawboneResponse(byte[] res) {
        source = res;

        index = res[0];
        g = (int)res[2];
        h = (int)res[3];
        i = (int)res[4];
        if (i > 0) {
            k = new byte[Math.min(i, 15)];
            System.arraycopy(res, 5, k, 0, k.length);
        } else {
            k = null;
        }
    }

    public String toString() {
        return JbResult.bytesToString(this.source);
//        return Arrays.toString(this.source);
    };

    public byte[] payload() {
        byte[] payload = new byte[this.i];
        if(i < 16) {
            System.arraycopy(this.k, 0, payload, 0, this.i);
        } else if (i >= 16) {
            System.arraycopy(this.k, 0, payload, 0, 15);
            System.arraycopy(this.continuation.d, 0, payload, 15, this.i - 15);
        }
        return payload;
    };
}
