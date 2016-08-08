package com.bdkj.ble.controller;

import android.bluetooth.BluetoothGattService;
import com.bdkj.ble.connector.BluetoothConnector;
import com.bdkj.ble.secretary.BluetoothSecretary;

import java.util.List;

/**
 * 蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
public abstract class BluetoothController<T extends BluetoothSecretary> extends BluetoothConnector  implements IBroadcaster{

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
     * 秘书的引用
     */
    protected T mSecretary;

    /**
     * 广播发射器
     */
    protected IBroadcaster mBroadcaster;

    /**
     * 是否重试
     */
    protected boolean retry = false;

    /**
     * 最大重试次数
     */
    protected int maxReconnectCount = 3;

    /**
     * 当前重试次数
     */
    protected int counter = 0;

    /**
     * 是否初次连接
     */
    protected boolean firstConnect = false;

    /**
     * 是否是中途断开
     */
    protected boolean suspend = false;

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
     * 设置广播器
     *
     * @param broadcaster the broadcaster
     */
    public void setBroadcaster(IBroadcaster broadcaster) {
        this.mBroadcaster = broadcaster;
    }

    /**
     * Gets broadcaster.
     * 获取广播器
     *
     * @return the broadcaster
     */
    public IBroadcaster getBroadcaster() {
        return mBroadcaster;
    }

    /**
     * Sets retry.
     *
     * @param retry    the retry
     * @param maxCount the max count
     */
    public void setRetry(boolean retry,int maxCount) {
        this.retry = retry;
        this.maxReconnectCount = maxCount >= 1 ? maxCount : 1;
    }

    /**
     * Is retry boolean.
     *
     * @return the boolean
     */
    public boolean isRetry() {
        return retry;
    }

    /**
     * ===================================
     * <p>
     * IBroadcaster的实现,方便在该类中进行调用
     * <p>
     * ===================================
     */
    @Override
    public void sendConnectAction(String action) {
        if (mBroadcaster != null) {
            mBroadcaster.sendConnectAction(action);
        }
    }

    @Override
    public void sendStatus(String status) {
        if (mBroadcaster != null) {
            mBroadcaster.sendStatus(status);
        }
    }

    @Override
    public void sendService(List<BluetoothGattService> services) {
        if (mBroadcaster != null) {
            mBroadcaster.sendService(services);
        }
    }


}
