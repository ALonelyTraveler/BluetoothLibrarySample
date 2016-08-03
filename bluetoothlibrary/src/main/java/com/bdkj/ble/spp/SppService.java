package com.bdkj.ble.spp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Spp服务 Created by chenwei on 15/10/16.
 */
public class SppService {

	/**
	 * spp连接的uuid
	 */
	public final String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";

	/**
	 * 缓存大小
	 */
	public final int CACHE_SIZE = 1024 * 2;

	/**
	 * The Socket.
	 */
	public BluetoothSocket socket;

	/**
	 * The Input stream.
	 */
	public InputStream inputStream;

	/**
	 * The Output stream.
	 */
	public OutputStream outputStream;

	/**
	 * The Device.
	 */
	public BluetoothDevice device;

	/**
	 * Instantiates a new Spp service.
	 *
	 * @param device the device
	 */
	public SppService(BluetoothDevice device) {
		this.device = device;
	}

	/**
	 * 建立连接
	 *
	 * @return boolean
	 */
	public boolean createConnect() {
		final UUID uuidSPP = UUID.fromString(UUID_SPP);
		boolean success = true;
		// 得到设备连接后，立即创建SPP连接
		try {
			/*
			 * Method m = device.getClass().getMethod( "createRfcommSocket", new
			 * Class[] { int.class }); socket = (BluetoothSocket)
			 * m.invoke(device, 1);//这里端口为1
			 */
			if (Build.VERSION.SDK_INT >= 10)// 2.3.3以上的设备需要用这个方式创建通信连接
				socket = device
						.createInsecureRfcommSocketToServiceRecord(uuidSPP);
			else
				// 创建SPP连接 API level 5
				socket = device.createRfcommSocketToServiceRecord(uuidSPP);
			socket.connect();
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			Log.d("SppService", "SppService-->createConnect-->success");
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			Log.d("SppService", "SppService-->createConnect-->Exception");
			disconnect();
		}
		return success;
	}

	/**
	 * 判断连接
	 */
	public void disconnect() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
			Log.i("断开连接", "断开连接");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读数据
	 *
	 * @return byte [ ]
	 * @throws IOException the io exception
	 */
	public byte[] read() throws IOException {
		if (inputStream != null && socket != null && socket.isConnected()) {
			byte[] buffer = new byte[CACHE_SIZE];
			int len = 0;
			len = inputStream.read(buffer);
			if (len > 0) {
				byte[] data = new byte[len];
				for (int i = 0; i < len; i++) {
					data[i] = buffer[i];
				}
				return data;
			}
		}
		return null;
	}

	/**
	 * 向流中写入数据
	 *
	 * @param data the data
	 * @return boolean
	 * @throws IOException the io exception
	 */
	public boolean write(byte[] data) throws IOException {
		boolean success = false;
		if (outputStream != null && socket != null && socket.isConnected()) {
			outputStream.write(data);
			success = true;
		}
		return success;
	}

	/**
	 * Gets input stream.
	 *
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Sets input stream.
	 *
	 * @param inputStream the input stream
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Gets output stream.
	 *
	 * @return the output stream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Sets output stream.
	 *
	 * @param outputStream the output stream
	 */
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * Gets device.
	 *
	 * @return the device
	 */
	public BluetoothDevice getDevice() {
		return device;
	}

	/**
	 * Sets device.
	 *
	 * @param device the device
	 */
	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	/**
	 * Gets socket.
	 *
	 * @return the socket
	 */
	public BluetoothSocket getSocket() {
		return socket;
	}

	/**
	 * Sets socket.
	 *
	 * @param socket the socket
	 */
	public void setSocket(BluetoothSocket socket) {
		this.socket = socket;
	}

	/**
	 * Gets device mac.
	 *
	 * @return the device mac
	 */
	public String getDeviceMac() {
		if (device != null) {
			return device.getAddress();
		}
		return null;
	}
}
