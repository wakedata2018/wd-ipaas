package com.wakedata.dw.open.service.vo;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/19 14:51
 */
@Data
public class AuthInfoVo extends BasePo {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用logo
     */
    private String appLogo;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 配置-json
     */
    private String configs;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人名称
     */
    private String createUser;

    /**
     * 已授权API数
     */
    private Integer apiNum;

    /**
     * 最后授权时间
     */
    private Date authorizationTime;
}
