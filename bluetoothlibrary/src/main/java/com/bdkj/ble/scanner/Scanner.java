package com.bdkj.ble.scanner;

/**
 * Created by weimengmeng on 2016/5/13.
 */
public interface Scanner {
    public abstract void startScan();

    /**
     * 设置搜索回调
     *
     * @param callBack
     */
    public abstract void setCallBack(ScanCallBack callBack);

    public abstract ScanCallBack getCallBack();

    public abstract void stopScan();

    /**
     * 设置筛选条件
     *
     * @param names
     */
    public abstract void setNameFilter(String... names);

    public abstract void setAddressFilter(String... address);

    public abstract String[] getNameFilter();

    public abstract String[] getAddressFilter();

    /**
     * 是否正在搜索
     *
     * @return
     */
    public abstract boolean isScaning();

    /**
     * 设置过滤类型
     *
     * @param type 取值{@link BaseScanner#FILTER_TYPE_NAME}
     *             {@link BaseScanner#FILTER_TYPE_ADDRESS}
     *             {@link BaseScanner#FILTER_TYPE_ALL}
     */

    public abstract void setFilterType(int type);

    public abstract int getFilterType();
}
