package com.bdkj.ble.controller;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * 传递消息的方式
 * @author: chenwei
 * @version: V1.0
 */
public interface IBroadcaster {

    /**
     * 发送连接动作消息
     * <pre>
     * {@link com.bdkj.ble.event.EventConstants#SUCCESS}
     * {@link com.bdkj.ble.event.EventConstants#FAIL}
     * {@link com.bdkj.ble.event.EventConstants#TIMEOUT}
     * </pre>
     *
     * @param action 动作
     */
    public void sendConnectAction(String action);

    /**
     * 发送状态消息
     * <pre>
     *     {@link com.bdkj.ble.event.EventConstants#STATE_CONNECTED}
     *     {@link com.bdkj.ble.event.EventConstants#STATE_DISCONNECTED}
     * </pre>
     *
     * @param status 状态
     */
    public void sendStatus(String status);

    /**
     * 发送服务消息
     * <pre>
     *     {@link com.bdkj.ble.event.EventConstants#DISCOVERY_SERVICE}
     * </pre>
     *
     * @param services 蓝牙服务列表
     */
    public void sendService(List<BluetoothGattService> services);

}
