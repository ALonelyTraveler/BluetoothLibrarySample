package com.bdkj.ble.controller;

import android.bluetooth.BluetoothGattService;
import com.bdkj.ble.event.ConnectAction;
import com.bdkj.ble.event.ServiceAction;
import com.bdkj.ble.event.StatusAction;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @ClassName: EventBusBroadcaster
 * @Description: 使用EventBus进行广播
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/5 下午4:01
 */
public class EventBusBroadcaster implements IBroadcaster {
    @Override
    public void sendConnectAction(String action) {
        EventBus.getDefault().post(new ConnectAction(action));
    }

    @Override
    public void sendStatus(String status) {
        EventBus.getDefault().post(new StatusAction(status));
    }

    @Override
    public void sendService(List<BluetoothGattService> services) {
        EventBus.getDefault().post(new ServiceAction(services));
    }

}
