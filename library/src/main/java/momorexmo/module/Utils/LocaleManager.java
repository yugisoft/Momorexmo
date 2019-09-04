package momorexmo.module.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import momorexmo.module.AppSettings;

public class LocaleManager
{
    public static void setLocale(Context c) {
        String language = AppSettings.get(c,"Language",getLanguage(c));
        Locale myLocale = new Locale(language);
        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
    public static void setLocale(Context c, String language) {

        Locale myLocale = new Locale(language);
        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        AppSettings.set(c,"Language",getLanguage(c));
    }
    public static String getLanguage(Context c) {
        return c.getResources().getConfiguration().locale.getLanguage();
    }
}