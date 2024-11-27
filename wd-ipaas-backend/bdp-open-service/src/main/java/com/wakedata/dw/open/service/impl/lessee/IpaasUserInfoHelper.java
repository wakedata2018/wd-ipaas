package com.wakedata.dw.open.service.impl.lessee;


import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author luomeng
 * @date 2022/8/4 17:32
 */
public class IpaasUserInfoHelper {

    /**
     * 获取登录sessionid
     * @param request
     * @return
     */
    public static String getIpaasCookieId(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if(ObjectUtil.isEmpty(cookies)){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(DwOpenConstant.COOKIE_IPAAS_KEY.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 获取登录用户缓存key
     * @param sessionId
     * @return
     */
    public static String getIpaasUserInfoRedisKey(String sessionId){
        return String.format(DwOpenConstant.IPAAS_USER_INFO,sessionId);
    }

    /**
     * 刷新缓存时间
     * @param sessionId
     */
    public static void refreshIpaasUserRedisExpireTime(String sessionId){
        RedisUtil.getInstance().expire(getIpaasUserInfoRedisKey(sessionId),DwOpenConstant.IPAAS_USERINFO_EXPIRETIME);
    }

}
