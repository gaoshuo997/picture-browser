package com.jimmy.utils;

import java.util.List;

public class UserUtils {
    private static final ThreadLocal<Long> localUserId = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> localActions = new ThreadLocal<>();
    private static final ThreadLocal<String> localUserName = new ThreadLocal<>();
    private static final ThreadLocal<String> localIp = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        localUserId.set(userId);
    }

    public static Long getUserId() {
        return localUserId.get();
    }

    public static Long getSystemUserId() {
        //系统用户，默认ID是1
        return 1L;
    }

    public static List<String> getActions() {
        return localActions.get();
    }

    public static void setActions(List<String> actions) {
        localActions.set(actions);
    }

    public static String getUserName() {
        return localUserName.get();
    }

    public static void setUserName(String userName) {
        localUserName.set(userName);
    }

    public static String getIp() {
        return localIp.get();
    }

    public static void setIp(String ip) {
        localIp.set(ip);
    }

    public static void remove() {
        localUserId.remove();
        localActions.remove();
        localIp.remove();
        localUserName.remove();
    }
}
