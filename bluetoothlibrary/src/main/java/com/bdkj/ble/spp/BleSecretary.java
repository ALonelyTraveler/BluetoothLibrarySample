package com.bdkj.ble.spp;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.text.TextUtils;
import com.bdkj.ble.connector.BleConnector;
import com.bdkj.ble.connector.BluetoothConnector;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName: BleSecretary
 * @Description: 低功耗蓝牙秘书
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/3 下午4:21
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleSecretary implements BluetoothSecretary {
    private BleConnector mConnector;

    @Override
    public void employ(BluetoothConnector mConnector) {
        if (!(mConnector instanceof BleConnector)) {
            throw new RuntimeException("mConnector参数必须是BleConnect类或其子类的对象");
        }
        this.mConnector = (BleConnector) mConnector;
    }

    @Override
    public void dismiss() {
        mConnector = null;
    }

    /**
     * ===================================
     *      客户端主动调用这些方法进行交互
     * ===================================
     */

    /**
     * Read characteristic boolean.
     *
     * @param serviceuuid the serviceuuid
     * @param charuuid    the charuuid
     * @return the boolean
     */
    public boolean readCharacteristic(String serviceuuid, String charuuid) {
        if (TextUtils.isEmpty(serviceuuid) || TextUtils.isEmpty(charuuid)) {
            return false;
        }
        return readCharacteristic(UUID.fromString(serviceuuid), UUID.fromString(charuuid));
    }

    /**
     * Read characteristic boolean.
     *
     * @param serviceuuid the serviceuuid
     * @param charuuid    the charuuid
     * @return the boolean
     */
    public boolean readCharacteristic(UUID serviceuuid, UUID charuuid) {
        if (mConnector != null && mConnector.getBluetoothGatt() != null) {
            BluetoothGatt gatt = mConnector.getBluetoothGatt();
            BluetoothGattService service = gatt.getService(serviceuuid);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(charuuid);
                if (characteristic != null) {
                    return gatt.readCharacteristic(characteristic);
                }
            }
        }
        return false;
    }

    /**
     * Read characteristic boolean.
     *
     * @param characteristic the characteristic
     * @return the boolean
     */
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null && mConnector != null && mConnector.getBluetoothGatt() != null) {
            return mConnector.getBluetoothGatt().readCharacteristic(characteristic);
        }
        return false;
    }

    /**
     * Write charcteristic boolean.
     *
     * @param serviceuuid the serviceuuid
     * @param charuuid    the charuuid
     * @param data        the data
     * @return the boolean
     */
    public boolean writeCharcteristic(String serviceuuid, String charuuid, byte[] data) {
        if (TextUtils.isEmpty(serviceuuid) || TextUtils.isEmpty(charuuid) || data == null) {
            return false;
        }
        return writeCharcteristic(UUID.fromString(serviceuuid), UUID.fromString(charuuid), data);
    }

    /**
     * Write charcteristic boolean.
     *
     * @param serviceuuid the serviceuuid
     * @param charuuid    the charuuid
     * @param data        the data
     * @return the boolean
     */
    public boolean writeCharcteristic(UUID serviceuuid, UUID charuuid, byte[] data) {
        if (mConnector != null && mConnector.getBluetoothGatt() != null) {
            BluetoothGatt gatt = mConnector.getBluetoothGatt();
            BluetoothGattService service = gatt.getService(serviceuuid);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(charuuid);
                if (characteristic != null) {
                    characteristic.setValue(data);
                    return gatt.writeCharacteristic(characteristic);
                }
            }
        }
        return false;
    }

    /**
     * Read characteristic boolean.
     *
     * @param characteristic the characteristic
     * @param data           the data
     * @return the boolean
     */
    public boolean writeCharcteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (characteristic != null && data != null && mConnector != null && mConnector.getBluetoothGatt() != null) {
            characteristic.setValue(data);
            return mConnector.getBluetoothGatt().writeCharacteristic(characteristic);
        }
        return false;
    }

    /**
     * Write charcteristic boolean.
     *
     * @param characteristic the characteristic
     * @return the boolean
     */
    public boolean writeCharcteristic(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null && mConnector != null && mConnector.getBluetoothGatt() != null) {
            return mConnector.getBluetoothGatt().writeCharacteristic(characteristic);
        }
        return false;
    }

    /**
     * Sets characteristic notification.
     *
     * @param characteristic the characteristic
     * @param enabled        the enabled
     * @return the characteristic notification
     */
    public boolean setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (characteristic != null && mConnector != null && mConnector.getBluetoothGatt() != null) {
            BluetoothGatt gatt = mConnector.getBluetoothGatt();
            gatt.setCharacteristicNotification(characteristic, enabled);
            List<BluetoothGattDescriptor> descriptors = characteristic
                    .getDescriptors();
            for (BluetoothGattDescriptor dp : descriptors) {
                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(dp);
            }
            return true;
        }
        return false;
    }

    /**
     * Sets characteristic notification.
     *
     * @param serviceuuid the serviceuuid
     * @param charuuid    the charuuid
     * @param enabled     the enabled
     * @return the characteristic notification
     */
    public boolean setCharacteristicNotification(
            UUID serviceuuid, UUID charuuid, boolean enabled) {
        if (serviceuuid != null && charuuid != null && mConnector != null && mConnector.getBluetoothGatt() != null) {
            BluetoothGatt gatt = mConnector.getBluetoothGatt();
            BluetoothGattService service = gatt.getService(serviceuuid);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(charuuid);
                if (characteristic != null) {
                    return setCharacteristicNotification(characteristic, enabled);
                }
            }
        }
        return false;
    }

    /**
     * Sets characteristic notification.
     *
     * @param serviceuuid the serviceuuid
     * @param charuuid    the charuuid
     * @param enabled     the enabled
     * @return the characteristic notification
     */
    public boolean setCharacteristicNotification(String serviceuuid, String charuuid, boolean enabled) {
        if (TextUtils.isEmpty(serviceuuid) || TextUtils.isEmpty(charuuid)) {
            return false;
        }
        return setCharacteristicNotification(UUID.fromString(serviceuuid), UUID.fromString(charuuid), enabled);
    }

    /**
     * ===================================
     * 蓝牙回调这些方法进行交互
     * ===================================
     */

    /**
     * On characteristic read.
     *
     * @param characteristic the characteristic
     * @param status         the status
     */
    public abstract void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status);


    /**
     * On characteristic write.
     *
     * @param characteristic the characteristic
     * @param status         the status
     */
    public abstract void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) ;

    /**
     * On characteristic changed.
     *
     * @param characteristic the characteristic
     */
    public abstract void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);

}
