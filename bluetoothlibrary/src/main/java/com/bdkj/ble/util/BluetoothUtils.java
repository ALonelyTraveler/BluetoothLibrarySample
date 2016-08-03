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
 * Created by weimengmeng on 2016/5/19. 检查蓝牙是否可用，是否连接
 */
public class BluetoothUtils {
	/**
	 * 蓝牙适配器
	 */
	private static BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

	/**
	 * 蓝牙是否支持
	 */
	public static boolean isSupportBT() {
		return bt != null;
	}

	/**
	 * 蓝牙是否启动
	 * 
	 * @return
	 */
	public static boolean isEnableBT() {
		return bt!=null&&bt.isEnabled();
	}


	/**
	 * 是否支持BLE蓝牙
	 *
	 * @param context the context
	 * @return boolean
	 */
	public static boolean supportBLE(Context context) {
		if (!isSupportBT()) {
			return false;
		}
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	/**
	 * 启动蓝牙(等待用户确认)
	 */
	public static void requestEnableBluetooth(Context context, int requestCode) {
		Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)context).startActivityForResult(mIntent, requestCode);
	}

	/**
	 * 自动启动蓝牙
	 */
	@SuppressWarnings("MissingPermission")
	public static boolean autoEnableBluetooth() {
		return bt != null && bt.enable();
	}

	/**
	 * 关闭蓝牙
	 */
	@SuppressWarnings("MissingPermission")
	public static boolean closeBluetooth() {
		return bt != null && bt.disable();
	}

	/**
	 * 主动配对设备
	 *
	 * @param device
	 * @return
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
	 * @param btDevice
	 * @return
	 * @throws Exception
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
	 * @param device
	 * @return
     */
	public static int getPairStatus(BluetoothDevice device) {
		return device.getBondState();
	}
}
