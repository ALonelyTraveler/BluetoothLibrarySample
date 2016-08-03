package com.bdkj.ble.spp;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bdkj.ble.connector.ClassicReadCallBack;
import com.bdkj.ble.constants.ConstansValue;
import com.bdkj.ble.link.ConnectCallBack;

/**
 * 读取线程 Created by chenwei on 15/10/16.
 */
public class ReadThread extends BaseThread {
    private ClassicReadCallBack handler;
    private boolean isCancel = false;
    /**
     * 缓存区大小
     */
    private final int BUFFER_SIZE = 1024;

    private InputStream inputStream;

    public ReadThread(ClassicReadCallBack handler, InputStream inputStream) {
        this.inputStream = inputStream;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        try {
            while ((!isCancel) && (len = inputStream.read(buffer)) != -1) {
                byte[] data = new byte[len];
                for (int i = 0; i < len; i++) {
                    data[i] = buffer[i];
                }
                Thread.sleep(100);
                if (!isCancel) {
                    if (handler != null) {
                        handler.dataReceive(data);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.readBreak(isCancel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (handler != null) {
                handler.readBreak(isCancel);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {
        isCancel = true;
        interrupt();
    }

    @Override
    public boolean isCancel() {
        return isCancel;
    }
}
