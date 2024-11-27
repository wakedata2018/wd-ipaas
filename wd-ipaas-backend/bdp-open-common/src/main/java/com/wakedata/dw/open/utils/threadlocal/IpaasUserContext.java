package com.wakedata.dw.open.utils.threadlocal;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;

/**
 * Ipaas后台用户上下文信息
 * @author luomeng
 * @date 2022/8/4 11:27
 */
public class IpaasUserContext {

    private static final ThreadLocal<IpaasUserInfo> IPAAS_USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public IpaasUserContext() {
    }

    /**
     * 添加
     * @param userInfo
     */
    public static void setUserInfo(IpaasUserInfo userInfo){
        IPAAS_USER_INFO_THREAD_LOCAL.set(userInfo);
    }

    /**
     * 获取
     * @return
     */
    public static IpaasUserInfo getUserInfo() {
        return IPAAS_USER_INFO_THREAD_LOCAL.get();
    }

    /**
     * 删除
     */
    public static void removeUserInfo(){
        IpaasUserInfo userInfo =IPAAS_USER_INFO_THREAD_LOCAL.get();
        if(ObjectUtil.isNotNull(userInfo)) {
            IPAAS_USER_INFO_THREAD_LOCAL.remove();
        }
    }
}
