package me.brilli.stefano.up2test;

import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by stefano on 7/13/15.
 */
public class SecuredStream
{
    private final byte[] c;
    private byte[] d;
    private int e;

    public SecuredStream(final byte[] seed, final byte[] key) {
        this.c = key;
        this.d = seed;
        this.e = 0;
    }

    private byte[] b(byte[] doFinal) {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(this.c, "AES");
        final byte[] array = new byte[16];
        Arrays.fill(array, (byte)0);
        new IvParameterSpec(array);
        byte[] array2 = null;

        try {
            final Cipher instance = Cipher.getInstance("AES/ECB/NoPadding");
            instance.init(1, secretKeySpec);
            doFinal = instance.doFinal(doFinal);
            array2 = new byte[16];
            System.arraycopy(doFinal, 0, array2, 0, 16);
            Log.d(SecuredStream.b, "generateSeed > " + HexBin.a(array2));
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            doFinal = array2;
        }
        catch (NoSuchPaddingException ex2) {
            ex2.printStackTrace();
            doFinal = array2;
        }
        catch (IllegalBlockSizeException ex3) {
            ex3.printStackTrace();
            doFinal = array2;
        }
        catch (BadPaddingException ex4) {
            ex4.printStackTrace();
            doFinal = array2;
        }
        catch (InvalidKeyException ex5) {
            ex5.printStackTrace();
            doFinal = array2;
        }
        return array2;
    }

    private byte[] a(int i) {
        final byte[] array = new byte[i];
        int n = 0;
        while (i > 0) {
            int n2;
            if (i < (n2 = 16 - this.e)) {
                n2 = i;
            }
            if (this.e == 0) {
                this.d = this.b(this.d);
            }
            System.arraycopy(this.d, this.e, array, n, n2);
            n += n2;
            i -= n2;
            this.e = (n2 + this.e) % 16;
        }
        return array;
    }

    public byte[] a(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        final byte[] a = this.a(array.length);
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)(array[i] ^ a[i]);
        }
        Log.d(SecuredStream.b, JbResult.bytesToString(array) + " xor " + JbResult.bytesToString(a) + " = " + JbResult.bytesToString(array2));
        return array2;
    }


    public static final int a = 16;
    private static final String b = SecuredStream.class.getSimpleName();

    public static byte[] a(byte[] doFinal, byte[] key, byte[] spec, final int n, final int n2) {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(spec);
        spec = null;
        Log.d("NEWTICK", "Key length = " + key.length + " mode =" + n + " inp length = " + doFinal.length);
        try {
            final Cipher instance = Cipher.getInstance("AES/ECB/NoPadding");
            if (n2 == 1) {
                instance.init(n, secretKeySpec, ivParameterSpec);
            }
            else {
                instance.init(n, secretKeySpec);
            }
            doFinal = instance.doFinal(doFinal, 0, 16);
            key = new byte[16];
            System.arraycopy(doFinal, 0, key, 0, 16);
            Log.d(SecuredStream.b, "encrypted data 3 > " + HexBin.a(key));
            return key;
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            doFinal = spec;
        }
        catch (NoSuchPaddingException ex2) {
            ex2.printStackTrace();
            doFinal = spec;
        }
        catch (IllegalBlockSizeException ex3) {
            ex3.printStackTrace();
            doFinal = spec;
        }
        catch (BadPaddingException ex4) {
            ex4.printStackTrace();
            doFinal = spec;
        }
        catch (InvalidKeyException ex5) {
            ex5.printStackTrace();
            doFinal = spec;
        }
        catch (Exception ex6) {
            Log.d("NEWTICK", "Exception = " + ex6.toString());
            doFinal = spec;
        }
        return key;
    }

}
