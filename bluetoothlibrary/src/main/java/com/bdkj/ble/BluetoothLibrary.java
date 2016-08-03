package com.bdkj.ble;

/**
 * @ClassName: BluetoothLibrary
 * @Description: 为库提供必要的工程信息
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/3 上午10:59
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
