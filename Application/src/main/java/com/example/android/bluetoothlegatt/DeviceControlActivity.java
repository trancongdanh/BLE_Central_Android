package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.danhtc.danhble.R;
import com.danhtc.danhblelib.DanhBluetoothLeDevice;

public class DeviceControlActivity extends Activity implements DanhBluetoothLeDevice.DanhBluetoothLeDeviceListener {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    final Handler handler = new Handler();

    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private DanhBluetoothLeDevice mBluetoothLeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        mDataField = findViewById(R.id.data_value);
        mDataField.setMovementMethod(new ScrollingMovementMethod());

        Button btnRestart = findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLog();
                mBluetoothLeService.connect(mDeviceAddress);
            }
        });

        getActionBar().setTitle(mDeviceName != null ? mDeviceName : "");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mBluetoothLeService = new DanhBluetoothLeDevice();
        mBluetoothLeService.initialize(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService = null;
    }

    private void updateLog(final String newMessage) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                String messageStr = mDataField.getText().toString();
                messageStr += "\n" + newMessage;
                mDataField.setText(messageStr);
            }
        });
    }

    private void clearLog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mDataField.setText("");
                mDataField.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void onGattConnected() {
        updateLog("Gatt connected");
    }

    @Override
    public void onGattDisconnect() {
        updateLog("Gatt disconnect");
    }

    @Override
    public void onGattDisconnected() {
        updateLog("Gatt disconnected");

        //Reconnect with the peripheral
        if (mBluetoothLeService != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 1s = 1000ms
                    mBluetoothLeService.connect(mDeviceAddress);
                }
            }, 1000);
            updateLog("Sleep 1s.");
        }
    }

    @Override
    public void onGattServicesDiscovered() {
        updateLog("Gatt services discovered.");
        mBluetoothLeService.writeCustomCharacteristic(DanhBluetoothLeDevice.RED_STATUS);
        updateLog("Send RED. \nWaiting the response from the peripheral ...");
    }

    @Override
    public void onGattWriteCharacteristicSuccessfully() {
        updateLog("The response is successful.");
    }

    @Override
    public void onGattWriteCharacteristicRed() {
        updateLog("Send RED. \nWaiting the response from the peripheral ...");
    }

    @Override
    public void onGattWriteCharacteristicGreen() {
        updateLog("Send GREEN. \nWaiting the response from the peripheral ...");
    }

    @Override
    public void onGattWriteCharacteristicSleep() {
        updateLog("Sleeping 1s.");
    }

    @Override
    public void onGattError(String errorMessage) {
        updateLog(errorMessage);
    }
}
