package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.danhtc.danhble.R;
import com.danhtc.danhblelib.BluetoothLeScanner;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends Activity implements BluetoothLeScanner.BluetoohLeScannerListener {

    private BluetoothLeScanner mBluetoothLeScanner;

    private ProgressBar mProgressBar;
    private Button mBtnScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mProgressBar = findViewById(R.id.progress_bar);
        mBtnScan = findViewById(R.id.btn_scan);

        mProgressBar.setVisibility(View.INVISIBLE);

        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mBluetoothLeScanner = new BluetoothLeScanner();
        mBluetoothLeScanner.initScanner(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    public void onScanCallback(BluetoothDevice device, int rssi, byte[] scanRecord) {

        stopScan();

        final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        DeviceScanActivity.this.startActivity(intent);
        DeviceScanActivity.this.finish();
    }

    private void startScan() {
        mBluetoothLeScanner.startScanner();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopScan() {
        mBluetoothLeScanner.stopScanner();
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}