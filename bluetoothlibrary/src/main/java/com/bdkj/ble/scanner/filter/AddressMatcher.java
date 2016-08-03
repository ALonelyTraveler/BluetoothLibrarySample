package com.bdkj.ble.scanner.filter;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 地址过滤器
 * @author: chenwei
 * @version: V1.0
 */
public class AddressMatcher implements BluetoothFilter {
    private Pattern mPattern;

    public AddressMatcher(String address) {
        if (address == null) {
            throw new NullPointerException("Params of construction is null");
        }
        mPattern = Pattern.compile(address);
    }

    @Override
    public boolean filter(BluetoothDevice device, String localName, int rssi) {
        if (device == null || TextUtils.isEmpty(device.getAddress())) {
            return false;
        }
        Matcher m = mPattern.matcher(device.getAddress());
        return m.matches();
    }
}
