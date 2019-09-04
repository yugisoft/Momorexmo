package momorexmo.module.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import momorexmo.module.AppRichActivity;
import momorexmo.module.AppSettings;
import momorexmo.module.DataTable;
import momorexmo.module.DateTime;

import static android.util.DisplayMetrics.DENSITY_DEFAULT;

public class parse
{
    //region NumberFormat
    public static String NF2Replace(Object ob) {
        return  NF(ob,true,2);
    }
    public static String NF2(Object ob) {
        return  NF(ob,false,2);
    }
    public static String NFReplace(Object ob, int bas) {
        return  NF(ob,true,bas);
    }
    public static String NF(Object ob, int bas) {
        return  NF(ob,false,bas);
    }
    public static String NF(Object ob, boolean Replace, int bas) {
        double d =0;
        try{d= Double.parseDouble(ob.toString().replace(",","."));}
        catch (Exception e){}
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(bas);      // Burada virgülden sonra maksimum 2 karakter olacağı belirtiliyor.
        nf.setMinimumFractionDigits(bas);      // Burada virgülden sonra minimum 2 karakter olacağı belirtiliyor.
        String s = nf.format(d);

        if (Replace && AppSettings.getCultureDigitSeperator().equals("."))
            return  s.replace(".","").replace(",",".");
        else if (Replace && AppSettings.getCultureDigitSeperator().equals(","))
            return  s.replace(",","").replace(".",",");
        else
            return  s;

    }
    //endregion
    //region Convertion
    public static int toInt(Object p) {
        try {
            return Integer.parseInt(NF(p.toString().replace(".0", "").replace(",0", ""),0));
        } catch (Exception ex) {
            return defaultInt;
        }
    }

    public static long toLong(Object p) {
        try {
            return Long.parseLong(NF(p.toString().replace(".0", "").replace(",0", ""),0));
        } catch (Exception ex) {
            return defaultLong;
        }
    }

    public static double toDouble(Object p) {
        try {
            return Double.parseDouble(p.toString());
        } catch (Exception ex) {
            try {
                return Double.parseDouble(p.toString().replace(",", "."));
            } catch (Exception exd) {
                return defaultDouble;
            }
        }
    }

    public static float toFloat(Object p) {
        try {
            return Float.parseFloat(p.toString());
        } catch (Exception ex) {
            try {
                return Float.parseFloat(p.toString().replace(",", "."));
            } catch (Exception exd) {
                return defaultFloat;
            }
        }
    }

    public static boolean toBoolean(Object p) {
        try {
            if (toInt(p) == 1)
                return true;
            else
                return Boolean.parseBoolean(p.toString());
        } catch (Exception ex) {
            return defaultBoolean;
        }
    }


    public static DateTime toDateTime(Object value) {
        try {
            boolean Iso8601 = value.toString().indexOf("T") > 0;
            if (Iso8601)
                return DateTime.fromISO8601UTC(value.toString());
            else
                return DateTime.fromDateTime(value.toString());
        } catch (Exception ex) {
            return null;
        }

    }

    public static DataTable toDataTable(Object p) {
        try {
            if (p instanceof String)
                return new DataTable(p.toString());
            else
                // TODO: 04.09.2019 Convert To Json Eklenecek
                return null;//new DataTable(toJson(p));
        } catch (Exception ex) {
            return new DataTable();
        }
    }


    public static int DpToPixel(float dp){ return  DpToPixel(dp, AppRichActivity.getActivity()); }
    public static int DpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DENSITY_DEFAULT);
        return toInt(px);
    }
    public static int PixelToDp(float px){ return  PixelToDp(px,AppRichActivity.getActivity()); }
    public static int PixelToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return toInt(dp);
    }
    //endregion
    //region Convertion Defaults
    private static int defaultInt = 0;

    public static int getDefaultInt() {
        return defaultInt;
    }

    public static void setDefaultInt(int defaultInt) {
        defaultInt = defaultInt;
    }

    private static double defaultDouble = 0;

    public static double getDefaultDouble() {
        return defaultDouble;
    }

    public static void setDefaultDouble(double defaultDouble) {
        defaultDouble = defaultDouble;
    }

    private static float defaultFloat = 0f;

    private static boolean defaultBoolean = false;

    public static boolean getDefaultBoolean() {
        return defaultBoolean;
    }

    public static void setDefaultBoolean(boolean defaultBoolean) {
        defaultBoolean = defaultBoolean;
    }

    private static long defaultLong = 0;

    public static long getDefaultLong() {
        return defaultLong;
    }

    public static void setDefaultLong(long defaultLong) {
        defaultLong = defaultLong;
    }


    public static float getDefaultFloat() {
        return defaultFloat;
    }

    //endregion
    //region String Join
    public static String Join(String string , Object... params) {
        try
        {
            int i = 0;
            for (Object item : params)
            {
                if (item.getClass() == Object[].class)
                {
                    for (Object item2 : ((Object[])item))
                    {
                        string = string.replace("{"+i+"}",String.valueOf(item2));
                        i++;
                    }
                }
                else
                {
                    string = string.replace("{"+i+"}",String.valueOf(item));
                    i++;
                }
            }

        }
        catch (Exception ex){}
        return string;
    }
    //endregion
    //region Formetter
    public static class Formatter
    {
        public static String get(String format, Object args) {
            format = args instanceof DataTable.DataRow ? purifyDR(format, (DataTable.DataRow) args) : purify(format, args);
            return String.format(format, args);
        }

        public static String purify(String format, Object object) {


            try {
                /*Modeldeki alanları bulma*/
                //region Field
                while (format.contains("${")) {
                    try {
                        String f, fieldName, fieldFormat;
                        f = format.replace("${", "~").split("~")[1].split("\\}")[0];
                        String[] fieldArea = f.split(":");
                        fieldName = fieldArea[0];
                        String fS = "${" + f + "}";
                        fieldFormat = fieldArea.length > 1 ? fieldArea[1] : "";
                        try {
                            Field ff = CustomUtil.getField(object,fieldName);
                            /*
                            try {
                                ff = object.getClass().getField(fieldName);
                            } catch (Exception e) {
                            }
                            if (ff == null)
                                ff = object.getClass().getDeclaredField(fieldName);
                            */
                            if (ff != null) {
                                ff.setAccessible(true);
                                Object value = ff.get(object);
                                if (fieldFormat.length() > 0)
                                {
                                    if (fieldFormat.substring(0, 1).equals("n")) {
                                        int len = toInt(fieldFormat.substring(1, fieldFormat.length()));

                                        format = format.replace(fS, NF(value, len));
                                    } else {
                                        switch (fieldFormat) {

                                            case "DS":
                                                format = format.replace(fS, DateTime.fromISO8601UTC(value.toString()).toShortDateString());
                                                break;
                                            case "DL":
                                                format = format.replace(fS, DateTime.fromISO8601UTC(value.toString()).toLongDateString());
                                                break;
                                            case "TS":
                                                format = format.replace(fS, DateTime.fromISO8601UTC(value.toString()).toShortTimeString());
                                                break;
                                            case "TL":
                                                format = format.replace(fS, DateTime.fromISO8601UTC(value.toString()).toLongTimeString());
                                                break;
                                            case "DTS":
                                                format = format.replace(fS, DateTime.fromISO8601UTC(value.toString()).toShortDateTimeString());
                                                break;
                                            case "DTL":
                                                format = format.replace(fS, DateTime.fromISO8601UTC(value.toString()).toLongDateTimeString());
                                                break;
                                        }
                                    }
                                }
                                else
                                {
                                    format = format.replace(fS, value.toString());
                                }
                                ff.setAccessible(false);
                            } else {
                                format = format.replace(fS, "");
                            }

                        } catch (Exception ex) {
                            format = format.replace(fS, "");
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                //endregion

                /*String Resource Okuma*/
                format = purifyS(format);
            } catch (Exception ex) {

            }


            return format;
        }

        public static String purifyDR(String format, DataTable.DataRow row) {


            try {
                /*Modeldeki alanları bulma*/
                //region Field
                if (format.length() > 0) {
                    while (format.contains("${")) {
                        try {
                            String f, fieldName, fieldFormat;
                            f = format.replace("${", "~").split("~")[1].split("\\}")[0];
                            String[] fieldArea = f.split(":");
                            fieldName = fieldArea[0];
                            fieldFormat = fieldArea.length > 1 ? fieldArea[1] : "";
                            String fS = "${" + f + "}";
                            String value = row.get(fieldName);

                            if (value.length() == 0) {
                                format = format.replace("${" + fieldName + "}", "");
                            } else if (fieldFormat.length() > 0)
                            {

                                if (fieldFormat.substring(0, 1).equals("n")) {
                                    int len = toInt(fieldFormat.substring(1, fieldFormat.length()));

                                    format = format.replace(fS, NF(value, len));
                                } else {
                                    switch (fieldFormat) {

                                        case "DS":
                                            format = format.replace(fS, DateTime.fromISO8601UTC(value).toShortDateString());
                                            break;
                                        case "DL":
                                            format = format.replace(fS, DateTime.fromISO8601UTC(value).toLongDateString());
                                            break;
                                        case "TS":
                                            format = format.replace(fS, DateTime.fromISO8601UTC(value).toShortTimeString());
                                            break;
                                        case "TL":
                                            format = format.replace(fS, DateTime.fromISO8601UTC(value).toLongTimeString());
                                            break;
                                        case "DTS":
                                            format = format.replace(fS, DateTime.fromISO8601UTC(value).toShortDateTimeString());
                                            break;
                                        case "DTL":
                                            format = format.replace(fS, DateTime.fromISO8601UTC(value).toLongDateTimeString());
                                            break;
                                    }
                                }

                            } else {
                                format = format.replace(fS, value);
                            }

                        } catch (Exception e) {
                            break;
                        }
                    }
                } else {

                }
                //endregion

                /*String Resource Okuma*/
                format = purifyS(format);
            } catch (Exception ex) {

            }


            return format;
        }

        public static String purifyS(String format) {
            /*String Resource Okuma*/
            while (format.contains("$S{")) {
                try {
                    Activity activity = AppRichActivity.getActivity();
                    String f = format.replace("$S{", "~").split("~")[1].split("\\}")[0];
                    try {
                        int id = activity.getResources().getIdentifier(f, "string", activity.getPackageName());
                        format = format.replace("$S{" + f + "}", activity.getResources().getString(id));
                    } catch (Exception ex) {
                        format = format.replace("$S{" + f + "}", "");
                    }
                } catch (Exception ex2) {
                    break;
                }
            }

            return format;
        }
    }
    //endregion
}
