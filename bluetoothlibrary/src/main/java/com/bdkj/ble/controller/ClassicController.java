package com.bdkj.ble.controller;

import android.bluetooth.BluetoothDevice;
import com.bdkj.ble.connector.ClassicConnector;
import com.bdkj.ble.spp.ClassicSecretary;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 传统蓝牙控制器
 * Created by chenwei on 16/5/24.
 */
public class ClassicController<T extends ClassicSecretary> extends BluetoothController<ClassicSecretary> {

    private ClassicConnector mConnector;

    private Subscription subscription;

    private Subscription timeoutSubscription;

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
        mDevice = device;
        connectState = STATE_CONNECTING;
        connectMac = device.getAddress();
        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        mConnector.connect(mDevice.getAddress());
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
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
                        cancelTimeout();
                        if (mConnector != null && mConnector.isCancel()) {
                            return;
                        }
                        e.printStackTrace();
                        connectState = STATE_INIT;
                        if (mCallBack != null) {
                            mCallBack.connectFail();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        cancelTimeout();
                        connectState = STATE_CONNECTED;
                        if (mCallBack != null) {
                            mCallBack.connectSuccess();
                        }
                        if (mSecretary != null) {
                            mSecretary.dismiss();
                        }
                        else{
                            mSecretary = new ClassicSecretary();
                        }
                        mSecretary.employ(mConnector);
                    }
                });
        timeoutSubscription = Observable.timer(timeout, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        cancelConnect();
                        if (mCallBack != null) {
                            mCallBack.connectFail();
                        }
                    }
                });

    }

    @Override
    public void disconnect() {
        cancelSubscription();
        cancelTimeout();
        connectState = STATE_DISCONNECTING;
        if (mSecretary != null) {
            mSecretary.dismiss();
        }
        connectState = STATE_INIT;
    }

    @Override
    public void cancelConnect() {
        cancelSubscription();
        cancelTimeout();
        connectState = STATE_DISCONNECTING;
        mConnector.cancelConnect();
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
     * 取消超时事件
     */
    @Override
    public void cancelTimeout() {
        if (timeoutSubscription != null) {
            timeoutSubscription.unsubscribe();
            timeoutSubscription = null;
        }
    }

}
