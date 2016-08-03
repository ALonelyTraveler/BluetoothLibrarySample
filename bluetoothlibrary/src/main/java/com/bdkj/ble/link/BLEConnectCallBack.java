package com.bdkj.ble.link;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * @ClassName: BLEConnectCallBack
 * @Description: BLE连接回调
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/3 下午5:43
 */
public interface BLEConnectCallBack extends ConnectCallBack {
    /**
     * 发现服务完成
     */
    public void discoverService(List<BluetoothGattService> services);
}
