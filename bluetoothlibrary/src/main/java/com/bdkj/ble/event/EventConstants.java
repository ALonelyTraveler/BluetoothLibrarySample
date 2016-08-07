package com.bdkj.ble.event;

import com.bdkj.ble.BluetoothLibrary;

/**
 *  事件常量
 * @author: chenwei
 * @version: V1.0
 */
public class EventConstants {
    /**=======================================
     *
     *   连接消息类型
     *
     =======================================*/
    /**
     * 连接成功
     */
    public static final String SUCCESS = BluetoothLibrary.getPackageName() + ".connect.success";
    /**
     * 连接失败
     */
    public static final String FAIL = BluetoothLibrary.getPackageName() + ".connect.fail";

    /**
     * 连接超时
     */
    public static final String TIMEOUT = BluetoothLibrary.getPackageName() + ".connect.timeout";


    /**=======================================
     *
     *   服务消息类型
     *
     =======================================*/
    /**
     * 发现服务
     */
    public static final String DISCOVERY_SERVICE = BluetoothLibrary.getPackageName() + ".service.discover";

    /**=======================================
     *
     *   连接状态消息类型
     *
     =======================================*/
    /**
     * 正在连接状态
     */
    public static final String STATE_CONNECTING = BluetoothLibrary.getPackageName() + ".status.connecting";

    /**
     * 已连接状态
     */
    public static final String STATE_CONNECTED = BluetoothLibrary.getPackageName() + ".status.connected";

    /**
     * 正在断开的状态
     */
    public static final String STATE_DISCONNECTING = BluetoothLibrary.getPackageName() + ".status.disconnecting";

    /**
     * 已经断开
     */
    public static final String STATE_DISCONNECTED = BluetoothLibrary.getPackageName() + ".status.disconnected";

    /**=======================================
     *
     *   重连消息类型
     *
     =======================================*/
    /**
     * 首次连接重连
     */
    public static final String FIRST_RECONNECT = BluetoothLibrary.getPackageName() + ".reconnect.first";

    /**
     * 中断重连
     */
    public static final String SUSPEND_RECONNECT = BluetoothLibrary.getPackageName() + ".reconnect.suspend";

    /**=======================================
     *
     *   数据相关消息类型
     *
     =======================================*/
    /**
     * 接收数据
     */
    public static final String RECEIVE_DATA = BluetoothLibrary.getPackageName() + ".data.receive";

    /**=======================================
     *
     *   连接断开消息类型
     *
     =======================================*/
    /**
     * 正常断开
     */
    public static final String BREAK_NORMAL = BluetoothLibrary.getPackageName() + ".disconnect.normal";

    /**
     * 连接过程中断开
     */
    public static final String BREAK_CONNECTED = BluetoothLibrary.getPackageName() + ".disconnect.break";
}
