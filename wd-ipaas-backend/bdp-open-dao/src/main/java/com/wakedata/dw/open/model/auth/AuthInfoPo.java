package com.wakedata.dw.open.model.auth;

import com.wakedata.dw.open.model.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 认证信息
 *
 * @author chenshaopeng
 * @date 2021/11/8
 */
@Table(name = "DW_AUTH_INFO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthInfoPo extends BasePo {

    /**
     * 主键
     */
    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空")
    @Column(name = "APP_NAME")
    private String appName;

    /**
     * 应用logo
     */
    @Column(name = "APP_LOGO")
    private String appLogo;

    /**
     * 应用类型
     */
    @NotNull(message = "类型不能为空")
    @Column(name = "APP_TYPE")
    private String appType;

    /**
     * 配置-json
     */
    @NotBlank(message = "配置不能为空")
    @Column(name = "CONFIGS")
    private String configs;

    /**
     * 认证信息-json
     */
    @Column(name = "AUTHORIZATIONAPI")
    private String authorizationApi;

    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空")
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 创建人名称
     */
    @Column(name = "CREATE_USER")
    private String createUser;

    /**
     * 已授权API数
     */
    private Integer apiNum;

    /**
     * 租户ID，因为该类无法继承BaseLesseePo类，所以单独在此类上加上租户ID
     */
    @Column(name = "lessee_id")
    private Long lesseeId;

}
