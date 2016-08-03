package com.bdkj.ble.controller;

import com.bdkj.ble.BluetoothLibrary;
import com.bdkj.ble.connector.BluetoothConnector;
import com.bdkj.ble.link.ConnectCallBack;
import com.bdkj.ble.spp.BluetoothSecretary;

/**
 * 蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
public abstract class BluetoothController<T extends BluetoothSecretary> extends BluetoothConnector {
    /**
     * 连接断开
     */
    public static final String CONNECT_INTERRUPT_ACTION = BluetoothLibrary.getPackageName() + ".BluetoothConnector.Connect.Interrupt";

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
     * 连接状态
     */
    protected int connectState = STATE_INIT;

    /**
     * The Connect mac.
     */
    protected String connectMac;

    /**
     * The M call back.
     */
    protected ConnectCallBack mCallBack;

    /**
     * 秘书的引用
     */
    protected T mSecretary;

    /**
     * Gets connect mac.
     *
     * @return the connect mac
     */
    public String getConnectMac() {
        return connectMac;
    }

    /**
     * Is connect boolean.
     *
     * @return the boolean
     */
    public boolean isConnect() {
        return connectState == STATE_CONNECTED;
    }

    /**
     * Gets connect state.
     * 获取连接状态
     * @return the connect state
     */
    public int getConnectState() {
        return connectState;
    }

    /**
     * Gets call back.
     *
     * @return the call back
     */
    public ConnectCallBack getCallBack() {
        return mCallBack;
    }

    /**
     * Sets call back.
     *
     * @param mCallBack the m call back
     */
    public void setCallBack(ConnectCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    /**
     * 设置秘书
     *
     * @param t the t
     */
    public void setBluetoothSecretary(T t)
    {
        this.mSecretary = t;
    }

    /**
     * Gets bluetooth secretary.
     * 获取秘书
     *
     * @return the bluetooth secretary
     */
    public T getBluetoothSecretary()
    {
        return this.mSecretary;
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

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Cancel timeout.
     */
    public abstract void cancelTimeout();

    /**
     * =====================
     * end --- 超时处理
     * =====================
     */


}
