package com.bdkj.ble.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;

/**
 * 蓝牙连接器
 * Created by chenwei on 16/5/23.
 */
public abstract class BluetoothConnector {
    /**
     * 是否取消
     */
    protected boolean isCancel = false;

    /**
     * 连接蓝牙设备
     *
     * @param device the device
     * @throws IOException the io exception
     */
    public abstract void connect(BluetoothDevice device) throws IOException;

    /**
     * 连接蓝牙设备
     *
     * @param address the address
     * @throws IOException the io exception
     */
    public void connect(String address) throws IOException {
        connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
    }

    /**
     * 断开蓝牙连接
     * 在连接的情况下调用此方法
     *
     * @throws IOException the io exception
     */
    public abstract void disconnect() throws IOException;

    /**
     * 取消连接
     * 在未连接的情况下调用此方法取消
     */
    public abstract void cancelConnect();

    /**
     * Is cancel boolean.
     *
     * @return the boolean
     */
    public boolean isCancel()
    {
        return isCancel;
    }

}
