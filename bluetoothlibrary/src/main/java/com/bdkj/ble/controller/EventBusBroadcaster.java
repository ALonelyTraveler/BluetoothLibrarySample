package com.bdkj.ble.controller;

import android.bluetooth.BluetoothGattService;
import android.util.Log;
import com.bdkj.ble.event.ConnectAction;
import com.bdkj.ble.event.ServiceAction;
import com.bdkj.ble.event.StatusAction;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 使用EventBus进行广播
 * @author: chenwei
 * @version: V1.0
 */
public class EventBusBroadcaster implements IBroadcaster {
    @Override
    public void sendConnectAction(String action) {
        Log.d("EventBusBroadcaster", "sendConnectAction:" + action);
        EventBus.getDefault().post(new ConnectAction(action));
    }

    @Override
    public void sendStatus(String status) {
        Log.d("EventBusBroadcaster", "sendStatus:" + status);
        EventBus.getDefault().post(new StatusAction(status));
    }

    @Override
    public void sendService(List<BluetoothGattService> services) {
//        Log.d("EventBusBroadcaster", "sendService:" + services.toString());
        EventBus.getDefault().post(new ServiceAction(services));
    }

}
