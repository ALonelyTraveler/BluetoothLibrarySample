package com.bdkj.ble.link;

/**
 * Created by weimengmeng on 2016/5/17.
 */
public interface ConfigInterface {
	public abstract String getServiceUUID();

	public abstract String getCharacteristicUUID();

	public abstract String getNotifyUUID();
}
