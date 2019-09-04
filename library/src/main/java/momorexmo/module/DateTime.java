package momorexmo.module;


import android.app.Activity;
import android.graphics.Color;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTime extends Date {

    private static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static String
            _popupFormat="yyyy[]MM[]dd",
            _dateFormat="dd[]MM[]yyyy",
            _longDateFormat="dd MMMM yyyy",
            _shortDateTimeFormat="dd[]MM[]yyyy HH:mm",
            _shortTimeFormat="HH:mm",
            _longTimeFormat ="HH:mm:ss",
            _longDateTimeFormat="dd MMMM yyyy HH:mm:ss"
                    ;


    public static String getPopupFormat() {
        return _popupFormat.replace("[]",getDateSeparator());
    }
    public static String getDateFormat() {
        return _dateFormat.replace("[]",getDateSeparator());
    }
    public static String getLongDateFormat() {
        return _longDateFormat.replace("[]",getDateSeparator());
    }
    public static String getShortTimeFormat() {
        return _shortTimeFormat;
    }

    public static void setShortTimeFormat(String _shortTimeFormat) {
        DateTime._shortTimeFormat = _shortTimeFormat;
    }

    public static String getLongTimeFormat() {
        return _longTimeFormat;
    }

    public static void setSongTimeFormat(String _longTimeFormat) {
        DateTime._longTimeFormat = _longTimeFormat;
    }

    public static void setLongDateFormat(String _longDateFormat) {
        DateTime._longDateFormat = _longDateFormat;
    }
    public static String getShortDateTimeFormat() {
        return _shortDateTimeFormat.replace("[]",getDateSeparator());
    }
    public static String getLongDateTimeFormat() {
        return _longDateTimeFormat.replace("[]",getDateSeparator());
    }



    TimeZone tz ;
    public static String dateSeparator=".";
    public static String getDateSeparator() {
        return dateSeparator;
    }

    public static void setDateSeparator(String dateSeparator) {
        DateTime.dateSeparator = dateSeparator;
    }
    public DateTime()
    {
        tz = TimeZone.getTimeZone("UTC");
    }

    public static DateTime fromISO8601UTC(String dateStr) {
        return fromDateTime(dateStr,ISO8601,"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }


    public static DateTime fromDateTime(String dateStr) {
        return fromDateTime(dateStr,getShortDateTimeFormat());
    }
    public static DateTime fromDateTime(String dateStr,String Format) {
        DateTime dateTime = new DateTime();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(Format);
        df.setTimeZone(tz);
        try
        {
            dateTime.setDateTime(df.parse(dateStr));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }
    public static DateTime fromDateTime(String dateStr,String Format,String Format2) {
        DateTime dateTime = new DateTime();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(Format);
        df.setTimeZone(tz);
        try
        {
            dateTime.setDateTime(df.parse(dateStr));
        }
        catch (Exception e) {
            try {
                df = new SimpleDateFormat(Format2);
                dateTime.setDateTime(df.parse(dateStr));
            } catch (ParseException e1) {

            }
        }
        return dateTime;
    }




    @Override
    public String toString() {
        return getIso8601();
    }
    public String toString(String format) {

        return getFormat(format);
    }





    public String toShortDateString() {

        SimpleDateFormat format=new SimpleDateFormat(getDateFormat());
        return format.format(this);
    }
    public String toLongDateString() {

        SimpleDateFormat format=new SimpleDateFormat(getLongDateFormat());
        return format.format(this);
    }

    public String toShortDateTimeString() {

        SimpleDateFormat format=new SimpleDateFormat(getShortDateTimeFormat());
        return format.format(this);
    }
    public String toLongDateTimeString() {

        SimpleDateFormat format=new SimpleDateFormat(getLongDateTimeFormat());
        return format.format(this);
    }

    public String toLongTimeString() {

        SimpleDateFormat format=new SimpleDateFormat(getLongTimeFormat());
        return format.format(this);
    }
    public String toShortTimeString() {

        SimpleDateFormat format=new SimpleDateFormat(getShortTimeFormat());
        return format.format(this);
    }

    public static DateTime Now()
    {
        Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        DateTime dateTime = new DateTime();
        dateTime.setDateTime(date);
        return  dateTime;
    }


    //region FastFormat

    public String getIso8601()
    {
        return getFormat(ISO8601);
    }

    public String getFormat(String Format) {
        tz = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat(Format, Locale.getDefault());
        dateFormat.setTimeZone(tz);
        String nowAsISO = dateFormat.format(this);
        return nowAsISO;
    }

    //endregion


    public void setDateFormat(String dateFormat) {
        this._dateFormat = dateFormat;
    }

    public void setShortDateTimeFormat(String shortDateTimeFormat) {
        this._shortDateTimeFormat = shortDateTimeFormat;
    }

    public void setLongDateTimeFormat(String longDateTimeFormat) {
        this._longDateTimeFormat = longDateTimeFormat;
    }

    public void setDateTime(Date date) {
        setTime(date.getDate());
        setTime(date.getTime());
    }

    public int[] getTimes()
    {
        String[] sTime = this.toLongDateTimeString().split(":");
        int[] time = { Integer.parseInt(sTime[0]),Integer.parseInt(sTime[1]),Integer.parseInt(sTime[2]) };
        return  time;
    }



}

