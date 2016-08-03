package com.bdkj.ble.scanner;

/**
 * Created by weimengmeng on 2016/5/13.
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
     * 设置筛选条件
     *
     * @param names the names
     */
    public abstract void setNameFilter(String... names);

    /**
     * Sets address filter.
     *
     * @param address the address
     */
    public abstract void setAddressFilter(String... address);

    /**
     * Get name filter string [ ].
     *
     * @return the string [ ]
     */
    public abstract String[] getNameFilter();

    /**
     * Get address filter string [ ].
     *
     * @return the string [ ]
     */
    public abstract String[] getAddressFilter();

    /**
     * 是否正在搜索
     *
     * @return boolean
     */
    public abstract boolean isScaning();

    /**
     * 设置过滤类型
     *
     * @param type 取值{@link BaseScanner#FILTER_TYPE_NAME}             {@link BaseScanner#FILTER_TYPE_ADDRESS}             {@link BaseScanner#FILTER_TYPE_ALL}
     */
    public abstract void setFilterType(int type);

    /**
     * Gets filter type.
     *
     * @return the filter type
     */
    public abstract int getFilterType();
}
