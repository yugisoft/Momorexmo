package momorexmo.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import momorexmo.module.Interfaces.AppRichActivityResultListener;
import momorexmo.module.Models.DeviceInfo;
import momorexmo.module.Utils.LocaleManager;

public class AppRichActivity extends AppCompatActivity
{
    //region Static
    private static AppRichActivity activity = null;
    public static AppRichActivity getActivity() {
        if (activity == null)
            new ModuleException("Aktif AppRichActivity activity Olmadığından İsteğiniz Gerçekleştirilemez.");
        return activity;
    }
    public static AppRichActivity getActivityIgnoreException() {

        return activity == null ? new AppRichActivity() : activity;
    }
    public static boolean LoadMainConfig = false;
    private static DeviceInfo deviceInfo;
    public static DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
    //endregion
    //region ReadOnly
    private View contentView;
    public View getContentView() {
        return contentView;
    }
    //endregion
    //region Props

    private boolean loaded = false;
    public boolean isLoaded() {
        return loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isFinisForCatchExtras() {
        return false;
    }
    //endregion
    //region Overrides
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        contentView = (View) findViewById(android.R.id.content);
        contentView.setOnClickListener(v -> hideKeyboard(v));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activity = this;

        super.onCreate(savedInstanceState);
        setLoaded(false);
        if (!LoadMainConfig) {
            LocaleManager.setLocale(this);

            switch (Locale.getDefault().getLanguage().toLowerCase()) {
                case "en":
                    AppSettings.cultureDigitSeperator = ",";
                    break;
                default:
                    AppSettings.cultureDigitSeperator = ".";
                    break;
            }
            deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceID(AppSettings.getDeviceID());
            deviceInfo.setDeviceName(AppSettings.getDeviceName());
            LoadMainConfig = true;
        }
    }

    @Override
    protected void onResume() {
        activity = this;
        super.onResume();
    }
    //endregion
    //region Extensions
    public void hideKeyboard() {
        hideKeyboard(getContentView());
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (AppRichDialog.isShowing())
                in.hideSoftInputFromWindow(AppRichDialog.mDialog.getWindow().peekDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ex) {

        }


    }

    public Object get(String key) {
        try {
            Bundle extras = getIntent().getExtras();
            return extras.get(key);
        } catch (Exception ex) {
            if (isFinisForCatchExtras())
                this.finish();
        }
        return "";
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        try {
            return Integer.parseInt(getString(key).replace(".0", ""));
        } catch (Exception ex) {
            return def;
        }
    }

    public String getString(String key) {
        try {
            return String.valueOf(get(key));
        } catch (Exception ex) {
            return "";
        }
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long def) {
        try {
            return Long.parseLong(getString(key).replace(".0", ""));
        } catch (Exception ex) {
            return def;
        }
    }
    //endregion
    //region ActivityRusult

    private Hashtable<String, AppRichActivityResultListener> activityResultListerHash = new Hashtable<>();
    public void addResultListener(String listenerKey, AppRichActivityResultListener listener)
    {
        activityResultListerHash.put(listenerKey,listener);
    }
    public void removeResultListener(String listenerKey)
    {
        activityResultListerHash.remove(listenerKey);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Enumeration<String> keys =  activityResultListerHash.keys();
        while (keys.hasMoreElements())
            activityResultListerHash.get(keys.nextElement()).onvActivityResult(requestCode,resultCode,data);
    }

    //endregion
}
