package com.bdkj.ble.spp;

import com.bdkj.ble.connector.BluetoothConnector;

/**
 * @ClassName: BluetoothSecretary
 * @Description: 秘书,读取和发送消息
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/3 下午4:09
 */
public interface BluetoothSecretary {

    /**
     * 雇用
     */
    void employ(BluetoothConnector mConnector);

    /**
     * 解雇
     */
    void dismiss();
}
