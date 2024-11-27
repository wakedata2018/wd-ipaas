package com.wakedata.dw.open.connector.dto.wakecloud;

import lombok.Data;

import java.io.Serializable;

/**
 * 惟客云登陆接口响应对象
 *
 * @author wujunqiang
 * @since 2022/12/1 17:29
 */
@Data
public class WakeCloudLoginResponse implements Serializable {

    /**
     * 用户管理类型，0-集团 1-业态
     */
    private Integer accountType;

    /**
     * domainID
     */
    private Long domainId;

    /**
     * 統一用戶ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 微信ID
     */
    private String wxOpenId;

}
