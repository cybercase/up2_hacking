package me.brilli.stefano.up2test;

/**
 * Created by stefano on 7/13/15.
 */
public final class HexBin
{
    private static final int a = 128;
    private static final int b = 16;
    private static final byte[] c;
    private static final char[] d;

    static {
        final int n = 10;
        final int n2 = 0;
        c = new byte[128];
        d = new char[16];
        for (int i = 0; i < 128; ++i) {
            HexBin.c[i] = -1;
        }
        for (int j = 57; j >= 48; --j) {
            HexBin.c[j] = (byte)(j - 48);
        }
        for (int k = 70; k >= 65; --k) {
            HexBin.c[k] = (byte)(k - 65 + 10);
        }
        int n3 = 102;
        int n4;
        while (true) {
            n4 = n2;
            if (n3 < 97) {
                break;
            }
            HexBin.c[n3] = (byte)(n3 - 97 + 10);
            --n3;
        }
        int l;
        while (true) {
            l = n;
            if (n4 >= 10) {
                break;
            }
            HexBin.d[n4] = (char)(n4 + 48);
            ++n4;
        }
        while (l <= 15) {
            HexBin.d[l] = (char)(l + 65 - 10);
            ++l;
        }
    }

    public static String a(final byte[] array) {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        final char[] array2 = new char[length * 2];
        for (int i = 0; i < length; ++i) {
            final byte b = array[i];
            int n;
            if ((n = b) < 0) {
                n = b + 256;
            }
            array2[i * 2] = HexBin.d[n >> 4];
            array2[i * 2 + 1] = HexBin.d[n & 0xF];
        }
        return new String(array2);
    }

    public static byte[] a(final String s) {
        if (s != null) {
            final int length = s.length();
            if (length % 2 == 0) {
                final char[] charArray = s.toCharArray();
                final int n = length / 2;
                final byte[] array = new byte[n];
                for (int i = 0; i < n; ++i) {
                    final char c = charArray[i * 2];
                    int n2;
                    if (c < '\u0080') {
                        n2 = HexBin.c[c];
                    }
                    else {
                        n2 = -1;
                    }
                    if (n2 == -1) {
                        return null;
                    }
                    final char c2 = charArray[i * 2 + 1];
                    int n3;
                    if (c2 < '\u0080') {
                        n3 = HexBin.c[c2];
                    }
                    else {
                        n3 = -1;
                    }
                    if (n3 == -1) {
                        return null;
                    }
                    array[i] = (byte)(n3 | n2 << 4);
                }
                return array;
            }
        }
        return null;
    }
}