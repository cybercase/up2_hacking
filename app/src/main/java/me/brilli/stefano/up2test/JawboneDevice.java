package me.brilli.stefano.up2test;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by stefano on 7/4/15.
 */
public class JawboneDevice {
    static String TAG = "JBDEV";

    final static UUID initServiceUUID = UUID.fromString("151c0000-4580-4111-9ca1-5056f3454fbc");
    final static UUID cmdServiceUUID = UUID.fromString("151c1000-4580-4111-9ca1-5056f3454fbc");
    final static UUID responseServiceUUID = UUID.fromString("151c1000-4580-4111-9ca1-5056f3454fbc");
    final static UUID otaServiceUUID = UUID.fromString("151c3000-4580-4111-9ca1-5056f3454fbc");
    final static UUID infoServiceUUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");

    final static UUID otaCharacteristicUUID = UUID.fromString("151c3002-4580-4111-9ca1-5056f3454fbc");
    final static UUID initCharacteristicUUID = UUID.fromString("151c0002-4580-4111-9ca1-5056f3454fbc");
    final static UUID cmdCharacteristicUUID = UUID.fromString("151c1001-4580-4111-9ca1-5056f3454fbc");
    final static UUID responseCharacteristicUUID = UUID.fromString("151c1002-4580-4111-9ca1-5056f3454fbc");
    final static UUID responseDescriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    final static UUID infoCharacteristicUUIDs[] = new UUID[]{
            UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb"),
    };

    public JawboneGatt gatt;
    public JawboneGattCallback cb;
    public int pktCounter;
    public int protoCounter;
    public byte key[] = new byte[16];

    BluetoothGattService cmdService;
    BluetoothGattService responseService;
    BluetoothGattService otaService;
    BluetoothGattService initService;
    BluetoothGattService infoService;

    BluetoothGattCharacteristic cmdCharacteristic;
    BluetoothGattCharacteristic responseCharacteristic;
    BluetoothGattCharacteristic otaCharacteristic;
    BluetoothGattCharacteristic initCharacteristic;


    JawboneDevice(JawboneGatt gatt, JawboneGattCallback cb) throws InterruptedException {
        this.gatt = gatt;
        this.cb = cb;
        pktCounter = 0;
        protoCounter = 251; // new Random().nextInt(256); // random

        JbResult res;

        // CONNECT
        while(true) {
            res = cb.getResult(JbResult.CONNECTION);
            if (res.status == BluetoothProfile.STATE_CONNECTED) {
                break;
            }
        }

        // DISCOVER SERVICES
        gatt.discoverServices();
        cb.getResult(JbResult.SERVICE);

        cmdService = gatt.getService(cmdServiceUUID);
        cmdCharacteristic = cmdService.getCharacteristic(cmdCharacteristicUUID);
        responseService = gatt.getService(responseServiceUUID);
        responseCharacteristic = cmdService.getCharacteristic(responseCharacteristicUUID);
        otaService = gatt.getService(otaServiceUUID);
        otaCharacteristic = otaService.getCharacteristic(otaCharacteristicUUID);
        initService = gatt.getService(initServiceUUID);
        initCharacteristic = initService.getCharacteristic(initCharacteristicUUID);
        infoService = gatt.getService(infoServiceUUID);


//        gatt.readCharacteristic(initCharacteristic);
//        res = cb.getResult(JbResult.READ_CHARACTERISTIC);
//        Log.d(TAG, "Init characteristic " + Arrays.toString(res.characteristic.getValue()));

        gatt.setCharacteristicNotification(responseCharacteristic, true);

        BluetoothGattDescriptor responseDescriptor = responseCharacteristic.getDescriptor(responseDescriptorUUID);
        gatt.readDescriptor(responseDescriptor);
        res = cb.getResult(JbResult.READ_DESCRIPTOR);
        Log.d(TAG, "Response descriptor: " + Arrays.toString(res.descriptor.getValue()));

        if (!Arrays.equals(res.descriptor.getValue(), BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
            responseDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            gatt.writeDescriptor(responseDescriptor);
            cb.getResult(JbResult.WRITE_DESCRIPTOR);
        }


//        for (UUID u : infoCharacteristicUUIDs) {
//            BluetoothGattCharacteristic tmp = infoService.getCharacteristic(u);
//            gatt.readCharacteristic(tmp);
//            cb.getResult(JbResult.READ_CHARACTERISTIC);
//        }
    }

    private void write(JawboneRequest req) throws InterruptedException {
        byte[] data = req.toByte();
        cmdCharacteristic.setValue(data);
        gatt.writeCharacteristic(cmdCharacteristic);

        this.pktCounter += 1;

        cb.getResult(JbResult.WRITE_CHARACTERISTIC);

        if (req.continuation != null) {
            byte[] continuationData = req.continuation.toByte();
            cmdCharacteristic.setValue(continuationData);

            gatt.writeCharacteristic(cmdCharacteristic);
            this.pktCounter += 1;
            cb.getResult(JbResult.WRITE_CHARACTERISTIC);
        }

        this.protoCounter += 1;
    }

    private void write(JawboneRequest req, SecuredStream ss) throws InterruptedException {
        byte[] data = req.toByte();

        byte[] partial = new byte[data.length-1];
        System.arraycopy(data, 1, partial, 0, data.length-1);

        byte[] encr = ss.a(partial);

        System.arraycopy(encr, 0, data, 1, encr.length);

        cmdCharacteristic.setValue(data);
        gatt.writeCharacteristic(cmdCharacteristic);

        this.pktCounter += 1;
        cb.getResult(JbResult.WRITE_CHARACTERISTIC);

        this.protoCounter += 1;
    }

    private JawboneResponse read() throws InterruptedException {
        JbResult r = cb.getResult(JbResult.CHANGED_CHARACTERISTIC);
        byte[] data = r.characteristic.getValue();

        JawboneResponse res = new JawboneResponse(data);
        if (res.k != null && res.i > res.k.length) {
            int remaining = res.i - res.k.length;
            JbResult r2 = cb.getResult(JbResult.CHANGED_CHARACTERISTIC);
            byte[] data2 = r2.characteristic.getValue();
            res.continuation = new JawboneResponse.Continuation(data2, remaining);

            if (data2.length - 1 > remaining) {
                BluetoothGattCharacteristic c = new BluetoothGattCharacteristic(r2.characteristic.getUuid(), r2.characteristic.getProperties(), r2.characteristic.getPermissions());
                byte[] tmp = new byte[data2.length - 1 - remaining];
                System.arraycopy(r2.characteristic.getValue(), 1 + remaining, tmp, 0, tmp.length);
                c.setValue(tmp);
                cb.pushResult(new JbResult(c, 0, JbResult.CHANGED_CHARACTERISTIC));
                Log.d(TAG, "rimesso: " + JbResult.bytesToString(tmp));
            }
        }
        return res;
    }

    private byte[] rawRead() throws InterruptedException {
        JbResult r = cb.getResult(JbResult.CHANGED_CHARACTERISTIC);
        return r.characteristic.getValue();
    }

    public void handshake() throws InterruptedException {
        JawboneRequest protoReq = new JawboneRequest(
            pktCounter,
            JawboneRequest.PROTOCOL_VERSION_REQUEST,
            0,
            protoCounter,
            0,
            null
        );

        Log.d(TAG, "Handshake REQ: " + protoReq.toString());
        write(protoReq);
        JawboneResponse protoRes = read();

        Log.d(TAG, "Handshake RES: " + protoRes.toString());

        // protocol version request
        // protocol version response

//        key = new byte[] {0x3D, 0x34, 0x07, (byte)0xDF, (byte)0xF5, 0x6D, 0x33, 0x71, 0x43, 0x73, 0x3F, 0x50, 0x50, (byte)0xD6, 0x52, 0x54};
        key = new byte[] {(byte)0x80, (byte)0xA0, 0x73, 0x5C, (byte)0xBC, (byte)0xD4, 0x61, 0x0B, 0x29, (byte)0xD3, 0x63, 0x67, (byte)0xD9, 0x24, (byte)0xCA, (byte)0x84};
        final SecureRandom secureRandom = new SecureRandom();

        byte[] array3 = new byte[8];  // first
        byte[] array2 = new byte[8];  // second
        byte[] array4 = new byte[16]; // destination
        byte[] array5 = new byte[16]; // padding

//        secureRandom.nextBytes(array2);
        array2 = new byte[]{(byte)0x94, (byte)0xE2, (byte)0x96, 0x3C, (byte)0xAD, 0x32, (byte)0xA7, 0x57};
//        array2 = new byte[]{(byte)0x94, (byte)0x91, 0x60, 0x66, 0x7E, (byte)0xB2, 0x10, (byte)0xCB};

        System.arraycopy(array2, 0, array4, 0, 8);
        System.arraycopy(array3, 0, array4, 8, 8);
        Arrays.fill(array5, (byte) 0);

        byte out[] = SecuredStream.a(array4, key, array5, 1, 2);
        Log.d(TAG, "Auth Out: " + JbResult.bytesToString(out));

        JawboneRequest authReq = new JawboneRequest(
                pktCounter,
                JawboneRequest.AUTHENTICATE_REQUEST,
                0,
                protoCounter,
                16,
                out
        );
        write(authReq);


        // If key is wrong, the band disconnects

        JawboneResponse authRes = read();
        Log.d(TAG, "Auth in: " + JbResult.bytesToString(authRes.payload()));


        // authenticate request
        // authenticate response
//        byte[] payload = new byte[]{(byte)0xED, 0x2B, 0x70, 0x3E, (byte)0xE1, 0x7F, 0x68, 0x45, 0x66, 0x4B, 0x7A, 0x18, 0x2C, 0x70, 0x3D, 0x47};
        byte[] payload = authRes.payload();
        out = SecuredStream.a(payload, key, array5, 2, 2);
        Log.d(TAG, "Check in1: " + JbResult.bytesToString(out));
        byte[] latter = new byte[16];
        System.arraycopy(out, 8, latter, 8, 8);
        Log.d(TAG, "Check in1/2: " + JbResult.bytesToString(latter));
        out = SecuredStream.a(out, key, array5, 1, 2);
        Log.d(TAG, "Check in2: " + JbResult.bytesToString(out));


        for (int i=0; i<out.length; i++) {
            out[i] = (byte)(out[i] ^ latter[i]);
        }

        JawboneRequest challengeReq = new JawboneRequest(
                pktCounter,
                JawboneRequest.CHALLENGE_REQUEST,
                0,
                protoCounter,
                16,
                out
        );
        write(challengeReq);

        JawboneResponse challengeRes = read();
        Log.d(TAG, "Challenge res: " + challengeRes.toString());


        // challenge request
        // challenge response

        byte[] phoneSeed = new byte[16];
        secureRandom.nextBytes(phoneSeed);

        JawboneRequest secureReq = new JawboneRequest(
                pktCounter,
                JawboneRequest.SECURE_REQUEST,
                0,
                protoCounter,
                16,
                phoneSeed
        );
        write(secureReq);


        JawboneResponse secureRes = read();
        byte[] deviceSeed = secureRes.payload();
        Log.d(TAG, "Device seed: " + JbResult.bytesToString(deviceSeed));

        byte[] tmpXor1 = rawRead();
        byte[] tmpXor2 = rawRead();
        byte[] xorData = new byte[17];
        System.arraycopy(tmpXor1, 0, xorData, 0, 8);
        System.arraycopy(tmpXor2, 1, xorData, 8, 9);

        SecuredStream ss = new SecuredStream(phoneSeed, key);


        JawboneRequest alertReq = new JawboneRequest(
                pktCounter,
                0x40,
                0,
                protoCounter,
                1,
                new byte[]{0x04}
        );
        write(alertReq, ss);

        // secure channel request
        // secure channel response
    }

    public void reset() throws InterruptedException {
        // vibrate command
        BluetoothGattCharacteristic c = initService.getCharacteristic(initCharacteristicUUID);
        c.setValue(new byte[]{0});
        gatt.writeCharacteristic(initCharacteristic);
        cb.getResult(JbResult.WRITE_CHARACTERISTIC);
        JbResult res;

//        Log.d(TAG, "Resetted");
//        gatt.disconnect();
//
//        res = cb.getResult(JbResult.CONNECTION);
//        Log.d(TAG, "Disconnected: " + new Integer(res.status).toString());
        while(true) {
            res = cb.getResult(JbResult.CONNECTION);
            if (res.status == BluetoothProfile.STATE_DISCONNECTED) {
                break;
            }
        }
        gatt.connect();
        while(true) {
            res = cb.getResult(JbResult.CONNECTION);
            if (res.status == BluetoothProfile.STATE_CONNECTED) {
                break;
            }
        }
    }
}
