package com.highgo.cloud.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
public class TimeUtil {

    /**
     * saas  string数字转化为timestamp
     * @param timeString
     * @return
     */
    public static Timestamp convertTimeToTimestamp(String timeString)
    {
        String timeFormat = "yyyyMMddHHmmssSSS";
        SimpleDateFormat sdformat = new SimpleDateFormat(timeFormat);
        try {
            Date d = null;
            d = sdformat.parse(timeString);
            Timestamp ts = new Timestamp(d.getTime());
            return ts;
        } catch (Exception e) {
            log.error("convertTimeToTimestamp failed.", e);
        }
        return null;
    }

    public static Timestamp convertTimeToSecond(String timeString)
    {
        String timeFormat = "yyyyMMddHHmmss";
        SimpleDateFormat sdformat = new SimpleDateFormat(timeFormat);
        try {
            Date d = null;
            d = sdformat.parse(timeString);
            Timestamp ts = new Timestamp(d.getTime());
            return ts;
        } catch (Exception e) {
            log.error("convertTimeToSecond failed.", e);
        }
        return null;
    }

    public static String convertNumFormat(String timeString){
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdformat = new SimpleDateFormat(timeFormat);
        try {
            Date d = null;
            d = sdformat.parse(timeString);
            Timestamp ts = new Timestamp(d.getTime());
            return ts.toString();
        } catch (Exception e) {
            log.error("convertNumFormat failed.", e);
        }
        return null;
    }
}
