package com.bdkj.ble.scanner.filter;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名称过滤器
 *
 * @author: chenwei
 * @version: V1.0
 */
public class NameMatcher implements BluetoothFilter {
    private Pattern mPattern;

    public NameMatcher(String regexp) {
        if (regexp == null) {
            throw new NullPointerException("Params of construction is null");
        }
        mPattern = Pattern.compile(regexp);
    }

    @Override
    public boolean filter(BluetoothDevice device, String localName, int rssi) {
        if (device == null || TextUtils.isEmpty(localName)) {
            return false;
        }
        Matcher m = mPattern.matcher(localName);
        return m.matches();
    }
}
