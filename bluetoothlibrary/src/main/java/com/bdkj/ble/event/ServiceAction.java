package com.bdkj.ble.event;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * 服务通知事件
 * @author: chenwei
 * @version: V1.0
 */
public class ServiceAction {
    public final List<BluetoothGattService> services;

    public ServiceAction(List<BluetoothGattService> services) {
        this.services = services;
    }
}
