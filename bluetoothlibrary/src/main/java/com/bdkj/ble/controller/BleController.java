package com.bdkj.ble.controller;

import android.annotation.TargetApi;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.bdkj.ble.BuildConfig;
import com.bdkj.ble.connector.BleConnector;
import com.bdkj.ble.connector.ClassicConnector;
import com.bdkj.ble.link.BLEConnectCallBack;
import com.bdkj.ble.link.ConfigInterface;
import com.bdkj.ble.spp.BleSecretary;
import com.bdkj.ble.spp.ClassicSecretary;
import com.bdkj.ble.util.CHexConver;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * BLE蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleController<T extends BleSecretary> extends BluetoothController<BleSecretary> {

    private Context mContext;

    private BleConnector mConnector;

    private Subscription timeoutSubscription;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (BluetoothGatt.STATE_CONNECTED == newState) {
                cancelTimeout();
                if (connectState == STATE_CONNECTING) {
                    if (mCallBack != null) {
                        mCallBack.connectSuccess();
                    }
                    connectState = STATE_CONNECTED;
                }
                else{
                    disconnect();
                    return;
                }
                // 进行服务发现，50ms
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                cancelTimeout();
                if (status == BluetoothGatt.STATE_CONNECTING) {
                    //与蓝牙设备连接时断开，表示连接时并没有成功
                    if (connectState == STATE_CONNECTING) {
                        if (mCallBack != null) {
                            mCallBack.connectFail();
                        }
                    }
                    connectState = STATE_INIT;
                } else if (status == BluetoothGatt.STATE_CONNECTED || status == BluetoothGatt.STATE_DISCONNECTING) {
                    //用户手动断开或设备主动断开的情况下标记Flag并发送断开的通知
                    connectState = STATE_INIT;
                    if (mContext != null) {
                        Intent intent = new Intent(CONNECT_INTERRUPT_ACTION);
                        mContext.sendBroadcast(intent);
                    }
                }
                if (mConnector.getBluetoothGatt() != null) {
                    mConnector.closeGatt();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //输出服务
                List<BluetoothGattService> services = gatt.getServices();
                //调试模式下输出
                if (BuildConfig.DEBUG) {
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
                                }
                            }
                            Log.d("BleController", "└--------------------------------------┙");
                        }
                    }
                }
                if (mCallBack != null && mCallBack instanceof BLEConnectCallBack) {
                    ((BLEConnectCallBack)mCallBack).discoverService(services);
                }
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
    public BleController(Context context,T secretary) {
        mConnector = new BleConnector(context,mGattCallback);
        mSecretary = secretary;
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (device == null) {
            return;
        }
        connectMac = device.getAddress();
        connectState = STATE_CONNECTING;
        isTimeout = false;
        try {
            mConnector.connect(device.getAddress());
            timeoutSubscription = Observable.timer(timeout,TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            cancelConnect();
                            isTimeout = true;
                            if (mCallBack != null) {
                                mCallBack.connectFail();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            if (mCallBack != null) {
                mCallBack.connectFail();
            }
            connectState = STATE_INIT;
        }
    }

    @Override
    public void disconnect() {
        cancelTimeout();
        connectState = STATE_DISCONNECTING;
        if (mSecretary != null) {
            mSecretary.dismiss();
            mSecretary = null;
        }
        connectState = STATE_INIT;
    }

    @Override
    public void cancelConnect() {
        cancelTimeout();
        connectState = STATE_DISCONNECTING;
        mConnector.cancelConnect();
        connectState = STATE_INIT;
    }

    @Override
    public void cancelTimeout() {
        if (timeoutSubscription != null) {
            timeoutSubscription.unsubscribe();
            timeoutSubscription = null;
        }
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
