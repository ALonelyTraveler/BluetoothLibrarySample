package com.bdkj.ble.scanner.filter;

import android.bluetooth.BluetoothDevice;

/**
 *  默认过滤器
 * @author: chenwei
 * @version: V1.0
 */
public class DefaultFilter implements BluetoothFilter {

    @Override
    public boolean filter(BluetoothDevice device, String localName, int rssi) {
        return true;
    }
}
