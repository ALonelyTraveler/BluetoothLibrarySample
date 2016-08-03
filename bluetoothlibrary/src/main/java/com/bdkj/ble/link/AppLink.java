package com.bdkj.ble.link;

import android.bluetooth.BluetoothDevice;

/**
 * Created by weimengmeng on 2016/5/13.
 */
public interface AppLink {
    public abstract void connect(BluetoothDevice device,
                                 ConnectCallBack callBack);

    public abstract void disConnect();

    public abstract String getDeviceMac();

    public abstract boolean isConnect();

    public abstract void write(byte[] data, WriteHelper helper);

    public abstract int getConnectStation();

    public abstract void read();
}
