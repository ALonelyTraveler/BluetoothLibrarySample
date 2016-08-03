package com.bdkj.ble.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 蓝牙工具类
 */
public class BluetoothUtils {
	/**
	 * 蓝牙适配器
	 */
	private static BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

	/**
	 * 蓝牙是否支持
	 *
	 * @return the boolean
	 */
	public static boolean isSupportBT() {
		return bt != null;
	}

	/**
	 * 蓝牙是否启动
	 *
	 * @return boolean
	 */
	public static boolean isEnableBT() {
		return bt!=null&&bt.isEnabled();
	}


	/**
	 * 是否支持BLE蓝牙
	 *
	 * @param context the context
	 * @return boolean boolean
	 */
	public static boolean supportBLE(Context context) {
		if (!isSupportBT()) {
			return false;
		}
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	/**
	 * 启动蓝牙(等待用户确认)
	 *
	 * @param context     the context
	 * @param requestCode the request code
	 */
	public static void requestEnableBluetooth(Context context, int requestCode) {
		Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)context).startActivityForResult(mIntent, requestCode);
	}

	/**
	 * 自动启动蓝牙
	 *
	 * @return the boolean
	 */
	@SuppressWarnings("MissingPermission")
	public static boolean autoEnableBluetooth() {
		return bt != null && bt.enable();
	}

	/**
	 * 关闭蓝牙
	 *
	 * @return the boolean
	 */
	@SuppressWarnings("MissingPermission")
	public static boolean closeBluetooth() {
		return bt != null && bt.disable();
	}

	/**
	 * 主动配对设备
	 *
	 * @param device the device
	 * @return boolean
	 */
	public static boolean pairDevice(BluetoothDevice device) {
		boolean success = false;
		Method createBondMethod = null;
		try {
			createBondMethod = BluetoothDevice.class
					.getMethod("createBond");
			success = (Boolean) createBondMethod.invoke(device);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * 取消配对
	 *
	 * @param btDevice the bt device
	 * @return boolean
	 * @throws Exception the exception
	 */
	public static boolean removeBond(BluetoothDevice btDevice) throws Exception {
		if (btDevice == null) {
			return false;
		}
		Method removeBondMethod = btDevice.getClass().getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 获取设备配对状态
	 *
	 * @param device the device
	 * @return pair status
	 */
	public static int getPairStatus(BluetoothDevice device) {
		return device.getBondState();
	}
}
