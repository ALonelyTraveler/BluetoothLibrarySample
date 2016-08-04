package com.bdkj.ble.spp;

import com.bdkj.ble.connector.BluetoothConnector;

/**
 * @ClassName: BleSecretary
 * @Description: 低功耗蓝牙秘书
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/3 下午4:21
 */
public class BleSecretary implements BluetoothSecretary {
    private BluetoothConnector mConnector;

    @Override
    public void employ(BluetoothConnector mConnector) {
        this.mConnector = mConnector;
    }

    @Override
    public void dismiss() {

    }
}
