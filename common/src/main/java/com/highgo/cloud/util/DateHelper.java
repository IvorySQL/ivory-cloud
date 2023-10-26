/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.cloud.util;
/**
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cww
 * 日期帮助
 */
@Slf4j
public class DateHelper {

    public final static String[] PATTERNS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss+zz",
            "yyyy-MM-dd HH:mm:sszz",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH",
            "yyyy-MM-dd",
            "yyyy-MM",
            "yyyyMMdd",
            "yyyyMM",
            "yyyy",
    };
    private static class DatePattern {

        public String pattern;
        public TimeZone zone;
        public DatePattern(String pattern, TimeZone zone) {
            this.pattern = pattern;
            this.zone = zone;
        }

        public DatePattern(String pattern) {
            this.pattern = pattern;
        }
    }
    private final static List<DatePattern> P = new ArrayList<DatePattern>(11);
    static {
        P.add(new DatePattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT+0:00")));
        P.add(new DatePattern("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("GMT+0:00")));
        P.add(new DatePattern("yyyy-MM-dd HH:mm:ss+zz"));
        P.add(new DatePattern("yyyy-MM-dd HH:mm:sszz"));
        P.add(new DatePattern("yyyy-MM-dd HH:mm:ss"));
        P.add(new DatePattern("yyyy-MM-dd HH:mm"));
        P.add(new DatePattern("yyyy-MM-dd HH"));
        P.add(new DatePattern("yyyy-MM-dd"));
        P.add(new DatePattern("yyyy-MM"));
        P.add(new DatePattern("yyyyMMdd"));
        P.add(new DatePattern("yyyyMM"));
        P.add(new DatePattern("yyyy"));
    }
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public final static String DATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd日HH:mm
     */
    public final static String DATE_NOS = "yyyy-MM-dd日HH:mm";

    /**
     * yyyy-MM-dd
     */
    public final static String DATE_NOTIME = "yyyy-MM-dd";
    /**
     * yyyy-MM
     */
    public final static String YEARMONTH = "yyyy-MM";
    /**
     * HH:mm:ss
     */
    public final static String TIME = "HH:mm";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public final static String TIME_FILENAME = "yyyyMMdd-HHmmssSSS";

    public static java.util.Date parseDate(String strdate) {
        java.util.Date date = null;

        if (strdate != null && !strdate.equals("")) {
            for (DatePattern p : P) {
                SimpleDateFormat dt = new SimpleDateFormat(p.pattern);
                if (p.zone != null) {
                    dt.setTimeZone(p.zone);
                }
                try {
                    date = dt.parse(strdate);
                    break;
                } catch (Exception e) {
                    log.error("parseDate failed.", e);
                }
            }
        }
        return date;
    }

    /**
     * 取得一个月的第一天, 并把时\分\秒设置为0
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 取得相对date便宜offset毫秒的日期
     * @param date 日期
     * @param offset 毫秒数，可以为负数
     * @return
     */
    public static Date getOffset(Date date, long offset) {
        Date ndate = new Date(date.getTime() + offset);
        return ndate;
    }

    /**
     * 比较两个时间， 若date1小于date2，返回true
     * @param date1
     * @param date2
     * @return
     */
    public static boolean HMLessThan(Date date1, Date date2) {
        boolean less = false;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        if (c1.get(Calendar.HOUR_OF_DAY) < c2.get(Calendar.HOUR_OF_DAY) ||
                (c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)
                        && c1.get(Calendar.MINUTE) <= c2.get(Calendar.MINUTE))) {
            less = true;
        }
        return less;
    }

    /**
     * 取得日期的下n个月的日期
     * @param date
     * @param n
     * @return
     */
    public static Date getNextNMonth(Date date, int n) {
        Date datex = null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + n);
        datex = c.getTime();
        return datex;
    }

    public static Date getNextNYear(Date date, int n) {
        Date datex = null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + n);
        datex = c.getTime();
        return datex;
    }

    /**
     * 判断两个日期的年\月\日 是否相等
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equalsDay(Date date1, Date date2) {
        boolean equals = false;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
            equals = true;
        }
        return equals;
    }

    /**
     * 判断两个日期的时、分 是否相等
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equalsTime(Date date1, Date date2) {
        boolean equals = false;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        if (c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)
                && c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE)) {
            equals = true;
        }
        return equals;
    }

    public static long getMinutes(Date date1, Date date2) {
        long minutes = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        c1.set(Calendar.SECOND, 0);
        c2.set(Calendar.SECOND, 0);
        Date date3 = c1.getTime();
        Date date4 = c2.getTime();
        long seconds = Math.abs(date3.getTime() - date4.getTime()) / 1000;
        minutes = seconds / 60;
        return minutes;
    }

    /**
     * 取得DayOfYear
     * @param date
     * @return
     */
    public static int getDayOfYear(Date date) {
        int dayOfYear = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        return dayOfYear;
    }

    /**
     * 取得DayOfMonth
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        int dayOfMonth = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth;
    }

    /**
     * DayOfWeek
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        int dayOfWeek = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek = 7;
        } else {
            dayOfWeek = dayOfWeek - 1;
        }
        return dayOfWeek;
    }

    /**
     * WeekOfMonth
     * @param date
     * @return
     */
    public static int getWeekOfMonth(Date date) {
        int weekOfMonth = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        weekOfMonth = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        return weekOfMonth;
    }

    /**
     * MonthOfYear
     * @param date
     * @return
     */
    public static int getMonthOfYear(Date date) {
        int monthOfYear = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        monthOfYear = c.get(Calendar.MONTH) + 1;
        return monthOfYear;
    }

    public static Date getNextNDate(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(getDate(getDay(date)));
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + n);
        return c.getTime();
    }

    /**
     * 取得跨月的两个日期之间的天数（前一月的天数、后一月的天数）
     * @param date1
     * @param date2
     * @return
     */
    public static int[] getTwoMonthDays(Date date1, Date date2) {
        int[] days = new int[2];
        if (date1.compareTo(date2) > 0) {
            Date temp = date1;
            date1 = date2;
            date2 = temp;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        days[0] = getActualMaximum(date1) - c1.get(Calendar.DAY_OF_MONTH);
        days[1] = c2.get(Calendar.DAY_OF_MONTH);

        return days;
    }

    /**
     * 取得两天之间的天数（包括这两天）
     * @param date1
     * @param date2
     * @return
     */
    public static int getDays(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return 0;
        int days = 0;
        days = (int) (Math.abs((date1.getTime() - date2.getTime())) / (60 * 60 * 1000 * 24));
        return days;
    }

    /**
     * yyyyMMddHHmm
     * @param date
     * @return
     */
    public static String getNo(Date date) {
        return formatDate(date, "yyyyMMddHHmm");
    }

    /**
     * yyyyMMddHHmmssSSS
     * @param date
     * @return
     */
    public static String getTimeStr(Date date) {
        return formatDate(date, "yyyyMMddHHmmssSSS");
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat dt = new SimpleDateFormat(format);
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }

    /**
     * 取得当前时间的前hour小时的时间（hour 可以为负数）
     * @param hour
     * @return
     */
    public static String getTime(int hour) {
        return getStrDate(DateHelper.getNextNHour(new Date(), hour));
    }

    /**
     * 取得某一时间的前hour小时的时间（hour 可以为负数）
     * @param hour
     * @return
     */
    public static Date getNextNHour(Date date, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, c.get(Calendar.HOUR) + hour);
        return c.getTime();
    }
    /**
     * 取得某一时间的前hour小时的时间（hour 可以为负数）
     * @param hour
     * @return
     */
    public static String getTime(Date date, int hour) {
        return getStrDate(DateHelper.getNextNHour(date, hour));
    }
    /**
     * 取得一个月的第一天
     * @param date
     * @return
     */
    public static Date getFirstdatOfMonth(Date date) {
        Date bdate = null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        bdate = c.getTime();
        return bdate;
    }
    /**
     * 取得一个月的最后一天
     * @param date
     * @return
     */
    public static Date getLastdatOfMonth(Date date) {
        Date bdate = null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        bdate = c.getTime();
        return bdate;
    }

    public static Date getBeforeYear(Date date) {
        Date bdate = null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
        bdate = c.getTime();
        return bdate;
    }
    public static Date getBeforeMonth(Date date) {
        Date bdate = null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
        bdate = c.getTime();
        return bdate;
    }
    public static Date getTime(String time) {
        SimpleDateFormat dt = new SimpleDateFormat(TIME);
        Date date = null;
        try {
            if (time != null) {
                date = dt.parse(time);
            }
        } catch (ParseException e) {
            log.error("getTime failed.", e);
        }
        return date;
    }
    /**
     * 取得时间　HH:mm:ss
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat(TIME);
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }
    /**
     * 取得某月最大天數
     * @param date
     * @return
     */
    public static int getActualMaximum(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    /**
     * 取得某天的前一天
     * @param date
     * @return
     */
    public static Date getBeforeDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        return c.getTime();
    }
    /**
     * yyyy-MM
     * @param date
     * @return
     */
    public static String getYearMonth(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat(YEARMONTH);
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }
    /**
     * yyyy-MM
     * @param strdate yyyy-MM
     * @return
     */
    public static Date getYearMonth(String strdate) {
        SimpleDateFormat dt = new SimpleDateFormat(YEARMONTH);
        Date date = null;
        try {
            if (strdate != null) {
                date = dt.parse(strdate);
            }
        } catch (ParseException e) {
            log.error("getYearMonth failed.", e);
        }
        return date;
    }
    /**
     * 星期
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        // Map weekmap = new HashMap();
        // weekmap.put("Mon","1");
        // weekmap.put("Tue","2");
        // weekmap.put("Wed","3");
        // weekmap.put("Thu","4");
        // weekmap.put("Fri","5");
        // weekmap.put("Sat","6");
        // weekmap.put("Sun","7");
        SimpleDateFormat dt = new SimpleDateFormat("EEE");
        String weekname = dt.format(date);
        // String week = weekmap.get(dt.format(date)).toString();
        return weekname;
    }

    /**
     *
     * @param strdate yyyy-MM-dd
     * @return
     */
    public static Date getDate(String strdate) {
        SimpleDateFormat dt = new SimpleDateFormat(DATE_NOTIME);
        Date date = null;
        try {
            date = dt.parse(strdate);
        } catch (ParseException e) {
            log.error("getDate failed.", e);
        }
        return date;
    }
    /**
     *
     * @param strdate yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date getFullDate(String strdate) {
        SimpleDateFormat dt = new SimpleDateFormat(DATE);
        Date date = null;
        try {
            date = dt.parse(strdate);
        } catch (ParseException e) {
            log.error("getFullDate failed.", e);
        }
        return date;
    }
    /**
     * 把日期格式化为 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String getStrDate(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat(DATE);
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }

    /**
     * 把日期格式化为 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String getStrAsFileName(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat(TIME_FILENAME);
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }
    /**
     * yyyy-MM-dd日HH:mm
     * @param date
     * @return
     */
    public static String getFullDate(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat(DATE_NOS);
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }
    /**
     * yyyy-MM-dd HH:mm
     * @param date
     * @return
     */
    public static String getDate(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String _date = "";
        if (date != null)
            _date = dt.format(date);
        return _date;
    }
    /**
     * 设置日期 小时 分钟
     * @param date
     * @param hour
     * @param minute
     * @return
     */
    public static Date setTime(Date date, String hour, String minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        c.set(Calendar.MINUTE, Integer.parseInt(minute));
        return c.getTime();
    }

    /**
     *
     * @param date
     * @param second
     * @return
     */
    public static Date getTime(String date, String second) {
        Date d = parseDate(date);
        long time = d.getTime();
        int i = new Integer(second).intValue();
        time += i * 1000;
        return new Date(time);
    }
    /**
     * 取得日期的日(yyyy-MM-dd)
     * @param date
     * @return
     */
    public static String getDay(Date date) {
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(date);
        String day = "";

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            day = dt.format(date);
        }
        return day;
        // return calendar.get(Calendar.YEAR) + "-" ;
    }

    private static String getSingle(Date date, int x) {
        String r = "";
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            r = calendar.get(x) + "";
        }
        return r;
    }
    /**
     * 取得日期 的小时
     * @param date
     * @return
     */
    public static String getHour(Date date) {
        String hour = "";
        if (date != null) {
            hour = getSingle(date, Calendar.HOUR_OF_DAY);
        }
        return hour;
    }
    /**
     * 日 dd
     * @param date
     * @return
     */
    public static String getOnlyDay(Date date) {
        String day = "";
        if (date != null) {
            day = getSingle(date, Calendar.DAY_OF_MONTH);
        }
        return day;
    }

    /**
     * 年
     * @param date
     * @return
     */
    public static String getYear(Date date) {
        String day = "";
        if (date != null) {
            day = getSingle(date, Calendar.YEAR);
        }
        return day;
    }

    /**
     * 月
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        String day = "";
        if (date != null) {
            day = (Integer.valueOf(getSingle(date, Calendar.MONTH)).toString() + 1) + "";
        }
        return day;
    }

    public static Date setOnlyDay(Date date, String day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        return c.getTime();
    }

    /**
     * 取得日期 的分钟
     * @param date
     * @return
     */
    public static String getMinute(Date date) {
        String hour = "";
        if (date != null) {
            hour = getSingle(date, Calendar.MINUTE);
        }
        return hour;
    }
    public static String getHM(Date date) {
        String hm = "";
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
        if (date != null) {
            hm = dt.format(date);
        }
        return hm;
    }
    public static String getSearchDate(Date date) {
        String _strdate = getStrDate(date);
        // String hm = _strdate.substring(11);
        if (_strdate.substring(11).equals("00:00:00")) {
            _strdate = _strdate.substring(0, 10);
        }
        return _strdate;
    }

    public static Date getYearDate(Date date) {
        Date datex = null;
        String yearstr = DateHelper.getYear(date);
        datex = DateHelper.getDate(yearstr + "-01-01");
        return datex;
    }
    public static Date getMonthDate(Date date) {
        Date datex = null;
        String yearstr = DateHelper.getYear(date);
        String monthstr = DateHelper.getMonth(date);
        datex = DateHelper.getDate(yearstr + "-" + monthstr + "-01");
        return datex;
    }

    public static Date getQuarterDate(Date date) {
        Date datex = null;
        int month = Integer.parseInt(DateHelper.getMonth(date));
        String yearstr = DateHelper.getYear(date);
        if (month >= 1 && month <= 3) {
            datex = DateHelper.getDate(yearstr + "-01-01");
        } else if (month >= 4 && month <= 6) {
            datex = DateHelper.getDate(yearstr + "-04-01");
        } else if (month >= 7 && month <= 9) {
            datex = DateHelper.getDate(yearstr + "-07-01");
        } else if (month >= 10 && month <= 12) {
            datex = DateHelper.getDate(yearstr + "-10-01");
        }
        return datex;
    }

    public static String getQuarerString(Date date) {
        String quarer = null;
        int month = Integer.parseInt(DateHelper.getMonth(date));
        String yearstr = DateHelper.getYear(date);
        if (month >= 1 && month <= 3) {
            quarer = yearstr + "-01";
        } else if (month >= 4 && month <= 6) {
            quarer = yearstr + "-02";
        } else if (month >= 7 && month <= 9) {
            quarer = yearstr + "-03";
        } else if (month >= 10 && month <= 12) {
            quarer = yearstr + "-04";
        }
        return quarer;
    }

    /**
     * 获取某一天开始的时间点，即每天0时
     * @param date2
     * @return
     */
    public static Date getDayStart(Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
    /**
     * 获取某一天结束的时间点，即每天的24时
     * @param date2
     * @return
     */
    public static Date getDayEnd(Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date2);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 29);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
    /**
     * 获取月末时间
     * @param date2
     * @return
     */
    public static Date getMonthEnd(Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date2);
        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month + 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTime().getTime();
        return new Date(time - 1000);
    }
    /**
     * 获取年末时间
     * @param date2
     * @return
     */
    public static Date getYearEnd(Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date2);
        int year = c.get(Calendar.YEAR);
        c.set(Calendar.YEAR, year + 1);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTime().getTime();
        return new Date(time - 1000);
    }

    public static Date getQuarterStart(int year, int quarter) {
        if (quarter >= 4)
            throw new RuntimeException("参数错误");
        int month = quarter * 3;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTime().getTime();
        return new Date(time);

    }

    public static Date getQuarterEnd(int year, int quarter) {
        if (quarter >= 4)
            throw new RuntimeException("参数错误");
        int month = quarter * 3 + 3;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTime().getTime();
        return new Date(time - 1000);
    }

    public static Date getYearStart(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTime().getTime();
        return new Date(time);
    }

    public static Date getYearEnd(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year + 1);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTime().getTime();
        return new Date(time - 1000);
    }
    public static List getDaysBetweenTwoDays(Date d1, Date d2) {
        List days = new ArrayList();
        Date sd = DateHelper.getDayStart(d1);

        Calendar c = Calendar.getInstance();
        c.setTime(sd);
        while (c.getTime().compareTo(d2) <= 0) {
            days.add(c.getTime());
            Calendar cx = Calendar.getInstance();
            cx.setTime(c.getTime());
            cx.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
            c = cx;
        }
        return days;
    }

    public static List getExtraDays(List wdays, List days) {
        List rs = new ArrayList();
        Map mdays = new HashMap(days.size());
        for (int i = 0; i < days.size(); i++) {
            Date d = (Date) days.get(i);
            d = DateHelper.getDayStart(d);
            mdays.put(d, d);
        }
        for (int i = 0; i < wdays.size(); i++) {
            Date d = (Date) wdays.get(i);
            if (mdays.get(DateHelper.getDayStart(d)) == null) {
                rs.add(d);
            }
        }
        return rs;
    }
    /**
     * 获取时间
     * @param year 年
     * @param month 月 从0开始
     * @param day 日
     * @param hour 时
     * @param minute 分
     * @param second 秒
     * @return
     */
    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

    /**
     *
     *获取当前的utc时间，格式为yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     *
     * @Author yangguilong
     * @Date
     */
    public static String getCurrentUTCTime() {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", utc);

        return fdf.format(System.currentTimeMillis());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Date d = new Date();
        // Date d2 = getBeforeMonth(d);

        // System.out.println(getTime(-1));
        // System.out.println(getDay(d2) + "--");
        // Date d3 = getDate("2006-8-16");
        // getDays(getDate(getDay(d2)), getDate(getDay(d3)));

        // String week = getWeek(d);
        // Date datex = getFullDate("2006-6-12 0:0:0");
        // System.out.println(getActualMaximum(datex));
        // String date = getstrDate(getDate("2006-6-12 1:1:1"));
        // String _date = getSearchDate(datex);
        // System.out.println(_date);
        // System.out.println(Calendar.MONDAY);
        // Date date1 = new Date();
        // Date date2 = DateHelper.getNextNDate(date1,1);
        // long minutes = DateHelper.getMinutes(date1, date2);
        System.out.println("|" + getYearDate(new Date()).compareTo(DateHelper.getDate("2007-01-01")));
        System.out.println("|" + getQuarterDate(new Date()).compareTo(DateHelper.getDate("2007-04-01")));
        System.out.println("|" + getMonthDate(new Date()).compareTo(DateHelper.getDate("2007-04-01")));

        Date d1 = DateHelper.parseDate("2008-09-01 08:56:45");
        Date d2 = new Date();
        List days = DateHelper.getDaysBetweenTwoDays(d1, d2);

    }
}
