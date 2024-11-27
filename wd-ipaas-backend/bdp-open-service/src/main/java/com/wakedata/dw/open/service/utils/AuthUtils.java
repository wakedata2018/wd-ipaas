package com.wakedata.dw.open.service.utils;


import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.BaseLesseePo;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.wd.permission.login.dto.AdminUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author wq
 * @title AuthUtils
 * @date 2020/10/23 15:09
 * @projectName dw-open
 * @description 获取用户信息和租户信息的工具类
 */
@Slf4j
@Component
@Deprecated
public class AuthUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 主题Swagger主题的租户ID
     */
    public static final Long SWAGGER_TENANT_ID = 1L;


    /**
     * 获取用户信息
     *
     * @return AdminUserDTO
     */
    public static AdminUserDTO getCurrentUserInfo() {

        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        if (userInfo == null) {
            throw new OpenException(MsgCodeEnum.s_not_login);
        }

        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setTenantId(userInfo.getLesseeId());
        adminUserDTO.setAccount(userInfo.getUserIdentification());
        return adminUserDTO;
    }

    /**
     * 获取用户租户ID
     *
     * @return 租户ID
     */
    public static Long currentAppId() {
        Long tenantId = getCurrentUserInfo().getTenantId();
        if (tenantId == null) {
            throw new OpenException(MsgCodeEnum.w_user_not_tenantId);
        }
        return tenantId;
    }

    /**
     * 给BaseLesseePo类设置租户ID
     *
     * @param po BaseLesseePo
     */
    public static void setAppId(BaseLesseePo po) {
        if (po.getLesseeId() == null) {
            po.setLesseeId(currentAppId());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AuthUtils.applicationContext = applicationContext;
    }

}
