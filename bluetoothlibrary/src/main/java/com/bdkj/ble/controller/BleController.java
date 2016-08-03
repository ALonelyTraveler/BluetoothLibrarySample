package com.bdkj.ble.controller;

import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.bdkj.ble.link.ConfigInterface;
import com.bdkj.ble.util.CHexConver;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * BLE蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
public class BleController extends BluetoothController {

    private BluetoothGatt bluetoothGatt;

    private ConfigInterface mDefaultHelper;

    private boolean isCancel;

    private Subscription timeoutScription;

    private boolean isTimeout = false;

    public static final long BLE_DEFAULT_TIMEOUT = 8000;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (BluetoothGatt.STATE_CONNECTED == newState) {
                if (isTimeout || isCancel) {
                    disconnect();
                    return;
                }
                gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                cancelTimeout();
                if (status == BluetoothGatt.STATE_CONNECTING) {
                    //与蓝牙设备连接时断开，表示连接时并没有成功
                    setConnectState(STATE_INIT);
                    if (mCallBack != null && !isTimeout) {
                        mCallBack.connectFail();
                    }
                } else if (status == BluetoothGatt.STATE_CONNECTED || status == BluetoothGatt.STATE_DISCONNECTING) {
                    //用户手动断开或设备主动断开的情况下标记Flag并发送断开的通知
                    setConnectState(STATE_INIT);
                    if (mContext != null && isCancel) {
                        Intent intent = new Intent(CONNECT_INTERRUPT_ACTION);
                        mContext.sendBroadcast(intent);
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //输出服务
                List<BluetoothGattService> services = gatt.getServices();
                if (services != null) {
                    for (BluetoothGattService service : services) {
                        Log.d("BleController", "┌--------------------------------------┑");
                        Log.d("BleController", "|S:" + service.getUuid().toString()+"|");
                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        if (characteristics != null) {
                            for (BluetoothGattCharacteristic characteristic : characteristics) {
                                Log.d("BleController", "|C:" + characteristic.getUuid().toString()+"|");
                                List<BluetoothGattDescriptor> descriptor = characteristic.getDescriptors();
                                if (descriptor != null) {
                                    for (BluetoothGattDescriptor bluetoothGattDescriptor : descriptor) {
                                        Log.d("BleController", "|D:" + bluetoothGattDescriptor.getUuid().toString()+"|");
                                    }
                                }
                                if (mDefaultHelper != null&&characteristic.getUuid().toString().equalsIgnoreCase(mDefaultHelper.getNotifyUUID())) {
                                    setCharacteristicNotification(characteristic, true);
                                }
                            }
                        }
                        Log.d("BleController", "└--------------------------------------┙");
                    }
                }
                setConnectState(STATE_CONNECTED);
                if (mCallBack != null) {
                    mCallBack.connectSuccess();
                }
                cancelTimeout();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (mContext != null) {
                dispatchData(characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                Log.d("BleController", "数据发送成功:" + CHexConver.byte2HexStr(data,data.length));
            }
        }
    };

    public BleController(Context mContext) {
        super(mContext);
        setTimeout(BLE_DEFAULT_TIMEOUT);
    }

    public BleController(Context mContext, ConfigInterface helper) {
        super(mContext);
        this.mDefaultHelper = helper;
        setTimeout(BLE_DEFAULT_TIMEOUT);
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (device == null) {
            return;
        }
        connectMac = device.getAddress();
        setConnectState(STATE_CONNECTING);
        isCancel = false;
        isTimeout = false;
        bluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        timeoutScription = Observable.timer(timeout,TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        timeoutScription.unsubscribe();
                        cancelTimeout();
                        isTimeout = true;
                        if (mCallBack != null && getConnectState() != STATE_CONNECTED) {
                            disconnect();
                            mCallBack.connectFail();
                        }
                    }
                });

    }

    @Override
    public void disconnect() {
        cancelTimeout();
        isCancel = true;
        if (bluetoothGatt != null) {
            setConnectState(STATE_DISCONNECTING);
            bluetoothGatt.disconnect();
            bluetoothGatt = null;
            setConnectState(STATE_INIT);
        }
    }

    @Override
    public boolean write(byte[] data, ConfigInterface helper) {
        if (bluetoothGatt == null || getConnectState() != STATE_CONNECTED) {
            return false;
        }
        BluetoothGattService gattService = bluetoothGatt.getService(UUID.fromString(helper
                .getServiceUUID()));
        if (gattService == null) {
            return false;
        }
        BluetoothGattCharacteristic writeCharacter = gattService.getCharacteristic(UUID.fromString(helper
                .getCharacteristicUUID()));
        if (writeCharacter == null) {
            return false;
        }
        writeCharacter.setValue(data);
        boolean success = bluetoothGatt.writeCharacteristic(writeCharacter);
        final BluetoothGattCharacteristic notifyCharacter = gattService.getCharacteristic(UUID.fromString(helper
                .getNotifyUUID()));
//        try {
//            Thread.sleep(600);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        setCharacteristicNotification(notifyCharacter, true);
        return success;

    }

    @Override
    public boolean write(byte[] data) {
        return write(data, mDefaultHelper);
    }

    @Override
    public void cancelTimeout() {

    }

    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        List<BluetoothGattDescriptor> descriptors = characteristic
                .getDescriptors();
        for (BluetoothGattDescriptor dp : descriptors) {
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(dp);
        }
    }

}
