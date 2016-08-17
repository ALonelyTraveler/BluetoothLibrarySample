# BluetoothLibrarySample
蓝牙搜索、连接、数据收发等工具库
##当前版本(VERSION)
[![Maven Central](https://img.shields.io/badge/VERSION-0.2.2-orange.svg)](https://bintray.com/gcssloop/maven/sutil/view)

## gradle依赖

	compile 'com.bandou:bluetoothlibrary:VERSION'
	
## 项目依赖

	compile 'io.reactivex:rxjava:1.1.5'
	compile 'io.reactivex:rxandroid:1.2.0'
	compile 'org.greenrobot:eventbus:3.0.0'
  	
  	
## 使用说明

### 一、搜索

	//定义传统蓝牙搜索方式并指定搜索时间
	//定义传统蓝牙搜索模式
	BaseScanner scanner = new ClassicScanner(Context, 8000);
	//定义低功耗搜索模式(API >= 18)
	//BaseScanner scanner = new BLEScanner(8000);
	//声明定义过滤器
	BluetoothFilter filter = new NameMatcher("MacBook Pro");
	//地址过滤
	//BluetoothFilter filter = new AddressMatcher("00:00:00:12");
	//默认不进行过滤
	//BluetoothFilter filter = new DefaultFilter();
	//设置搜索设备过滤条件
	scanner.setBluetoothFilter(filter);
	//设置搜索器回调接口
	scanner.setCallBack(new ScanCallBack() {
            @Override
            public void startScan() {
            	//开始搜索
            }

            @Override
            public void finishScan() {
                //在指定的时间内搜索完成时回调
            }

            @Override
            public void foundSpeificDevice(String name, String address, int rssi) {
            	//搜索到设备时调用
                
            }
        });
	//中途停止搜索
	scanner.stopScan();
	
### 二、连接
#### 传统蓝牙SPP连接
> 蓝牙SPP由于connect()方法会阻塞主线程，所有连接部分由RxJava和RxAndroid代劳。数据的读取通过对InputStream进行循环读接收byte数据，并由BluetoothSecretary进行数据发送。

	//创建ClassicController对象
	ClassicController mController = new ClassicController<ClassicSecretary>(new ClassicSecretary() {
        @Override
        public void receiveData(byte[] data) {
              //对获取的data进行处理和加工
        }
	});
	//指定通知发送方式,具体事件可查看源码（com.bdkj.ble.event.EventConstants）
	mController.setBroadcaster(new EventBusBroadcaster());
	//mController.setBroadcaster(new ReceiverBroadcaster(mContext.getApplicationContext()))
	//设置重连的次数,默认是不进行重试
	mController.setRetry(true,3);
	//连接指定蓝牙设备
	try {
		mController.connect(macAddress);
	} catch (IOException e) {
	}
	//写数据
	byte[] data = {0x01,0x02,0x03};
	mController.getBluetoothSecretary().write(data);
	
	//是否连接
	if (mController.isConnect()) {
		//与连接的设备断开
		mController.disconnect();
	} else {
		//取消正在连接中的设备
		mController.cancelConnect();
	}

#### 低功耗蓝牙连接
> BLE蓝牙的connect是异步的，大部分开发者建议连接部分放在主线程能提高连接的成功率。

	 BleController mController = new BleController<BleSecretary>(BLEActivity.this.getApplicationContext(), new BleSecretary() {

		@Override
		public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
			//处理接收到的BluetoothGattCharacteristic数据
		}

		@Override
		public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
			//处理接收到的BluetoothGattCharacteristic数据
		}

		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
			//处理接收到的BluetoothGattCharacteristic数据
		}
	});
	//指定通知发送方式,具体事件可查看源码（com.bdkj.ble.event.EventConstants）
	mController.setBroadcaster(new EventBusBroadcaster());
	//mController.setBroadcaster(new ReceiverBroadcaster(mContext.getApplicationContext()))
	//设置重连的次数,默认是不进行重试
	mController.setRetry(true,3);
	//连接指定蓝牙设备
	try {
		 mController.connect(info.address);
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	//读取设备电量
	String battery_service = "0000180f-0000-1000-8000-00805f9b34fb";
	String battery_character = "00002a19-0000-1000-8000-00805f9b34fb";
	mController.getBluetoothSecretary().readCharacteristic(battery_service,battery_character);
	//mController.getBluetoothSecretary().readCharacteristic(ServiceUUID,CharcterUUID);
	//向设备写数据,如果发送成功，会回调上方的onCharacteristicWrite方法
	byte[] data = {0x01,0x02,0x03};
	String serviceuuid = "0000180f-0000-1000-8000-00805f9b34fb";
	String charcteruuid = "00002a19-0000-1000-8000-00805f9b34fb";
	mController.getBluetoothSecretary().writeCharcteristic(serviceuuid,charcteruuid,data);
	
	//是否连接
	if (mController.isConnect()) {
		//与连接的设备断开
		mController.disconnect();
	} else {
		//取消正在连接中的设备
		mController.cancelConnect();
	}
	
##bug
1. 如果设置重连setRetry(true,3)。那么在被连接设备主动取消配对的情况下，被连接设备会多次被请求配对。
2. 传统蓝牙的SPP连接方式如果正在连接的过程中调用disconnect()方法有时并不会中断蓝牙的连接，因为connect是一个阻塞的过程，如果被连接设备不可连接，那connect过程势必花费很长的时间，如果一直处于连接中的状态，那下一次连接时如果还未结束，则会出现连接失败的情况。


## 更新日志

>
>0.2.2 (2016-8-17)
>
>* BluetoothUtils添加获取本地蓝牙地址的方法
>
>--------------------------
>
>0.2.1 (2016-8-9)
>
>* 初始化版本
>