package com.bdkj.ble.connector;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Ble连接器
 * Created by chenwei on 16/5/23.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleConnector extends BluetoothConnector {
    /**
     * 最好使用Application.getApplicationContext();
     */
    private Context mContext;

    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (BluetoothGatt.STATE_CONNECTED == newState) {
                gatt.discoverServices();
                setConnectState(STATE_CONNECTED);
                if (mCallBack != null) {
                    mCallBack.connectSuccess();
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                if (status == BluetoothGatt.STATE_CONNECTING) {
                    //与蓝牙设备连接时断开，表示连接时并没有成功
                    setConnectState(STATE_INIT);
                    if (mCallBack != null) {
                        mCallBack.connectFail();
                    }
                } else if (status == BluetoothGatt.STATE_CONNECTED || status == BluetoothGatt.STATE_DISCONNECTING) {
                    //用户手动断开或设备主动断开的情况下标记Flag并发送断开的通知
                    setConnectState(STATE_INIT);
                    if (getBreakFlag() == FLAG_BREAK_DEFAULT) {
                        setBreakFlag(FLAG_BREAK_DEVICE_INITIATIVE);
                    }
                    if (mContext != null) {
                        Intent intent = new Intent(CONNECT_INTERRUPT_ACTION);
                        mContext.sendBroadcast(intent);
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (mContext != null) {
                Intent intent = new Intent();
                intent.setAction(RECEIVE_DATA_ACTION);
                intent.putExtra(EXTRA_DATA, characteristic.getValue());
                mContext.sendBroadcast(intent);
            }
        }
    };

    public BleConnector(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void connect(BluetoothDevice device) {
        setConnectState(STATE_CONNECTING);
        bluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
    }

    @Override
    public void disconnect() {
        if (bluetoothGatt != null) {
            setConnectState(STATE_DISCONNECTING);
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            setConnectState(STATE_INIT);
        }
    }
}
