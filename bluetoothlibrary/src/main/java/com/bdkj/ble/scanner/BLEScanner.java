package com.bdkj.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import com.bdkj.ble.scanner.filter.BluetoothFilter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  BLE蓝牙搜索器,前提是手机支持低功耗
 *  支持的API大于等于18 (4.3)
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEScanner extends BaseScanner {
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

    /**
     * Instantiates a new Ble scanner.
     *
     * @param timeout the timeout
     */
    public BLEScanner(long timeout) {
        super(timeout);
    }

    /**
     * Instantiates a new Ble scanner.
     *
     */
    public BLEScanner() {
        super(DEFAULT_TIMEOUT);
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

    /**
     * 搜索到蓝牙设备时回调接口
     */
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            //在该方法中尽量少做事情
            if (scanCallBack != null && device != null) {
                //过滤并将线程切换到主线程
                Observable.create(new Observable.OnSubscribe<ParsedAd>()
                {
                    @Override
                    public void call(Subscriber<? super ParsedAd> subscriber) {
                        ParsedAd result = parseData(scanRecord);
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                }).filter(new Func1<ParsedAd, Boolean>() {
                    @Override
                    public Boolean call(ParsedAd parsedAd) {
                        BluetoothFilter filter = getBluetoothFilter();
                        return filter == null || filter.filter(device, parsedAd.localName, rssi);
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ParsedAd>() {
                            @Override
                            public void call(ParsedAd parsedAd) {
                                scanCallBack.foundSpeificDevice(parsedAd.localName,
                                        device.getAddress(), rssi);
                            }
                        });
            }
        }
    };

    /**
     * The type Parsed ad.
     * 解析canRecord信息类
     */
    public static class ParsedAd {
        /**
         * The Flags.
         */
        public int flags;
        /**
         * The Uuids.
         */
        public List<UUID> uuids = new ArrayList<UUID>();
        /**
         * The Local name.
         */
        public String localName;
        /**
         * The Manufacturer.
         */
        public short manufacturer;
    }

    /**
     * 通过对ble广播的byte数据进行解释获取localName等信息
     * 参考网址：http://www.tuicool.com/articles/3EZjYvv
     *
     * @param adv_data the adv data
     * @return parsed ad
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
