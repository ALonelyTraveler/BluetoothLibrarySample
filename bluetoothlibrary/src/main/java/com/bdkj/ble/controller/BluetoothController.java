package com.bdkj.ble.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import com.bdkj.ble.link.ConfigInterface;
import com.bdkj.ble.link.ConnectCallBack;

/**
 * 蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
public abstract class BluetoothController {
    /**
     * 最好使用Application.getApplicationContext();
     */
    protected Context mContext;
    /**
     * 连接断开
     */
    public static final String CONNECT_INTERRUPT_ACTION = "BluetoothConnector.Connect.Interrupt";

    /**
     * 接收数据通知
     */
    public static final String RECEIVE_DATA_ACTION = "BluetoothConnector.data.receive";

    /**
     * 接收数据时传递的bundle的key
     */
    public static final String EXTRA_DATA = "data";

    /**
     * 初始化状态
     */
    public static final int STATE_INIT = 0;

    /**
     * 正在连接状态
     */
    public static final int STATE_CONNECTING = 1;

    /**
     * 已连接状态
     */
    public static final int STATE_CONNECTED = 2;

    /**
     * 正在断开的状态
     */
    public static final int STATE_DISCONNECTING = 3;


    /**
     * 标记 未断开
     */
    public static final int FLAG_BREAK_DEFAULT = -1;
    /**
     * 断开标记：正常断开
     */
    public static final int FLAG_BREAK_NORMAL = 0;

    /**
     * 连接状态
     */
    private int connectState = STATE_INIT;

    protected String connectMac;

    protected ConnectCallBack mCallBack;

    public BluetoothController(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 直接连接
     *
     * @param device
     */
    public abstract void connect(BluetoothDevice device);

    public abstract void disconnect();

    public void setConnectMac(String connectMac) {
        this.connectMac = connectMac;
    }

    public String getConnectMac() {
        return connectMac;
    }

    public boolean isConnect() {
        return connectState == STATE_CONNECTED;
    }

    public void setConnectState(int state) {
        this.connectState = state;
    }

    public int getConnectState() {
        return connectState;
    }

    public ConnectCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(ConnectCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    /**
     * 主要用于ble蓝牙的数据交互
     *
     * @param data
     * @param helper
     * @return
     */
    public abstract boolean write(byte[] data, ConfigInterface helper);

    /**
     * ble模式下取用默认的ConfigInterface
     *
     * @param data
     * @return
     */
    public abstract boolean write(byte[] data);

    /**
     * 分发数据
     *
     * @param data
     */
    public void dispatchData(byte[] data) {
        Intent intent = new Intent();
        intent.setAction(RECEIVE_DATA_ACTION);
        intent.putExtra(EXTRA_DATA, data);
        mContext.sendBroadcast(intent);
    }

    /**
     * =====================
     * start --- 超时处理
     * =====================
     */
    /**
     * 默认超时时间
     */
    public static final long DEFAULT_TIMEOUT = 12000;

    /**
     * 搜索的超时时间
     */
    protected long timeout = DEFAULT_TIMEOUT;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public abstract void cancelTimeout();

    /**
     * =====================
     * end --- 超时处理
     * =====================
     */


}
