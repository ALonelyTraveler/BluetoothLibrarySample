package com.bdkj.ble;

/**
 * The type Bluetooth library.
 *
 * 为库提供必要的工程信息
 * @author: chenwei
 * @version: V1.0
 */
public class BluetoothLibrary {

    /**
     * The constant sPackageName.
     */
    public static String sPackageName = "";

    /**
     * Init package.
     * 初始化包名
     *
     * @param packageName the package name
     */
    public void initPackage(String packageName) {
        this.sPackageName = packageName;
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
}
