package momorexmo.module;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.resurce.library.R;
import android.util.Log;

import momorexmo.module.Utils.parse;

public class AppSettings
{
    //region SheradPereferance
    private static String getSetupTag(Context context) {
        return context.getResources().getString(R.string.app_name).replace(" ","_");
    }
    public static void set(String setup_name,Object value) {
        set(AppRichActivity.getActivity(),setup_name,value);
    }
    public static void set(Context context, String Setup_name, Object Value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getSetupTag(context)+"." + Setup_name, String.valueOf(Value));
        editor.commit();
    }
    public static String get(Object Setup_name,String Defult) {
        return get(AppRichActivity.getActivity(),Setup_name,Defult);
    }
    public static String get(Context context, Object Setup_name,String Defult) {
        try
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String str;
            str = sharedPreferences.getString(getSetupTag(context)+"." + Setup_name, null);
            if (str.isEmpty() || str.length()==0)
                return Defult;
            else return str;
        } catch (Exception e) {
            return Defult;
        }
    }
    //endregion
    //region cultureDigitSeperator
    public static String cultureDigitSeperator = "";
    public static String getCultureDigitSeperator() {
        return get("cultureDigitSeperator",cultureDigitSeperator);
    }
    public static void setCultureDigitSeperator(String cultureDigitSeperator) {
        set("cultureDigitSeperator",cultureDigitSeperator);
    }
    //endregion
    //region showPrintLog

    public static boolean showPrintLog = true;
    public static boolean isShowPrintLog () {
        return parse.toBoolean(get("showPrintLog",showPrintLog?"1":"0"));
    }
    public static void setShowPrintLog (boolean showPrintLog) {
        set("showPrintLog",showPrintLog);
    }

    //endregion
    //region Print
    public static void Print(String error) {Print(AppRichActivity.getActivityIgnoreException(),"i","",error);}
    public static void Print(String type,String error) {Print(AppRichActivity.getActivityIgnoreException(),type,"",error);}
    public static void Print(String type,String tag,String error) {Print(AppRichActivity.getActivityIgnoreException(),type,tag,error);}
    public static void Print(Activity activity, String type, String tag, String error) {
        if (!isShowPrintLog()) return;
        String form = "|->"+tag;
        if (activity != null)
            form=activity.getLocalClassName()+"\n";
        if (tag.length()>0)
            form+=tag+"\n";

        form += error;
        tag="|GizLog|";
        switch (type.toLowerCase())
        {
            case "e":
                Log.e(tag,form);
                break;
            case "i":
                Log.i(tag,form);
                break;
            default:
                Log.d(tag,form);
                break;
        }
    }
    //endregion
    //region DeviceInfo
    public static String getDeviceID() { return getDeviceID(AppRichActivity.getActivity()); }
    public static String getDeviceID(Context context) { return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID); }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    //endregion
}
