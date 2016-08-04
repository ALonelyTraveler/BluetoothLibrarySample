package com.bdkj.ble.scanner;

import android.util.Log;
import com.bdkj.ble.scanner.filter.BluetoothFilter;
import com.bdkj.ble.scanner.filter.DefaultFilter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * 基本扫描器
 * @author: chenwei
 * @version: V1.0
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
     * 搜索过程回调
     */
    protected ScanCallBack scanCallBack;

    /**
     * RxJava超时处理
     */
    private Subscription timeoutSubscriptions;

    /**
     * 是否完成搜索
     */
    private boolean isFinish = false;

    /**
     * 默认过滤器
     */
    private BluetoothFilter mFilter = new DefaultFilter();

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
            Log.d("BaseScanner", "startTimer()--timeout:" + timeout);
            timeoutSubscriptions = Observable.timer(timeout, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            stopScan();
                            if (scanCallBack != null) {
                                scanCallBack.finishScan();
                            }
                        }
                    });
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
    public boolean isScanning() {
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
        if (timeoutSubscriptions != null) {
            timeoutSubscriptions.unsubscribe();
            timeoutSubscriptions = null;
        }
    }

    @Override
    public void setBluetoothFilter(BluetoothFilter filter) {
        this.mFilter = filter;
    }

    @Override
    public BluetoothFilter getBluetoothFilter() {
        return mFilter;
    }
}
