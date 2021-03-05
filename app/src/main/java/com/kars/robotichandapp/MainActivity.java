package com.kars.robotichandapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.util.UUID;




public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;
    Button btnConnectToRoboticHand;
    BluetoothGattCharacteristic characteristic;
    BluetoothGatt gatt;
    UUID HM10_SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    UUID HM10_CHAR_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        discover();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null && device.getName().equals("DSD TECH")) {
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        Log.d(TAG, "Trying to pair with " + device.getName());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            gatt = device.connectGatt(context, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_AUTO);
                        }
                        else {
                            gatt = device.connectGatt(context, false, bluetoothGattCallback);
                        }
                        mBluetoothAdapter.cancelDiscovery();
                    }
                }
            }
        }
    };


    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server.");
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.");
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "Services Discovered: ");
            characteristic = gatt.getService(HM10_SERVICE_UUID).getCharacteristic(HM10_CHAR_UUID);

        }
    };


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConnectToRoboticHand = (Button) findViewById(R.id.btnConnectToRoboticHand);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnConnectToRoboticHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        final TextView sliderText0 = (TextView)findViewById(R.id.verticalSeekbarText0);
        sliderText0.setTextSize(20);
        final TextView sliderText1 = (TextView)findViewById(R.id.verticalSeekbarText1);
        sliderText1.setTextSize(20);
        final TextView sliderText2 = (TextView)findViewById(R.id.verticalSeekbarText2);
        sliderText2.setTextSize(20);
        final TextView sliderText3 = (TextView)findViewById(R.id.verticalSeekbarText3);
        sliderText3.setTextSize(20);
        final TextView sliderText4 = (TextView)findViewById(R.id.verticalSeekbarText4);
        sliderText4.setTextSize(20);
        VerticalSeekBar verticalSeekbar0 = (VerticalSeekBar) findViewById(R.id.verticalSeekBar0);
        VerticalSeekBar verticalSeekbar1 = (VerticalSeekBar) findViewById(R.id.verticalSeekBar1);
        VerticalSeekBar verticalSeekbar2 = (VerticalSeekBar) findViewById(R.id.verticalSeekBar2);
        VerticalSeekBar verticalSeekbar3 = (VerticalSeekBar) findViewById(R.id.verticalSeekBar3);
        VerticalSeekBar verticalSeekbar4 = (VerticalSeekBar) findViewById(R.id.verticalSeekBar4);
        verticalSeekbar0.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sliderText0.setText(""+progress);
                double pinky = Double.parseDouble(sliderText0.getText().toString());
                String Pinky = Integer.toString((int)((180.0*3.0)+(180*pinky/100.0))) + '\n';
                Log.d(TAG, String.valueOf((int)((180.0*3.0)+(180*pinky/100.0))));
                if (writeCharacteristic(Pinky)) {
                    Log.d(TAG,"true");
                }
                else {
                    Log.d(TAG,"false");
                }
            }
        });
        verticalSeekbar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sliderText1.setText(""+progress);
                double ring = Double.parseDouble(sliderText1.getText().toString());
                String Ring = Integer.toString((int)((180.0*2.0)+(180*ring/100.0))) + '\n';
                Log.d(TAG, String.valueOf((int)((180.0*2.0)+(180*ring/100.0))));
                if (writeCharacteristic(Ring)) {
                    Log.d(TAG,"true");
                }
                else {
                    Log.d(TAG,"false");
                }
            }
        });
        verticalSeekbar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sliderText2.setText(""+progress);
                double middle = Double.parseDouble(sliderText2.getText().toString());
                String Middle = Integer.toString((int)((180.0*1.0)+(180*middle/100.0))) + '\n';
                Log.d(TAG, String.valueOf((int)((180.0*1.0)+(180*middle/100.0))));
                if (writeCharacteristic(Middle)) {
                    Log.d(TAG,"true");
                }
                else {
                    Log.d(TAG,"false");
                }
            }
        });
        verticalSeekbar3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sliderText3.setText(""+progress);
                double pointer = Double.parseDouble(sliderText3.getText().toString());
                String Pointer = Integer.toString((int)((180.0*0.0)+(180*pointer/100.0))) + '\n';
                Log.d(TAG, String.valueOf((int)((180.0*0.0)+(180*pointer/100.0))));
                if (writeCharacteristic(Pointer)) {
                    Log.d(TAG,"true");
                }
                else {
                    Log.d(TAG,"false");
                }
            }
        });
        verticalSeekbar4.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sliderText4.setText(""+progress);
                double thumb = Double.parseDouble(sliderText4.getText().toString());
                String Thumb = Integer.toString((int)((180.0*4.0)+(180*thumb/100.0))) + '\n';
                Log.d(TAG, String.valueOf((int)((180.0*4.0)+(180*thumb/100.0))));
                if (writeCharacteristic(Thumb)) {
                    Log.d(TAG,"true");
                }
                else {
                    Log.d(TAG,"false");
                }
            }
        });

    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: already connected to BT.");
            //mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            discover();
        }

    }

    public void discover() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean writeCharacteristic(String data) {
        Log.d(TAG, data);
        //check mBluetoothGatt is available
        if (gatt == null) {
            Log.e(TAG, "lost connection");
            return false;
        }
        BluetoothGattService Service = gatt.getService(HM10_SERVICE_UUID);
        if (Service == null) {
            Log.e(TAG, "service not found!");
            return false;
        }
        characteristic = Service.getCharacteristic(HM10_CHAR_UUID);
        if (characteristic == null) {
            Log.e(TAG, "char not found!");
            return false;
        }

        byte[] value = data.getBytes();
        characteristic.setValue(value);
        boolean status = gatt.writeCharacteristic(characteristic);
        return status;
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}