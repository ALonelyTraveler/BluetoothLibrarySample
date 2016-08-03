package com.bdkj.ble.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import com.bdkj.ble.connector.ClassicConnector;
import com.bdkj.ble.connector.ClassicReadCallBack;
import com.bdkj.ble.link.ConfigInterface;
import com.bdkj.ble.spp.ReadThread;
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
public class ClassicController extends BluetoothController implements ClassicReadCallBack {

    private ClassicConnector mConnector;

    private ReadThread readThread = null;

    private Subscription subscription;

    private Subscription timeoutScription;

    private BluetoothDevice mDevcie;

    public ClassicController(Context context) {
        super(context);


    }

    @Override
    public void connect(BluetoothDevice device) {
        if (device == null) {
            return;
        }
        mDevcie = device;
        setConnectState(STATE_CONNECTING);
        connectMac = device.getAddress();
        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        mConnector = new ClassicConnector(mDevcie);
                        mConnector.connect();
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
                        cancelSubscription();
                        e.printStackTrace();
                        setConnectState(STATE_INIT);
                        if (mCallBack!=null) {
                            mCallBack.connectFail();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        cancelTimeout();
                        cancelSubscription();
                        setConnectState(STATE_CONNECTED);
                        if (mCallBack != null) {
                            mCallBack.connectSuccess();
                        }
                        startLoopRead();
                    }
                });
        timeoutScription = Observable.timer(timeout,TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        timeoutScription.unsubscribe();
                        if (mConnector != null && mConnector.isCancel()) {
                            return;
                        }
                        cancelSubscription();
                        setConnectState(STATE_INIT);
                        if (mCallBack!=null) {
                            mCallBack.connectFail();
                        }
                    }
                });

    }

    @Override
    public void disconnect() {
        synchronized (this) {
            cancelSubscription();
            setConnectState(STATE_DISCONNECTING);
            closeRead();
            if (mConnector != null) {
                try {
                    mConnector.disconnect();
                    mConnector = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setConnectState(STATE_INIT);
        }
    }

    private void cancelSubscription() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    @Override
    public boolean write(byte[] data, ConfigInterface helper) {
        boolean success = false;
        if (mConnector != null && mConnector.getOutputStream() != null) {
            try {
                mConnector.getOutputStream().write(data);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    @Override
    public boolean write(byte[] data) {
        return write(data, null);
    }

    /**
     * 开始循环读
     */
    private void startLoopRead() {
        if (readThread != null) {
            closeRead();
        }
        readThread = new ReadThread(this, mConnector.getInputStream());
        readThread.start();
    }

    /**
     * 关闭循环读
     */
    private void closeRead() {
        if (readThread != null) {
            readThread.cancel();
            readThread = null;
        }
    }

    @Override
    public void dataReceive(byte[] data) {
        dispatchData(data);
    }

    @Override
    public void readBreak(boolean isCancel) {
        if (!isCancel) {
            if (mContext != null) {
                Intent intent = new Intent(CONNECT_INTERRUPT_ACTION);
                mContext.sendBroadcast(intent);
            }
        }
    }

    @Override
    public void cancelTimeout()
    {
        if (timeoutScription != null) {
            timeoutScription.unsubscribe();
            timeoutScription = null;
        }
    }

}
