package com.bdkj.ble.connector;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
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

    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCallback mGattCallback;

    public BleConnector(Context mContext,BluetoothGattCallback gattCallback) {
        this.mContext = mContext;
        this.mGattCallback = gattCallback;
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        mBluetoothGatt = device.connectGatt(mContext.getApplicationContext(), false, mGattCallback);
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    @Override
    public void cancelConnect() {
        isCancel = true;
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    public BluetoothGatt getBluetoothGatt()
    {
        return mBluetoothGatt;
    }

    public void closeGatt()
    {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }

}
