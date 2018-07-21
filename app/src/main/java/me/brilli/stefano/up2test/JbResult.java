package me.brilli.stefano.up2test;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by stefano on 7/13/15.
 */
public class JbResult {

    public static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return "NULL";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    final static int CONNECTION = 0;
    final static int READ_CHARACTERISTIC = 1;
    final static int WRITE_CHARACTERISTIC = 2;
    final static int READ_DESCRIPTOR = 3;
    final static int WRITE_DESCRIPTOR = 4;
    final static int SERVICE = 5;
    final static int CHANGED_CHARACTERISTIC = 6;


    BluetoothGattCharacteristic characteristic = null;
    BluetoothGattDescriptor descriptor = null;
    int status = 0;
    int type = -1;

    JbResult(BluetoothGattCharacteristic c, int s, int type) {
        this.status = s;
        this.characteristic = c;
        this.type = type;
    }

    JbResult(BluetoothGattCharacteristic c, BluetoothGattDescriptor d, int s, int type) {
        this.status = s;
        this.characteristic = c;
        this.descriptor = d;
        this.type = type;
    }
}