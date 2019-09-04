package momorexmo.module.Models;

import android.util.DisplayMetrics;

public class DeviceInfo
{
    private String deviceID;
    private String deviceName;
    private DisplayMetrics displayMetrics;

    public String getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }
    public void setDisplayMetrics(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
    }
}
