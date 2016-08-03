package com.bdkj.ble.link;

/**
 * Created by weimengmeng on 2016/5/17.
 */
public class WriteHelper implements ConfigInterface {
	String service = "";
	String characteristic = "";
	String notify = "";

	public WriteHelper(String serviceUUID, String characteristicUUID,
			String notifyUUID) {
		this.service = serviceUUID;
		this.characteristic = characteristicUUID;
		this.notify = notifyUUID;
	}

	@Override
	public String getServiceUUID() {
		return service;
	}

	@Override
	public String getCharacteristicUUID() {
		return characteristic;
	}

	@Override
	public String getNotifyUUID() {
		return notify;
	}
}
