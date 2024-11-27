package com.wakedata.dw.open.model.auth;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/15 16:07
 */
@Data
@Table(name = "DW_AUTH_COLLECT")
public class AuthAuthorizationPo extends BasePo implements Serializable {
    private static final long serialVersionUID = 5399807375305474685L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 第三方应用id
     */
    @Column(name = "DW_AUTH_INFO_ID")
    private Integer authInfoId;
    /**
     * apiId
     */
    @Column(name = "data_asset_api_id")
    private Integer apiId;
    /**
     * 创建人
     */
    @Column(name = "CREATE_BY")
    private String createBy;
}
