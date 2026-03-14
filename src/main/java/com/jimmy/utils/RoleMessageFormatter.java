package com.jimmy.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class RoleMessageFormatter {
    private static final int DISPLAY_LIMIT = 3;

    // 使用 {0}, {1} 这种标准占位符
    private static final String PATTERN_FULL = "该角色已被 {0} 使用，无法删除。";
    private static final String PATTERN_PARTIAL = "该角色已被 {0} 等 {1} 位用户使用，无法删除。";

    public String format(List<String> userNames) {
        int total = userNames.size();
        String sep = "、";

        if (total <= DISPLAY_LIMIT) {
            String usersStr = String.join(sep, userNames);
            return MessageFormat.format(PATTERN_FULL, usersStr);
        } else {
            List<String> showNames = new ArrayList<>(userNames.subList(0, DISPLAY_LIMIT));
            String usersStr = String.join(sep, showNames);
            return MessageFormat.format(PATTERN_PARTIAL, usersStr, total);
        }
    }
}
