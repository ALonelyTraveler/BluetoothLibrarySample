package com.bdkj.ble.scanner.filter;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙搜索时的过滤器
 * @author: chenwei
 * @version: V1.0
 */
public interface BluetoothFilter {
    /**
     * 过滤设备是否正确
     * @param device
     * @return
     */
    public boolean filter(BluetoothDevice device,String localName,int rssi);
}
