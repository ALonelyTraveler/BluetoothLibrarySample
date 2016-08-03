package com.bdkj.ble.constants;

/**
 * Created by weimegnmegn on 2016/5/14.
 */
public class ConstansValue {
	private static String ACTION = "com.ble.action";
	/**
	 * 找到蓝牙设备
	 */
	public static String DEVICE_FOUND_ACTION = ACTION + ".found";
	/**
	 * 未找到蓝牙设备
	 */
	public static String DEVICE_FOUND_NO_ACTION = ACTION + ".nofound";
	/**
	 * 蓝牙设备断开连接
	 */
	public static String DISCONNECT_ACTION = ACTION + ".disconnect";
	/**
	 * 蓝牙设备连接成功
	 */
	public static String CONNECT_ACTION = ACTION + ".connect";
	/**
	 * 接收到蓝牙设备返回数据
	 */
	public static String RECEIVEDATA_ACTION = ACTION + ".data.receive";
	/**
	 * 发送到蓝牙数据
	 */
	public static String SENDDATA_ACTION = ACTION + ".data.send";
}
