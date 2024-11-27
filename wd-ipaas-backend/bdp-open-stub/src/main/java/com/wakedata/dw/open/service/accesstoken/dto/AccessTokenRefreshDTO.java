package com.wakedata.dw.open.service.accesstoken.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: weiliao
 * @Date: 2022-6-9
 * @Description: AccessToken
 */
@Data
public class AccessTokenRefreshDTO implements Serializable {
    
    /**
     * access_token
     */
    private String accessToken;

    /**
     * access_token过期时间，单位秒
     */
    private Integer expireIn; 
}
