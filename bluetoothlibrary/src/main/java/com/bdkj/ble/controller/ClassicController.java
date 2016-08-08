package com.bdkj.ble.controller;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import com.bdkj.ble.connector.ClassicConnector;
import com.bdkj.ble.event.EventConstants;
import com.bdkj.ble.secretary.ClassicSecretary;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 传统蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
public class ClassicController<T extends ClassicSecretary> extends BluetoothController<ClassicSecretary> implements ClassicSecretary.InterruptedCallback {

    /**
     * 连接器
     */
    private ClassicConnector mConnector;

    /**
     * 当前连接的RxJava处理
     */
    private Subscription subscription;

    /**
     * 当前连接的设备
     */
    private BluetoothDevice mDevice;


    public ClassicController(T secretary) {
        mConnector = new ClassicConnector();
        mSecretary = secretary;
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (device == null) {
            return;
        }
        isCancel = false;
        mDevice = device;
        counter = 0;
        firstConnect = true;
        suspend = false;
        connectState = STATE_CONNECTING;
        connectMac = device.getAddress();
        connect();
    }

    /**
     * 连接
     */
    private void connect() {
        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        Thread.sleep(300);
                        mConnector.connect(mDevice.getAddress());
                        if (mConnector != null && isCancel) {
                            Log.d("ClassicController", "IOException取消连接");
                            mConnector.cancelConnect();
                            return;
                        }
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).delaySubscription(400, TimeUnit.MILLISECONDS);
        subscription = observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mConnector != null && mConnector.isCancel()) {
                            return;
                        }
                        e.printStackTrace();
                        appearDisconnect();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
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
                            mSecretary.setCallback(ClassicController.this);
                        }
                    }
                });
    }

    @Override
    public void disconnect() {
        if (connectState == STATE_INIT || connectState == STATE_DISCONNECTING || isCancel) {
            return;
        }
        isCancel = true;
        cancelSubscription();
        //只有当设备已连接或正在重连的状态才通知用户已断开连接
        boolean isNotifyDisconnected = (connectState == STATE_CONNECTED) || ((!firstConnect) && suspend && connectState == STATE_CONNECTING);
        connectState = STATE_DISCONNECTING;
        try {
            mConnector.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        cancelSubscription();
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
     * 取消连接事件
     */
    private void cancelSubscription() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }


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
            connect();
        }
    }


    @Override
    public void interrupted(Throwable throwable) {
        appearDisconnect();
    }
}
