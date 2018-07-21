package me.brilli.stefano.up2test;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by stefano on 7/4/15.
 */
public class JawboneGatt {
    BluetoothGatt gatt;

    public JawboneGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public void close() {
        gatt.close();
    }

    public boolean discoverServices() {
        return gatt.discoverServices();
    }

//    public boolean requestConnectionPriority(int connectionPriority) {
//        return gatt.requestConnectionPriority(connectionPriority);
//    }

    public boolean executeReliableWrite() {
        return gatt.executeReliableWrite();
    }

    public boolean connect() {
        return gatt.connect();
    }

    public boolean readDescriptor(BluetoothGattDescriptor descriptor) {
        return gatt.readDescriptor(descriptor);
    }

    public boolean readRemoteRssi() {
        return gatt.readRemoteRssi();
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return gatt.writeDescriptor(descriptor);
    }

    public List<BluetoothDevice> getConnectedDevices() {
        return gatt.getConnectedDevices();
    }

//    public void abortReliableWrite() {
//        gatt.abortReliableWrite();
//    }

    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        return gatt.getDevicesMatchingConnectionStates(states);
    }

    @Deprecated
    public void abortReliableWrite(BluetoothDevice mDevice) {
        gatt.abortReliableWrite(mDevice);
    }

    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        return gatt.readCharacteristic(characteristic);
    }

    public List<BluetoothGattService> getServices() {
        return gatt.getServices();
    }

    public void disconnect() {
        gatt.disconnect();
    }

    public boolean beginReliableWrite() {
        return gatt.beginReliableWrite();
    }

    public int getConnectionState(BluetoothDevice device) {
        return gatt.getConnectionState(device);
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        return gatt.setCharacteristicNotification(characteristic, enable);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return gatt.writeCharacteristic(characteristic);
    }
//
//    public boolean requestMtu(int mtu) {
//        return gatt.requestMtu(mtu);
//    }

    public BluetoothDevice getDevice() {
        return gatt.getDevice();
    }

    public BluetoothGattService getService(UUID uuid) {
        return gatt.getService(uuid);
    }
}
