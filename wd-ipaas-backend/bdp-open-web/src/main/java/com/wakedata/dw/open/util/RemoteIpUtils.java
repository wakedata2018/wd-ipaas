package com.wakedata.dw.open.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wq
 * @title RemoteIpUtils
 * @date 2020/11/11 17:58
 * @projectName dw-open
 * @description
 */
public class RemoteIpUtils {
    public static String realIP(HttpServletRequest request) {
        String xff = request.getHeader("x-forwarded-for");
        if (xff != null) {
            int index = xff.indexOf(',');
            if (index != -1) {
                xff = xff.substring(0, index);
            }
            return xff.trim();
        }
        return request.getRemoteAddr();
    }
}
