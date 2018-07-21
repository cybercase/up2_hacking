package me.brilli.stefano.up2test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    static String TAG = "MAIN";

    BluetoothManager bluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        device = mBluetoothAdapter.getRemoteDevice("E9:7C:2F:27:15:00");

        setContentView(R.layout.activity_main);
        Button b = (Button)findViewById(R.id.testButton);
        b.setOnClickListener(this);

        Button disconnect = (Button)findViewById(R.id.button2);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "handshake");
//        mBluetoothAdapter.disable();

//        try {
//            Thread.sleep(200, 0);
//            mBluetoothAdapter.enable();
//            Thread.sleep(200, 0);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        JawboneGattCallback jbGattCb = new JawboneGattCallback();
        JawboneGatt jbGatt = new JawboneGatt(device.connectGatt(this, true, jbGattCb));
        try {
            JawboneDevice jbDevice = new JawboneDevice(jbGatt, jbGattCb);
//            jbDevice.reset();
            jbDevice.handshake();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jbGatt.close();
        }
    }


    public void disconnect () {
        Log.d(TAG, "disconnect");

        JawboneGattCallback jbGattCb = new JawboneGattCallback();
        JawboneGatt jbGatt = new JawboneGatt(device.connectGatt(this, true, jbGattCb));
        try {
            JawboneDevice jbDevice = new JawboneDevice(jbGatt, jbGattCb);
            jbDevice.reset();
            jbGatt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jbGatt.close();
        }
    }
}
