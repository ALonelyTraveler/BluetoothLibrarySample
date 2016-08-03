package com.bdkj.ble.scanner;

import com.bdkj.ble.scanner.filter.BluetoothFilter;

/**
 * 扫描器的基类
 *
 * @author: chenwei
 * @version: V1.0
 */
public interface Scanner {
    /**
     * Start scan.
     */
    public abstract void startScan();

    /**
     * 设置搜索回调
     *
     * @param callBack the call back
     */
    public abstract void setCallBack(ScanCallBack callBack);

    /**
     * Gets call back.
     *
     * @return the call back
     */
    public abstract ScanCallBack getCallBack();

    /**
     * Stop scan.
     */
    public abstract void stopScan();


    /**
     * 是否正在搜索
     *
     * @return boolean boolean
     */
    public abstract boolean isScanning();

    /**
     * Sets bluetooth filter.
     *
     * @param filter the filter
     */
    public abstract void setBluetoothFilter(BluetoothFilter filter);

    /**
     * Gets bluetooth filter.
     *
     * @return the bluetooth filter
     */
    public abstract BluetoothFilter getBluetoothFilter();

}
