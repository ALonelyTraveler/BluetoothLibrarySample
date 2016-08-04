package com.bandou.bluetooth.sample;

import android.bluetooth.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.bandou.bluetooth.sample.model.DeviceInfo;
import com.bdkj.ble.connector.BleConnector;
import com.bdkj.ble.scanner.BLEScanner;
import com.bdkj.ble.scanner.BaseScanner;
import com.bdkj.ble.scanner.ScanCallBack;
import com.bdkj.ble.util.CHexConver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String serviceUUID = "14839AC4-7D7E-415C-9A42-167340CF2339";
    private String characteristicUUID = "8B00ACE7-EB0B-49B0-BBE9-9AEE0A26E1A3";
    private String notifyUUID = "0734594A-A8E7-4B1A-A6B1-CD5243059A57";

    private String battery_service = "0000180f-0000-1000-8000-00805f9b34fb";
    private String battery_character = "00002a19-0000-1000-8000-00805f9b34fb";
    List<DeviceInfo> list = new ArrayList<>();
    List<String> names = new ArrayList<>();
    private boolean isConnected;
    private boolean isCancel;
    private String address;
    ListView lvDevice = null;
    BaseScanner scanner = null;

            BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("MainActivity", "************************newState=" + newState+",status="+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (newState == BluetoothGatt.STATE_CONNECTED) {
//                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    // 进行服务发现，50ms
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    gatt.discoverServices();
                    isConnected = true;
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
//                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    isConnected = false;
                    if (!isCancel) {
                        Log.d("MainActivity", "重连----=================================================");
                        if (checkIsSamsung()) {

                            gatt.connect();
                        }
                        else{
                            try {
                                Thread.sleep(600);
                                connector.connect(address);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    else{
                        connector.closeGatt();
                    }
                }
            }
            else{
                isConnected = false;
                if (!isCancel) {
                    Log.d("MainActivity", "重连----=================================================");
                    if (checkIsSamsung()) {

                        gatt.connect();
                    }
                    else{
                        try {
                            Thread.sleep(600);
                            connector.connect(address);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else{
                    connector.closeGatt();
                }
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //输出服务
                List<BluetoothGattService> services = gatt.getServices();
                //调试模式下输出
                    if (services != null) {
                        for (BluetoothGattService service : services) {
                            Log.d("BleController", "┌--------------------------------------┑");
                            Log.d("BleController", "|S:" + service.getUuid().toString() + "|");
                            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                            if (characteristics != null) {
                                for (BluetoothGattCharacteristic characteristic : characteristics) {
                                    Log.d("BleController", "|C:" + characteristic.getUuid().toString() + "|");
                                    List<BluetoothGattDescriptor> descriptor = characteristic.getDescriptors();
                                    if (descriptor != null) {
                                        for (BluetoothGattDescriptor bluetoothGattDescriptor : descriptor) {
                                            Log.d("BleController", "|D:" + bluetoothGattDescriptor.getUuid().toString() + "|");
                                        }
                                    }
                                }
                            }
                            Log.d("BleController", "└--------------------------------------┙");
                        }
                }
//                BluetoothGattService service = gatt.getService(UUID.fromString(serviceUUID));
//                BluetoothGattCharacteristic readCharacteristic_2 = service.getCharacteristic(UUID.fromString(characteristicUUID));
//                //gatt.readCharacteristic(readCharacteristic_2);
//                readCharacteristic_2.setValue(new byte[]{(byte) 0xFE, (byte) 0xEF,0x03,0x00,0x75,0x76});
//                gatt.writeCharacteristic(readCharacteristic_2);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                Log.d("BleController", "onCharacteristicRead:" + CHexConver.byte2HexStr(data,data.length));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                Log.d("BleController", "onCharacteristicWrite:" + CHexConver.byte2HexStr(data,data.length));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };
    private boolean checkIsSamsung() { //此方法是我自行使用众多三星手机总结出来，不一定很准确
        String brand = android.os.Build.BRAND;
        Log.e("", " brand:" + brand);
        if (brand.toLowerCase().equals("samsung")) {
            return true;
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancel = true;
        if (scanner != null && scanner.isScanning()) {
            scanner.stopScan();
        }
        if (connector != null) {
            if (isConnected) {
                connector.disconnect();
            }
            else{
                connector.cancelConnect();
            }
        }
    }
    BleConnector connector = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvDevice = (ListView) findViewById(R.id.lvDevices);
        lvDevice.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names));
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                scanner.stopScan();
                isCancel = false;
                DeviceInfo info = list.get(i);
                address = info.address;
                if (connector == null) {
                    connector = new BleConnector(MainActivity.this, mCallback);
                }

                try{
                    connector.connect(info.address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        scanner = new BLEScanner(this,8000);
//        scanner.setBluetoothFilter(new NameMatcher("WL75"));
        scanner.setCallBack(new ScanCallBack() {
            @Override
            public void startScan() {
                Toast.makeText(MainActivity.this, "开始搜索", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void finishScan() {
                Toast.makeText(MainActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void foundSpeificDevice(String name, String address, int rssi) {
                DeviceInfo info = new DeviceInfo();
                info.name = name;
                info.address = address;
                info.rssi = rssi;
                list.add(info);
                names.add(name);
                ((ArrayAdapter)lvDevice.getAdapter()).notifyDataSetChanged();

            }
        });
        scanner.startScan();

        findViewById(R.id.btnDisconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCancel = true;
                if (connector != null) {
                    if (isConnected) {
                        connector.disconnect();
                    }
                    else{
                        connector.cancelConnect();
                    }
                }
            }
        });
    }

}
