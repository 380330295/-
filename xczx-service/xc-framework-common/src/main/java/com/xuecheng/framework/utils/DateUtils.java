package com.xuecheng.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kiku
 * @date 2019/6/9 10:41
 * 日期转换工具类
 */
public class DateUtils {
    /**
     * @param date
     * @param farmat
     * @return 将日期转换为字符串形式
     */
    public static String datetoString(Date date, String farmat){
        SimpleDateFormat dateFormat = new SimpleDateFormat(farmat);
        String dateStr = dateFormat.format(date);
        return dateStr;
    }

    /**
     * @param format
     * @param dateStr
     * @return 将字符串形式日期 转换为日期
     * @throws ParseException
     */
    public static Date stringtoDate(String format, String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = dateFormat.parse(dateStr);
        return date;
    }
}
