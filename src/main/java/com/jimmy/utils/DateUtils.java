package com.jimmy.utils;

import lombok.extern.slf4j.Slf4j;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具类
 */
@Slf4j
public class DateUtils {

    /** 日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 日期格式：yyyy-MM-dd */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** 时间格式：HH:mm:ss */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /** 日期时间格式：yyyy/MM/dd HH:mm:ss */
    public static final String DATETIME_FORMAT_SLASH = "yyyy/MM/dd HH:mm:ss";

    /** 日期格式：yyyy/MM/dd */
    public static final String DATE_FORMAT_SLASH = "yyyy/MM/dd";

    /** 日期时间格式：yyyyMMddHHmmss */
    public static final String DATETIME_FORMAT_COMPACT = "yyyyMMddHHmmss";

    /** 日期格式：yyyyMMdd */
    public static final String DATE_FORMAT_COMPACT = "yyyyMMdd";

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static long currentSecond() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前日期时间字符串
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String now() {
        return format(LocalDateTime.now(), DATETIME_FORMAT);
    }

    /**
     * 获取当前日期字符串
     * @return yyyy-MM-dd
     */
    public static String nowDate() {
        return format(LocalDateTime.now(), DATE_FORMAT);
    }

    /**
     * 获取格式化后的当前日期时间
     * @param pattern 格式化模式
     * @return 格式化后的字符串
     */
    public static String now(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    /**
     * 格式化日期时间
     * @param dateTime 日期时间
     * @param pattern 格式化模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (Exception e) {
            log.error("日期格式化失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化日期
     * @param date 日期
     * @param pattern 格式化模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return date.format(formatter);
        } catch (Exception e) {
            log.error("日期格式化失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化 Date 对象
     * @param date Date 对象
     * @param pattern 格式化模式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);

    }

    /**
     * 格式化时间戳
     * @param timestamp 时间戳（毫秒）
     * @param pattern 格式化模式
     * @return 格式化后的字符串
     */
    public static String format(long timestamp, String pattern) {
        return format(new Date(timestamp), pattern);
    }

    /**
     * 解析日期时间字符串
     * @param dateTimeStr 日期时间字符串
     * @param pattern 格式化模式
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || pattern == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.error("日期时间解析失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析日期字符串为 Date 对象
     * @param dateStr 日期字符串
     * @param pattern 格式化模式
     * @return Date
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || pattern == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("日期解析失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 计算两个日期之间的天数差
     * @param start 开始日期
     * @param end 结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的小时差
     * @param start 开始时间
     * @param end 结束时间
     * @return 小时差
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 日期加减天数
     * @param date 日期
     * @param days 天数（正数为加，负数为减）
     * @return 新的日期
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    /**
     * 日期时间加减天数
     * @param dateTime 日期时间
     * @param days 天数（正数为加，负数为减）
     * @return 新的日期时间
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }

    /**
     * 日期时间加减小时
     * @param dateTime 日期时间
     * @param hours 小时数（正数为加，负数为减）
     * @return new的日期时间
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    /**
     * 获取当天的开始时间（00:00:00）
     * @param date 日期
     * @return 当天的开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * 获取当天的结束时间（23:59:59）
     * @param date 日期
     * @return 当天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59);
    }

    /**
     * 获取当天的开始时间（00:00:00）
     * @return 当天的开始时间
     */
    public static LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 获取当天的结束时间（23:59:59）
     * @return 当天的结束时间
     */
    public static LocalDateTime endOfToday() {
        return LocalDate.now().atTime(23, 59, 59);
    }

    /**
     * 获取本周的第一天（周一）
     * @param date 日期
     * @return 周一的日期
     */
    public static LocalDate startOfWeek(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.minusDays(date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

    /**
     * 获取本周的最后一天（周日）
     * @param date 日期
     * @return 周日的日期
     */
    public static LocalDate endOfWeek(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.plusDays(DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue());
    }

    /**
     * 获取本月的第一天
     * @param date 日期
     * @return 本月的第一天
     */
    public static LocalDate startOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(1);
    }

    /**
     * 获取本月的最后一天
     * @param date 日期
     * @return 本月的最后一天
     */
    public static LocalDate endOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * 判断是否是同一天
     * @param date1 日期1
     * @param date2 日期2
     * @return true 是同一天，false 不是同一天
     */
    public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.toLocalDate().isEqual(date2.toLocalDate());
    }

    /**
     * 判断是否是同一天
     * @param date1 日期1
     * @param date2 日期2
     * @return true 是同一天，false 不是同一天
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isEqual(date2);
    }

    /**
     * 判断是否是今天
     * @param date 日期
     * @return true 是今天，false 不是今天
     */
    public static boolean isToday(LocalDateTime date) {
        if (date == null) {
            return false;
        }
        return date.toLocalDate().isEqual(LocalDate.now());
    }

    /**
     * 判断是否是今天
     * @param date 日期
     * @return true 是今天，false 不是今天
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isEqual(LocalDate.now());
    }

    /**
     * 比较日期
     * @param date1 日期1
     * @param date2 日期2
     * @return 0 相等，-1 date1 < date2，1 date1 > date2
     */
    public static int compare(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) {
            return 0;
        }
        if (date1 == null) {
            return -1;
        }
        if (date2 == null) {
            return 1;
        }
        return date1.compareTo(date2);
    }

    /**
     * 比较日期时间
     * @param dateTime1 日期时间1
     * @param dateTime2 日期时间2
     * @return 0 相等，-1 dateTime1 < dateTime2，1 dateTime1 > dateTime2
     */
    public static int compare(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null && dateTime2 == null) {
            return 0;
        }
        if (dateTime1 == null) {
            return -1;
        }
        if (dateTime2 == null) {
            return 1;
        }
        return dateTime1.compareTo(dateTime2);
    }

    /**
     * 获取某月的天数
     * @param year 年份
     * @param month 月份（1-12）
     * @return 天数
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断是否是闰年
     * @param year 年份
     * @return true 是闰年，false 不是闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}