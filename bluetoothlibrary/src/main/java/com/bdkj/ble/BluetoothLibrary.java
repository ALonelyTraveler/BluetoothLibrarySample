package com.bdkj.ble;

/**
 * The type Bluetooth library.
 *
 * 为库提供必要的工程信息
 * @author: chenwei
 * @version: V1.0
 */
public class BluetoothLibrary {

    private static BluetoothLibrary mLibrary = new BluetoothLibrary();
    /**
     * The constant sPackageName.
     */
    public static String sPackageName = "";

    /**
     * The constant debug.
     */
    public static boolean debug = false;

    /**
     * Init package.
     * 初始化包名
     *
     * @param packageName the package name
     * @return the bluetooth library
     */
    public static BluetoothLibrary initPackage(String packageName) {
        mLibrary.sPackageName = packageName;
        return mLibrary;
    }

    /**
     * Sets debug.
     *
     * @param debug the debug
     * @return the debug
     */
    public static BluetoothLibrary setDebug(boolean debug)
    {
        mLibrary.debug = debug;
        return mLibrary;
    }

    /**
     * Gets package name.
     * 获取包名
     *
     * @return the package name
     */
    public static String getPackageName() {
        return sPackageName;
    }

    /**
     * Is debug boolean.
     *
     * @return the boolean
     */
    public static boolean isDebug() {
        return debug;
    }
}
