package com.bdkj.ble.scanner;

/**
 * Created by weimengmeng on 2016/5/13.
 */
public interface ScanCallBack {
    /**
     * 开始搜索
     */
    public abstract void startScan();

    /**
     * 结束搜索
     */
    public abstract void finishScan();

    /**
     * 找到设备后回掉
     *
     * @param name    the name
     * @param address the address
     * @param rssi    the rssi
     * @param type    the type
     */
    public abstract void foundSpeificDevice(String name, String address,
                                            int rssi, String type);
}
