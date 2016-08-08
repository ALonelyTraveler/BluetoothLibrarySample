package com.bdkj.ble.controller;

import android.annotation.TargetApi;
import android.bluetooth.*;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.bdkj.ble.BluetoothLibrary;
import com.bdkj.ble.connector.BleConnector;
import com.bdkj.ble.event.EventConstants;
import com.bdkj.ble.secretary.BleSecretary;
import com.bdkj.ble.util.BluetoothUtils;
import com.bdkj.ble.util.CHexConver;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * BLE蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleController<T extends BleSecretary> extends BluetoothController<BleSecretary> {

    /**
     * BLE设备连接器
     */
    private BleConnector mConnector;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (isCancel) {
                mConnector.cancelConnect();
                return;
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BluetoothGatt.STATE_CONNECTED == newState) {
                    counter = 0;
                    //如果是初次连接,则通知连接成功和状态改变
                    if (firstConnect) {
                        firstConnect = false;
                        sendConnectAction(EventConstants.SUCCESS);
                        sendStatus(EventConstants.STATE_CONNECTED);
                    }
                    //如果是中途断开,则通知状态改变
                    if (suspend) {
                        suspend = false;
                        sendStatus(EventConstants.STATE_CONNECTED);
                    }
                    connectState = STATE_CONNECTED;
                    if (mSecretary != null) {
                        mSecretary.employ(mConnector);
                    }
                    // 进行服务发现，100ms
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    gatt.discoverServices();
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    appearDisconnect();
                }
            } else {
                appearDisconnect();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d("BleController", "-------0-----");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                if (BluetoothLibrary.isDebug()) {
                    //调试模式下输出
                    if (services != null) {
                        for (BluetoothGattService service : services) {
                            Log.d("BleController", "┌--------------------------------------┑");
                            Log.d("BleController", "|S:" + service.getUuid().toString() + "|");
                            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                            if (characteristics != null) {
                                for (BluetoothGattCharacteristic characteristic : characteristics) {
                                    Log.d("BleController", "|C:" + characteristic.getUuid().toString() + "|");
                                    List<BluetoothGattDescriptor> descriptor = characteristic.getDescriptors();
                                    if (descriptor != null) {
                                        for (BluetoothGattDescriptor bluetoothGattDescriptor : descriptor) {
                                            Log.d("BleController", "|D:" + bluetoothGattDescriptor.getUuid().toString() + "|");
                                        }
                                    }
                                }
                            }
                            Log.d("BleController", "└--------------------------------------┙");
                        }
                    }
                }
                sendService(services);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (BluetoothLibrary.isDebug()) {
                byte[] data = characteristic.getValue();
                Log.d("BleController", "onCharacteristicChanged:" + CHexConver.byte2HexStr(data, data.length));
            }
            if (mSecretary != null) {
                mSecretary.onCharacteristicChanged(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (BluetoothLibrary.isDebug() && status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                Log.d("BleController", "onCharacteristicWrite:" + CHexConver.byte2HexStr(data, data.length));
            }
            if (mSecretary != null) {
                mSecretary.onCharacteristicWrite(characteristic, status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (BluetoothLibrary.isDebug() && status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                Log.d("BleController", "onCharacteristicRead:" + CHexConver.byte2HexStr(data, data.length));
            }
            if (mSecretary != null) {
                mSecretary.onCharacteristicRead(characteristic, status);
            }
        }
    };

    /**
     * 出现断开或错误的时候
     */
    private void appearDisconnect() {
        //如果是初次连接,则不记录停止的状态
        if (!firstConnect) {
            suspend = true;
        }
        if ((!isRetry()) || counter >= maxReconnectCount) {
            connectState = STATE_INIT;
            if (firstConnect) {
                sendConnectAction(EventConstants.FAIL);
            } else {
                sendStatus(EventConstants.STATE_DISCONNECTED);
                if (mSecretary != null) {
                    mSecretary.dismiss();
                }
            }
            isCancel = true;
            connectState = STATE_DISCONNECTING;
            mConnector.cancelConnect();
            connectState = STATE_INIT;
        } else {
            counter++;
            connectState = STATE_CONNECTING;
            if (isCancel) {
                return;
            }
            Observable.timer(600, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            try {
                                reconnect(mConnector.getBluetoothGatt());
                            } catch (IOException e) {
                                e.printStackTrace();
                                if (firstConnect) {
                                    sendConnectAction(EventConstants.FAIL);
                                } else {
                                    sendStatus(EventConstants.STATE_DISCONNECTED);
                                    if (mSecretary != null) {
                                        mSecretary.dismiss();
                                    }
                                }
                                isCancel = true;
                                connectState = STATE_DISCONNECTING;
                                mConnector.cancelConnect();
                                connectState = STATE_INIT;
                            }
                        }
                    });
        }
    }

    public BleController(Context context, T secretary) {
        mConnector = new BleConnector(context, mGattCallback);
        mSecretary = secretary;
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (device == null || !BluetoothUtils.isEnableBT()) {
            return;
        }
        counter = 0;
        isCancel = false;
        firstConnect = true;
        suspend = false;
        connectMac = device.getAddress();
        connectState = STATE_CONNECTING;
        try {
            mConnector.connect(connectMac);
        } catch (IOException e) {
            e.printStackTrace();
            isCancel = true;
            connectState = STATE_DISCONNECTING;
            mConnector.disconnect();
            connectState = STATE_INIT;
            sendConnectAction(EventConstants.FAIL);
        }
    }

    @Override
    public void disconnect() {
        if (connectState == STATE_INIT || connectState == STATE_DISCONNECTING || isCancel) {
            return;
        }
        isCancel = true;
        //只有当设备已连接或正在重连的状态才通知用户已断开连接
        boolean isNotifyDisconnected = (connectState == STATE_CONNECTED) || ((!firstConnect) && suspend && connectState == STATE_CONNECTING);
        connectState = STATE_DISCONNECTING;
        mConnector.disconnect();
        if (isNotifyDisconnected) {
            sendStatus(EventConstants.STATE_DISCONNECTED);
            if (mSecretary != null) {
                mSecretary.dismiss();
            }
        }
        connectState = STATE_INIT;
    }

    @Override
    public void cancelConnect() {
        if (connectState == STATE_INIT || connectState == STATE_DISCONNECTING || isCancel) {
            return;
        }
        isCancel = true;
        //只有当设备已连接或正在重连的状态才通知用户已断开连接
        boolean isNotifyDisconnected = (connectState == STATE_CONNECTED) || ((!firstConnect) && suspend && connectState == STATE_CONNECTING);
        connectState = STATE_DISCONNECTING;
        mConnector.cancelConnect();
        if (isNotifyDisconnected) {
            sendStatus(EventConstants.STATE_DISCONNECTED);
            if (mSecretary != null) {
                mSecretary.dismiss();
            }
        }
        connectState = STATE_INIT;
    }

    /**
     * 此方法是我自行使用众多三星手机总结出来，不一定很准确
     *
     * @return
     */
    private boolean checkIsSamsung() {
        String brand = android.os.Build.BRAND;
        Log.e("", " brand:" + brand);
        if (brand.toLowerCase().equals("samsung")) {
            return true;
        }
        return false;
    }

    /**
     * 重连
     *
     * @return
     */
    private void reconnect(BluetoothGatt gatt) throws IOException {
        Log.d("BleController", "重连吧!!!!!!!!!");
        connectState = STATE_CONNECTING;
        if (checkIsSamsung()) {
            gatt.connect();
        } else {
            mConnector.connect(connectMac);
        }
    }


}
