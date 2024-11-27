package com.wakedata.openapi.sdk.accesstoken;

import lombok.Data;

import java.io.Serializable;

/**
 * token返回对象
 * @author luomeng
 * @date 2022/8/24 10:53
 */
@Data
public class AccessToken implements Serializable {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * refreshToken有效期为30天，可⽤来刷新access_token
     */
    private String refreshToken;

    /**
     * access_token过期时间，单位秒
     */
    private Integer expireIn;

}
