package com.kristain.common.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kristain on 15/12/17.
 */
public class DateTimeUtils {


    /** 日期格式：yyyy-MM-dd HH:mm:ss **/
    public static final String DF_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /** 日期格式：yyyy-MM-dd HH:mm **/
    public static final String DF_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /** 日期格式：yyyy-MM-dd **/
    public static final String DF_YYYY_MM_DD = "yyyy-MM-dd";

    /** 日期格式：HH:mm:ss **/
    public static final String DF_HH_MM_SS = "HH:mm:ss";

    /** 日期格式：HH:mm **/
    public static final String DF_HH_MM = "HH:mm";

    /** 日期格式：yyyy.MM.dd **/
    public static final String YYYY_MM_DD = "yyyy.MM.dd";
    public static final String YY_MM_DD = "yy.MM.dd";

    public static final String YYYYMM = "yyyyMM";
    public static final String YYYY_MM_DD_HH_MM="yyyy.MM.dd HH:mm";
    public static final String YYYY = "yyyy";
    public static final String MM="MM";

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /** Log输出标识 **/
    private static final String TAG = DateTimeUtils.class.getSimpleName();

    /**
     * 将日期格式化成友好的字符串：几分钟前、几小时前、几天前、几月前、几年前、刚刚
     *
     * @param date
     * @return
     */
    public static String formatFriendly(Date date) {
        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    /**
     * 获取离月底还有几天
     * @return
     */
    public static int getDayOfMonth(){
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day_of_month = aCalendar.get(Calendar.DAY_OF_MONTH);
        int day=aCalendar.getActualMaximum(Calendar.DATE);
        return day-day_of_month+1;
    }


    /**
     * 将日期格式化成友好的字符串：几分钟前、几小时前、几天前、几月前、几年前、刚刚
     *
     * @param date
     * @return
     */
    public static String formatFriendly(long date) {
        if (date == 0) {
            return null;
        }
        long diff = new Date().getTime() - date;
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            diff= diff -r*year;
            long m=0;
            if (diff > month) {
                m = (diff / month);
                return r + "年" +m +"个月";
            }
            return r + "年";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天";
        }
        return "刚刚";
    }

    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     *
     * @param dateL
     *            日期
     * @return
     */
    public static String formatDateTime(long dateL) {
        SimpleDateFormat sdf = new SimpleDateFormat(DF_YYYY_MM_DD_HH_MM_SS);
        Date date = new Date(dateL);
        return sdf.format(date);
    }


    /**
     * 根据毫秒转日期
     * @param dateL
     * @return
     */
    public static String formatDateByMill(long dateL){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(new Date(dateL*1000));
    }

    /**
     * 根据毫秒转日期
     * @param dateL
     * @return
     */
    public static String formatDateByMill(long dateL,String fomate){
        SimpleDateFormat df = new SimpleDateFormat(fomate);
        return df.format(new Date(dateL));
    }


    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     *
     * @param dateL
     *            日期
     * @param formater
     *
     * @return
     */
    public static String formatDateTime(long dateL, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        return sdf.format(new Date(dateL));
    }

    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式化
     * @param date 日期
     * @param formater
     * @return
     */
    public static String formatDateTime(Date date, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        return sdf.format(date);
    }

    /**
     * 将日期字符串转成日期
     *
     * @param strDate
     *            字符串日期
     * @return java.util.date日期类型
     */
    public static Date parseDate(String strDate) {
        DateFormat dateFormat = new SimpleDateFormat(DF_YYYY_MM_DD_HH_MM_SS);
        Date returnDate = null;
        try {
            returnDate = dateFormat.parse(strDate);
        } catch (ParseException e) {
            Log.v(TAG, "parseDate failed !");

        }
        return returnDate;

    }

    /**
     * 将日期字符串转成日期
     *
     * @param strDate
     *            字符串日期
     * @return java.util.date日期类型
     */
    public static Date parseDate(String strDate,String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date returnDate = null;
        try {
            returnDate = dateFormat.parse(strDate);
        } catch (ParseException e) {
            Log.v(TAG, "parseDate failed !");

        }
        return returnDate;

    }

    /**
     * 获取系统当前日期
     *
     * @return
     */
    public static Date gainCurrentDate() {
        return new Date();
    }

    /**
     * 获取系统当前日期
     *
     * @return
     */
    public static String gainCurrentDate(String formater) {
        return formatDateTime(new Date(), formater);
    }

    /**
     * 获取上一天
     * @param date
     * @return
     */
    public static String getLastDate(String date){
        return formatDateTime(subDateTime(parseDate(date, DF_YYYY_MM_DD), 24), DF_YYYY_MM_DD);
    }

    /**
     * 获取下一天
     * @param date
     * @return
     */
    public static String getNextDate(String date){
        return formatDateTime(addDateTime(parseDate(date, DF_YYYY_MM_DD), 24), DF_YYYY_MM_DD);
    }

    /**
     * 获取系统当前月份
     * @return
     */
    public static String gainCurrentMonth(){
        return formatDateTime(new Date(), YYYYMM);
    }

    public static String gainCurrentMonth(int position){
        String year  = formatDateTime(new Date(), YYYY);
        if(position<10){
            return year+"0"+position;
        }
        return year+position;
    }

    /**
     * 获取上一月份日期
     * @param date
     * @return
     */
    public static String getLastMonth(String date){
        String year =date.substring(0, 4);
        String month = date.substring(4);
        if("01".equals(month)){
            return (Integer.parseInt(year)-1)+"12";
        }
        if("10".equals(month)){
            return year+"09";
        }
        if("11".equals(month)){
            return year+"10";
        }
        if("12".equals(month)){
            return year+"11";
        }
        return year+"0"+ (Integer.parseInt(month)-1);
    }

    /**
     * 格式化月份
     * @param month
     * @return
     */
    public static String formatMonth(int month){
        if(month<10){
            return "0"+month;
        }
        return month+"";
    }

    /**
     * 格式化日期
     * @param day
     * @return
     */
    public static String formatDay(int day){
        if(day<10){
            return "0"+day;
        }
        return day+"";
    }

    /**
     * 获取下一月份日期
     * @param date
     * @return
     */
    public static String getNextMonth(String date){
        String year =date.substring(0,4);
        String month = date.substring(4);
        if("12".equals(month)){
            return (Integer.parseInt(year)+1)+"01";
        }
        if("09".equals(month)){
            return year+"10";
        }
        if("10".equals(month)||"11".equals(month)){
            return year+ (Integer.parseInt(month)+1);
        }
        return year+"0"+ (Integer.parseInt(month)+1);
    }



    /**
     * 验证日期是否比当前日期早
     *
     * @param target1
     *            比较时间1
     * @param target2
     *            比较时间2
     * @return true 则代表target1比target2晚或等于target2，否则比target2早
     */
    public static boolean compareDate(Date target1, Date target2) {
        boolean flag = false;
        try {
            String target1DateTime = DateTimeUtils.formatDateTime(target1,
                    DF_YYYY_MM_DD_HH_MM_SS);
            String target2DateTime = DateTimeUtils.formatDateTime(target2,
                    DF_YYYY_MM_DD_HH_MM_SS);
            if (target1DateTime.compareTo(target2DateTime) <= 0) {
                flag = true;
            }
        } catch (Exception e1) {
            System.out.println("比较失败，原因：" + e1.getMessage());
        }
        return flag;
    }

    /**
     * 对日期进行增加操作
     *
     * @param target
     *            需要进行运算的日期
     * @param hour
     *            小时
     * @return
     */
    public static Date addDateTime(Date target, double hour) {
        if (null == target || hour < 0) {
            return target;
        }

        return new Date(target.getTime() + (long) (hour * 60 * 60 * 1000));
    }

    /**
     * 对日期进行相减操作
     *
     * @param target
     *            需要进行运算的日期
     * @param hour
     *            小时
     * @return
     */
    public static Date subDateTime(Date target, double hour) {
        if (null == target || hour < 0) {
            return target;
        }

        return new Date(target.getTime() - (long) (hour * 60 * 60 * 1000));
    }

}
