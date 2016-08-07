package com.bdkj.ble.secretary;

import com.bdkj.ble.connector.BluetoothConnector;
import com.bdkj.ble.connector.ClassicConnector;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;

/**
 * 传统蓝牙秘书
 * @author: chenwei
 * @version: V1.0
 */
public abstract class ClassicSecretary implements BluetoothSecretary {
    private ClassicConnector mConnector;
    /**
     * 缓存区大小
     */
    private final int BUFFER_SIZE = 1024;
    /**
     * 是否取消
     */
    private boolean isCancel;

    Subscription readSubscription;

    @Override
    public void employ(BluetoothConnector mConnector) {
        if (mConnector == null || !(mConnector instanceof ClassicConnector)) {
            throw new RuntimeException("mConnector参数必须是ClassicConnector类或其子类的对象");
        }
        this.mConnector = (ClassicConnector) mConnector;
        isCancel = false;
        Observable<byte[]> observable = Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len = 0;
                    try {
                        while ((!isCancel) && (len = ClassicSecretary.this.mConnector.getInputStream().read(buffer)) != -1) {
                            byte[] data = new byte[len];
                            for (int i = 0; i < len; i++) {
                                data[i] = buffer[i];
                            }
                            Thread.sleep(100);
                            if (!isCancel) {
                                subscriber.onNext(data);
                            }
                        }
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            }
        });
        readSubscription = observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<byte[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!isCancel) {
                            if (mCallback != null) {
                                mCallback.interrupted(e);
                            }
                        }
                    }

                    @Override
                    public void onNext(byte[] data) {
                        receiveData(data);
                    }
                });
    }

    @Override
    public void dismiss() {
        isCancel = true;
        mCallback = null;
        if (readSubscription != null) {
            readSubscription.unsubscribe();
            readSubscription = null;
        }
        mConnector = null;
    }

    /**
     * 向流中写入数据
     *
     * @param data the data
     * @return boolean boolean
     */
    public boolean write(byte[] data) {
        boolean success = false;
        if (mConnector != null && mConnector.canWrite()) {
            try {
                mConnector.getOutputStream().write(data);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * 接收到数据
     *
     * @param data the data
     */
    public abstract void receiveData(byte[] data);

    /**
     * 连接中途断开回调
     */
    public interface InterruptedCallback
    {
        public void interrupted(Throwable throwable);
    }

    private InterruptedCallback mCallback;

    public void setCallback(InterruptedCallback callback) {
        this.mCallback = callback;
    }
}
