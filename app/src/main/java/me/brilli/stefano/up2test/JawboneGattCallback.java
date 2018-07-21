package me.brilli.stefano.up2test;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by stefano on 7/4/15.
 */
public class JawboneGattCallback extends BluetoothGattCallback{

    public final static String TAG = "CONN:";
    public static int QUEUE_CAPACITY = 50;
    private ArrayBlockingQueue<JbResult> jbResultQueue;


    JawboneGattCallback() {
        super();
        jbResultQueue = new ArrayBlockingQueue<JbResult>(QUEUE_CAPACITY);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        try {
            Log.d(TAG, "Connection Changed: " + new Integer(newState).toString());
            jbResultQueue.put(new JbResult(null, newState, JbResult.CONNECTION));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        try {
            this.jbResultQueue.put(new JbResult(null, status, JbResult.SERVICE));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        try {
            this.jbResultQueue.put(new JbResult(characteristic, status, JbResult.READ_CHARACTERISTIC));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        try {
            this.jbResultQueue.put(new JbResult(characteristic, status, JbResult.WRITE_CHARACTERISTIC));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        try {
            Log.d(TAG, "Changed: " + JbResult.bytesToString(characteristic.getValue()));
            this.jbResultQueue.put(new JbResult(characteristic, 0, JbResult.CHANGED_CHARACTERISTIC));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        try {
            this.jbResultQueue.put(new JbResult(null, descriptor, 0, JbResult.WRITE_DESCRIPTOR));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        try {
            this.jbResultQueue.put(new JbResult(null, descriptor, 0, JbResult.READ_DESCRIPTOR));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public JbResult getResult(int type) throws InterruptedException {
        JbResult r = jbResultQueue.take();

        if (r.type != type) {
            throw new RuntimeException(String.format("Expected type %d, got type %d", type, r.type));
        }

        return r;
    }

    public void pushResult(JbResult r) throws InterruptedException {
        jbResultQueue.put(r);
    }
}
