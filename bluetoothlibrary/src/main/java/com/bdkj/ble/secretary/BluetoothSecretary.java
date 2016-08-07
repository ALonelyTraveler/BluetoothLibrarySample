package com.bdkj.ble.secretary;

import com.bdkj.ble.connector.BluetoothConnector;

/**
 * 秘书,读取和发送消息
 * @author: chenwei
 * @version: V1.0
 */
public interface BluetoothSecretary {

    /**
     * 雇用
     * @param mConnector 连接器
     */
    void employ(BluetoothConnector mConnector);

    /**
     * 解雇
     */
    void dismiss();
}
