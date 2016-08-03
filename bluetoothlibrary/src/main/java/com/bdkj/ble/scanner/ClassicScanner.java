package com.bdkj.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.bdkj.ble.constants.ScannerType;

/**
 * 传统蓝牙搜索器
 * Created by weimengmeng on 2016/5/13.
 */
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
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 过滤掉不需要的蓝牙设备
            String name = device.getName();
            short rssi = intent.getExtras()
                    .getShort(BluetoothDevice.EXTRA_RSSI);
            if (device != null && name != null&&scanCallBack!=null) {
                switch (getFilterType()) {
                    case FILTER_TYPE_NAME:
                        if (filterDeviceByName(name)) {
                            scanCallBack.foundSpeificDevice(name,
                                    device.getAddress(), rssi, ScannerType.CLASSIC);
                        }
                        break;
                    case FILTER_TYPE_ADDRESS:
                        if (filterDeviceByAddress(device.getAddress())) {
                            scanCallBack.foundSpeificDevice(name,
                                    device.getAddress(), rssi, ScannerType.CLASSIC);
                        }
                        break;
                    case FILTER_TYPE_ALL:
                        if (filterDeviceByName(name) || filterDeviceByAddress(device.getAddress())) {
                            scanCallBack.foundSpeificDevice(name,
                                    device.getAddress(), rssi, ScannerType.CLASSIC);
                        }
                        break;
                }
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
        }, isDiscovering?BLUETOOTH_SCAN_DELAY:0);
    }

    @Override
    public void stopScan() {
        super.stopScan();
        mHandler.removeCallbacksAndMessages(null);
        unregisterFinishReceiver();
        unregisterFoundReceiver();
    }

    private void registerFoundReceiver() {
        if (!isFoundListen) {
            IntentFilter foundFilter = new IntentFilter(
                    BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(_foundReceiver, foundFilter);
            isFoundListen = true;
        }
    }

    private void unregisterFoundReceiver() {
        if (isFoundListen) {
            context.unregisterReceiver(_foundReceiver);
            isFoundListen = false;
        }
    }

    private void registerFinishReceiver() {
        if (!isListenerFinish) {
            IntentFilter discoveryFilter = new IntentFilter(
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(_finshedReceiver, discoveryFilter);
            isListenerFinish = true;
        }
    }

    private void unregisterFinishReceiver() {
        if (isListenerFinish) {
            context.unregisterReceiver(_finshedReceiver);
            isListenerFinish = false;
        }
    }
}
