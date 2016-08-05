package com.bdkj.ble.event;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * @ClassName: ServiceAction
 * @Description: 服务通知事件
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/5 下午4:04
 */
public class ServiceAction {
    public final List<BluetoothGattService> services;

    public ServiceAction(List<BluetoothGattService> services) {
        this.services = services;
    }
}
