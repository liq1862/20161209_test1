package com.example.user.a20161209_test1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class DeviceActivity extends AppCompatActivity {
    final int REQUEST_ENABLE_BT = 321;
    MyReceiver mReceiver;
    BluetoothAdapter mBtAdapter;
    UUID mUUID;
    BluetoothDevice myDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        mReceiver = new MyReceiver();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter( );

        if( !mBtAdapter.isEnabled() ) {
            Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( enableIntent, REQUEST_ENABLE_BT ); }

    }

    public void click1(View v)
    {
        mBtAdapter.startDiscovery();
    }

    public void click2(View v)
    {
        BluetoothSocket clienSocket= null;
        try {
            clienSocket = myDevice.createRfcommSocketToServiceRecord(mUUID);
            clienSocket.connect();

            InputStream is = clienSocket.getInputStream();
            OutputStream os = clienSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        // 搜尋到藍牙裝置時，呼叫我們的函式
        IntentFilter filter = new IntentFilter( BluetoothDevice.ACTION_FOUND );
        this.registerReceiver( mReceiver, filter );

        // 搜尋的過程結束後，也呼叫我們的函式
        filter = new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );
        this.registerReceiver( mReceiver, filter );
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mReceiver);
    }

    class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BT", "Found!!");
            Toast.makeText(DeviceActivity.this, "Bluetooth found!", Toast.LENGTH_SHORT).show();

            String action = intent.getAction();
            //找到設備
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("BT", "Action Found");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.d("BT", "find device:" + device.getName()
                            + device.getAddress());

                    Toast.makeText(DeviceActivity.this, "device:" + device.getName()
                            + device.getAddress(), Toast.LENGTH_SHORT).show();
                    ParcelUuid[] uuids = device.getUuids();
                    mUUID = uuids[0].getUuid();
                    myDevice = device;
                }
            }
        }
    }

}

