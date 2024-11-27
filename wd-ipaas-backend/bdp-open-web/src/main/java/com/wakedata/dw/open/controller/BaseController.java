package com.wakedata.dw.open.controller;

import com.wakedata.dw.open.model.OpenApiOperatorPo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;

import java.util.Objects;

/**
 * 基础控制器，用来设置控制器共有方法和属性
 * @author 佟蕊
 */
public class BaseController {

    protected void setEditUserAndEpId(OpenApiOperatorPo openApiOperatorPo) {
        String loginName = IpaasUserContext.getUserInfo().getUserIdentification();
        if (Objects.isNull(openApiOperatorPo.getId())) {
            openApiOperatorPo.setCreateBy(loginName);
        }
        openApiOperatorPo.setUpdateBy(loginName);
        openApiOperatorPo.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
    }



}
