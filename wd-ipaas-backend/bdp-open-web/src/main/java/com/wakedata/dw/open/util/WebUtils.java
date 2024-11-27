package com.wakedata.dw.open.util;

import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.wd.permission.login.dto.AdminUserDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanzhi
 * @title WebUtils
 * @date 2019/11/27 14:20
 * @projectName bdp-open
 * @descriptor
 */
@Slf4j
@Deprecated
public class WebUtils {

    /**
     * 获取用户信息
     *
     * @return AdminUserDTO
     */
    public static AdminUserDTO getCurrentUserInfo() {
        return AuthUtils.getCurrentUserInfo();
    }

}
