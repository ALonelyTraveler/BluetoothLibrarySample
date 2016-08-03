package com.bdkj.ble.scanner;

import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by weimengmeng on 2016/5/13.
 */
public abstract class BaseScanner implements Scanner {

    /**
     * 默认超时时间
     * 1.传统蓝牙一般是12秒
     * 2.BLE蓝牙会一直进行扫描
     */
    public static final long DEFAULT_TIMEOUT = -1;

    /**
     * 搜索的超时时间
     */
    protected long timeout = DEFAULT_TIMEOUT;
    /**
     * 允许通过的设备名称数组
     * null允许搜索所有设备
     */
    private String[] mNameFilters;

    /**
     * 允许通过的设备地址数组
     * null允许搜索所有设备
     */
    private String[] mAddressFilters;

    /**
     * 搜索过程回调
     */
    protected ScanCallBack scanCallBack;
    private Timer timer;
    private ScannerTask mScannerTask;
    /**
     * 是否完成搜索
     */
    private boolean isFinish = false;

    /**
     * 按名称过滤
     */
    public final static int FILTER_TYPE_NAME = 0;

    /**
     * 按地址过滤
     */
    public final static int FILTER_TYPE_ADDRESS = 1;

    /**
     * 按名称和地址过滤(并集)
     */
    public final static int FILTER_TYPE_ALL = 2;

    /**
     * 过滤方式
     */
    private int filterType = FILTER_TYPE_NAME;

    /**
     * 取消搜索
     * 只供子类进行实现和访问
     */
    protected abstract void cancelScan();

    /**
     * 开始搜索计时
     */
    private void startTimer() {
        if (timeout != DEFAULT_TIMEOUT) {
            mScannerTask = new ScannerTask();
            timer = new Timer();
            timer.schedule(mScannerTask, timeout);
        }
    }

    public BaseScanner(long timeout) {
        this.timeout = timeout;
    }

    /**
     * 开始搜索
     */
    @Override
    public void startScan() {
        if (scanCallBack != null) {
            scanCallBack.startScan();
        }
        isFinish = false;
        startTimer();
    }

    /**
     * 是否正在搜索
     */
    @Override
    public boolean isScaning() {
        return !isFinish;
    }

    @Override
    public void setCallBack(ScanCallBack callBack) {
        this.scanCallBack = callBack;
    }

    @Override
    public ScanCallBack getCallBack() {
        return scanCallBack;
    }

    @Override
    public void stopScan() {
        cancelScan();
        isFinish = true;
        if (mScannerTask != null) {
            mScannerTask.cancel();
            mScannerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void setNameFilter(String... names) {
        mNameFilters = names;
    }

    @Override
    public String[] getNameFilter() {
        return mNameFilters;
    }

    class ScannerTask extends TimerTask {

        @Override
        public void run() {
            stopScan();
            if (scanCallBack != null) {
                scanCallBack.finishScan();
            }
        }
    }

    /**
     * 按名称过滤设备
     *
     * @param name the name
     * @return boolean
     */
    public boolean filterDeviceByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            if (mNameFilters == null || mNameFilters.length <= 0 || mNameFilters[0] == null) {
                return true;
            }
            for (String mFilter : mNameFilters) {
                if (name.startsWith(mFilter)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 按地址过滤设备
     *
     * @param address the address
     * @return boolean
     */
    public boolean filterDeviceByAddress(String address) {
        if (!TextUtils.isEmpty(address)) {
            if (mAddressFilters == null || mAddressFilters.length <= 0 || mAddressFilters[0] == null) {
                return true;
            }
            for (String mFilter : mAddressFilters) {
                if (address.equalsIgnoreCase(mFilter)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void setAddressFilter(String... address) {
        mAddressFilters = address;
    }

    @Override
    public String[] getAddressFilter() {
        return mAddressFilters;
    }


    @Override
    public void setFilterType(int type) {
        filterType = type;
    }

    @Override
    public int getFilterType() {
        return filterType;
    }
}
