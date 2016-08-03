package com.bdkj.ble.spp;

/**
 * 蓝牙通信基本线程类 Created by chenwei on 15/10/30.
 */
public abstract class BaseThread extends Thread {

	public abstract void cancel();

	public abstract boolean isCancel();

}
