package com.bdkj.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import com.bdkj.ble.scanner.filter.BluetoothFilter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 传统蓝牙搜索器
 * Created by weimengmeng on 2016/5/13.
 */
@SuppressWarnings("MissingPermission")
public class ClassicScanner extends BaseScanner {
    private Context context;
    /**
     * 手机的蓝牙适配器
     */
    protected BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    /**
     * 是否正在进行搜索蓝牙监听
     */
    private boolean isFoundListen = false;

    /**
     * 是否监听蓝牙搜索完成
     */
    private boolean isListenerFinish = false;

    private Handler mHandler = new Handler();

    /**
     * Scan for Bluetooth devices. (broadcast listener)
     */
    private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            /* get the search results */
            // ToastUtils.show("设备名称:----");
            final BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 过滤掉不需要的蓝牙设备
            final String name = device.getName();
            final short rssi = intent.getExtras()
                    .getShort(BluetoothDevice.EXTRA_RSSI);
            if (device != null && name != null && scanCallBack != null) {
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext(name);
                        subscriber.onCompleted();
                    }
                }).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String name) {
                        BluetoothFilter filter = getBluetoothFilter();
                        return filter == null || filter.filter(device, name, rssi);
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String name) {
                                scanCallBack.foundSpeificDevice(name,
                                        device.getAddress(), rssi);
                            }
                        });

            }
        }
    };

    /**
     * 蓝牙操作间隔
     */
    private final int BLUETOOTH_SCAN_DELAY = 100;

    /**
     * Bluetooth scanning is finished processing.(broadcast listener)
     */
    private BroadcastReceiver _finshedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEFAULT_TIMEOUT == timeout) {
                if (scanCallBack != null) {
                    scanCallBack.finishScan();
                }
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBT.startDiscovery();
                    }
                }, BLUETOOTH_SCAN_DELAY);
            }
        }
    };

    public ClassicScanner(Context context, long timeout) {
        super(timeout);
        this.context = context;
    }

    public ClassicScanner(Context context) {
        super(DEFAULT_TIMEOUT);
        this.context = context;
    }

    @Override
    protected void cancelScan() {
        mBT.cancelDiscovery();
    }

    @Override
    public void startScan() {
        boolean isDiscovering = mBT.isDiscovering();
        if (isDiscovering) {
            mBT.cancelDiscovery();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ClassicScanner.super.startScan();
                registerFinishReceiver();
                registerFoundReceiver();
                mBT.startDiscovery();// start scan
            }
        }, isDiscovering ? BLUETOOTH_SCAN_DELAY : 0);
    }

    @Override
    public void stopScan() {
        super.stopScan();
        mHandler.removeCallbacksAndMessages(null);
        unregisterFinishReceiver();
        unregisterFoundReceiver();
    }

    /**
     * 注册蓝牙搜索到设备时的通知
     */
    private void registerFoundReceiver() {
        if (!isFoundListen) {
            IntentFilter foundFilter = new IntentFilter(
                    BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(_foundReceiver, foundFilter);
            isFoundListen = true;
        }
    }

    /**
     * 反注册蓝牙搜索到设备时的通知
     */
    private void unregisterFoundReceiver() {
        if (isFoundListen) {
            context.unregisterReceiver(_foundReceiver);
            isFoundListen = false;
        }
    }

    /**
     * 注册蓝牙搜索完成通知
     */
    private void registerFinishReceiver() {
        if (!isListenerFinish) {
            IntentFilter discoveryFilter = new IntentFilter(
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(_finshedReceiver, discoveryFilter);
            isListenerFinish = true;
        }
    }

    /**
     * 反注册草草搜索完成通知
     */
    private void unregisterFinishReceiver() {
        if (isListenerFinish) {
            context.unregisterReceiver(_finshedReceiver);
            isListenerFinish = false;
        }
    }
}
