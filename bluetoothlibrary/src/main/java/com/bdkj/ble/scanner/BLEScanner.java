package com.bdkj.ble.scanner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.os.Handler;
import com.bdkj.ble.constants.ScannerType;

/**
 * Created by weimengmeng on 2016/5/13.
 */
public class BLEScanner extends BaseScanner {
    private Context context;
    /**
     * 手机的蓝牙适配器
     */
    protected BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();

    private Handler mHandler = new Handler();

    /**
     * 蓝牙操作间隔
     */
    private final int BLUETOOTH_SCAN_DELAY = 100;

    @Override
    protected void cancelScan() {

    }

    public BLEScanner(Context context, long timeout) {
        super(timeout);
        this.context = context;
    }

    public BLEScanner(Context context) {
        super(DEFAULT_TIMEOUT);
        this.context = context;
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void startScan() {
        boolean isDiscovering = mBT.isDiscovering();
        if (isDiscovering) {
            mBT.cancelDiscovery();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BLEScanner.super.startScan();
                mBT.startLeScan(mLeScanCallback);
            }
        }, isDiscovering ? BLUETOOTH_SCAN_DELAY : 0);

    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void stopScan() {
        super.stopScan();
        if (mBT.isDiscovering()) {
            mBT.cancelDiscovery();
        }
        mBT.stopLeScan(mLeScanCallback);
    }

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            // 过滤掉不需要的蓝牙设备
            if (device != null && device.getName() != null && scanCallBack != null) {
                ParsedAd parsedAd = parseData(scanRecord);
                switch (getFilterType()) {
                    case FILTER_TYPE_NAME:
                        if (filterDeviceByName(parsedAd.localName)) {
                            scanCallBack.foundSpeificDevice(parsedAd.localName,
                                    device.getAddress(), rssi, ScannerType.BLE);
                        }
                        break;
                    case FILTER_TYPE_ADDRESS:
                        if (filterDeviceByAddress(device.getAddress())) {
                            scanCallBack.foundSpeificDevice(parsedAd.localName,
                                    device.getAddress(), rssi, ScannerType.BLE);
                        }
                        break;
                    case FILTER_TYPE_ALL:
                        if (filterDeviceByName(parsedAd.localName) || filterDeviceByAddress(device.getAddress())) {
                            scanCallBack.foundSpeificDevice(parsedAd.localName,
                                    device.getAddress(), rssi, ScannerType.BLE);
                        }
                        break;
                }
            }
        }
    };

    public static class ParsedAd {
        public int flags;
        public List<UUID> uuids = new ArrayList<UUID>();
        public String localName;
        public short manufacturer;
    }

    /**
     * 通过对ble广播的byte数据进行解释获取localName等信息
     * 参考网址：http://www.tuicool.com/articles/3EZjYvv
     *
     * @param adv_data
     * @return
     */
    public static ParsedAd parseData(byte[] adv_data) {
        ParsedAd parsedAd = new ParsedAd();
        ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(
                ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01: // Flags
                    parsedAd.flags = buffer.get();
                    length--;
                    break;
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                case 0x14: // List of 16-bit Service Solicitation UUIDs
                    while (length >= 2) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb",
                                buffer.getShort())));
                        length -= 2;
                    }
                    break;
                case 0x04: // Partial list of 32 bit service UUIDs
                case 0x05: // Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        parsedAd.uuids
                                .add(UUID.fromString(String.format(
                                        "%08x-0000-1000-8000-00805f9b34fb",
                                        buffer.getInt())));
                        length -= 4;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                case 0x15: // List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        parsedAd.uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;
                case 0x08: // Short local device name
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    parsedAd.localName = new String(sb).trim();
                    break;
                case (byte) 0xFF: // Manufacturer Specific Data
                    parsedAd.manufacturer = buffer.getShort();
                    length -= 2;
                    break;
                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return parsedAd;
    }
}
