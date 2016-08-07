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
     * @param device 蓝牙设备
     * @param localName 蓝牙名称
     * @param rssi 信号强度
     * @return 是否匹配
     */
    public boolean filter(BluetoothDevice device,String localName,int rssi);
}
