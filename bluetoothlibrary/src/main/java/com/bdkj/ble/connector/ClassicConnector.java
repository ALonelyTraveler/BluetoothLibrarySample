package com.bdkj.ble.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 传统蓝牙连接器
 * Created by chenwei on 16/5/24.
 */
public class ClassicConnector extends BluetoothConnector {

    /**
     * spp连接的uuid
     */
    public final String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothSocket socket;

    private InputStream inputStream;

    private OutputStream outputStream;

    private boolean isCancel = false;

    @Override
    public void connect(BluetoothDevice device) throws IOException {
        isCancel = false;
        final UUID uuidSPP = UUID.fromString(UUID_SPP);
        if (Build.VERSION.SDK_INT >= 10)// 2.3.3以上的设备需要用这个方式创建通信连接
            socket = device
                    .createInsecureRfcommSocketToServiceRecord(uuidSPP);
        else
            // 创建SPP连接 API level 5
            socket = device.createRfcommSocketToServiceRecord(uuidSPP);
        socket.connect();
        if (isCancel && socket.isConnected()) {
            disconnect();
            return;
        }
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public void disconnect() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        isCancel = true;
    }

    @Override
    public void cancelConnect() {
        while (socket != null && socket.isConnected()) {
            try {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isCancel = true;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public boolean canWrite() {
        return outputStream != null && socket != null && socket.isConnected();
    }
}
