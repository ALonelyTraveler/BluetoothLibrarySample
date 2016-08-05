package com.bdkj.ble.controller;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import com.bdkj.ble.BluetoothLibrary;

import java.util.List;

/**
 * @ClassName: EventBusBroadcaster
 * @Description: 使用EventBus进行广播
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/5 下午4:01
 */
public class ReceiverBroadcaster implements IBroadcaster {
    /**
     * The constant CONNECT_ACTION.
     */
    public final static String CONNECT_ACTION = BluetoothLibrary.getPackageName() + ".connect";
    /**
     * The constant STATUS_ACTION.
     */
    public final static String STATUS_ACTION = BluetoothLibrary.getPackageName() + ".status";
    /**
     * The constant SERVICE_ACTION.
     */
    public final static String SERVICE_ACTION = BluetoothLibrary.getPackageName() + ".service";

    /**
     * The constant EXTRA_ACTION_KEY.
     */
    public final static String EXTRA_ACTION_KEY = "action";
    private Context mContext;

    public ReceiverBroadcaster(Context mContext) {
        this.mContext = mContext;
        if (mContext == null) {
            throw new NullPointerException("mContext is null");
        }
    }

    @Override
    public void sendConnectAction(String action) {
        Intent intent = new Intent(CONNECT_ACTION);
        intent.putExtra(EXTRA_ACTION_KEY, action);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void sendStatus(String status) {
        Intent intent = new Intent(STATUS_ACTION);
        intent.putExtra(EXTRA_ACTION_KEY, status);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void sendService(List<BluetoothGattService> services) {
        //Intent无法传递BluetoothGattService对象
        Intent intent = new Intent(SERVICE_ACTION);
        mContext.sendBroadcast(intent);
    }

}
