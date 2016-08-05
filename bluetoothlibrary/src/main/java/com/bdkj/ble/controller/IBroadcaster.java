package com.bdkj.ble.controller;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * @ClassName: IBroadcaster
 * @Description: 传递消息的方式
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/5 上午9:57
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
     * @param action
     */
    public void sendConnectAction(String action);

    /**
     * 发送状态消息
     * <pre>
     *     {@link com.bdkj.ble.event.EventConstants#STATE_CONNECTED}
     *     {@link com.bdkj.ble.event.EventConstants#STATE_DISCONNECTED}
     * </pre>
     *
     * @param status
     */
    public void sendStatus(String status);

    /**
     * 发送服务消息
     * <pre>
     *     {@link com.bdkj.ble.event.EventConstants#DISCOVERY_SERVICE}
     * </pre>
     *
     * @param services
     */
    public void sendService(List<BluetoothGattService> services);

}
