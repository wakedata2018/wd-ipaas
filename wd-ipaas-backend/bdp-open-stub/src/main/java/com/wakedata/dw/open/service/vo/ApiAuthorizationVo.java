package com.wakedata.dw.open.service.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/17 14:56
 */
@Data
public class ApiAuthorizationVo {
    /**
     * apiId
     */
    private Integer apiId;
    /**
     * 步骤中文名称
     */
    private String stepChineseName;
    /**
     * 当前apiId已授权的外部应用
     * */
    private List<AuthInfoVo> authInfoVos;
}
