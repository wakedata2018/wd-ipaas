package com.wakedata.dw.open.connector.dto.wakecloud;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 惟客云登陆选择租户响应对象
 *
 * @author wujunqiang
 * @since 2022/12/2 14:52
 */
@Data
public class WakeCloudChooseTenantResponse implements Serializable {

    /**
     * 可配置的应用数量
     */
    private Integer businessUnitNum;

    /**
     * 负责人邮箱
     */
    private String directorMail;

    /**
     * 负责人姓名
     */
    private String directorName;

    /**
     * 负责人电话
     */
    private String directorPhone;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 租户id
     */
    private Long id;

    /**
     * 关联组织架构的节点ID
     */
    private Long nodeId;

    /**
     * 租户租期状态 0-已过期 1-未到期
     */
    private Integer tenantExpireStatus;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户状态 0-禁用 1-启用
     */
    private Integer tenantStatus;

    /**
     * 租户类型 0-体验类型 1-付费类型
     */
    private Integer tenantType;

}
