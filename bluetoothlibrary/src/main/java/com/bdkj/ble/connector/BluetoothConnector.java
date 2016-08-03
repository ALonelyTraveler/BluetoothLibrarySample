package com.bdkj.ble.connector;

import android.bluetooth.BluetoothDevice;
import com.bdkj.ble.link.ConnectCallBack;

import java.io.IOException;

/**
 * 蓝牙连接器
 * Created by chenwei on 16/5/23.
 */
public abstract class BluetoothConnector {

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
     * 断开标记：用户手动关闭蓝牙
     */
    public static final int FLAG_BREAK_BLUETOOTH_CLOSE = 1;
    /**
     * 断开标记：程序进入后台断开
     */
    public static final int FLAG_BREAK_APP_BACKGROUND = 2;

    /**
     * 断开标记：设备主动断开或因为其它未知原因断开
     */
    public static final int FLAG_BREAK_DEVICE_INITIATIVE = 3;

    /**
     * 连接状态
     */
    private int connectState = STATE_INIT;

    /**
     * 断开标记
     */
    private int breakFlag = FLAG_BREAK_NORMAL;

    /**
     * 当前连接的蓝牙设备
     */
    private String connectMac = null;

    protected ConnectCallBack mCallBack;

    public abstract void connect(BluetoothDevice device) throws IOException;

    public abstract void disconnect() throws IOException;

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

    public void setBreakFlag(int breakFlag) {
        this.breakFlag = breakFlag;
    }

    public int getBreakFlag() {
        return breakFlag;
    }

    public ConnectCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(ConnectCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

}
