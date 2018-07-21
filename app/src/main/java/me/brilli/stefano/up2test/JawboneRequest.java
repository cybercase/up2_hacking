package me.brilli.stefano.up2test;

import java.util.Arrays;

/**
 * Created by stefano on 7/4/15.
 */
public class JawboneRequest {

    public static class Continuation {
        public int index;  // counter
        public byte[] d;  // payload

        Continuation(int index, byte[] payload) {
            this.index = index;
            this.d = payload;
        }

        public byte[] toByte() {
            byte[] res = new byte[this.d.length + 1];
            res[0] = (byte)this.index;
            System.arraycopy(this.d, 0, res, 1, this.d.length);
            return res;
        };
    }


    static int PROTOCOL_VERSION_REQUEST = 0x00;
    static int AUTHENTICATE_REQUEST = 0x04;
    static int CHALLENGE_REQUEST = 0x05;
    static int SECURE_REQUEST = 0x06;

    public int index; // packet number
    public int f;  // type
    public int g;  // always zero?
    public int h;  // counter
    public int i;  // payload length

    public byte[] d;  // payload

    public Continuation continuation;

    JawboneRequest(int index, int type, int zero, int counter, int len, byte[] payload) {
        this.index = index;
        f = type;
        g = zero;
        h = counter;
        i = len;
        d = payload;

        if (d != null && d.length > 15) {
            byte[] first = new byte[15];
            System.arraycopy(d, 0, first, 0, 15);

            byte[] second = new byte[d.length-15];
            System.arraycopy(d, 15, second, 0, second.length);

            d = first;
            continuation = new Continuation(this.index + 1, second);

        } else {
            continuation = null;
        }
    }

    public byte[] toByte() {
        int resLength = 5;
        if (d != null) {
            resLength += d.length;
        }

        byte[] res = new byte[resLength];
        res[0] = (byte)index;
        res[1] = (byte)f;
        res[2] = (byte)g;
        res[3] = (byte)h;
        res[4] = (byte)i;

        if (d != null) {
            System.arraycopy(d, 0, res, 5, d.length);
        }
        return res;
    };

    public String toString() {
        return JbResult.bytesToString(this.toByte());
//        return Arrays.toString(this.toByte());
    };
}
