package com.wakedata.dw.open.model.openapi;

import com.wakedata.dw.open.model.OpenApiOperatorPo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 应用下授权绑定的关联信息
 * 例如：惟客云应用授权给开放平台后产生的映射数据
 * @author 佟蕊
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_access_auth")
public class DwOpenAccessAuthPo extends OpenApiOperatorPo {

    /**
     * 接入方应用id
     */
    @Column(name = "data_access_app_id")
    private Integer dataAccessAppId;

    /**
     * 授权类型（0：惟客云应用，1：其他）
     * {@link com.wakedata.dw.open.enums.DataAssetEnums.AppAuthType}
     */
    @Column(name = "type")
    private Integer type;
    /**
     * 授权配置，json串形式，如惟客云应用授权{“tenantId”,xxx,"appBuId":xxx}
     */
    @Column(name = "api_auth_config")
    private String apiAuthConfig;

    /**
     * 授权状态（已授权、已解绑）
     */
    @Column(name = "status")
    private Integer status;

}
