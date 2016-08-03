package com.bdkj.ble.connector;

/**
 * 传统蓝牙读取数据时的回调
 * Created by chenwei on 16/5/24.
 */
public interface ClassicReadCallBack {
    /**
     * 数据接收
     *
     * @param data the data
     */
    public void dataReceive(byte[] data);

    /**
     * 读取时中断
     *
     * @param isCancel the is cancel
     */
    public void readBreak(boolean isCancel);
}
