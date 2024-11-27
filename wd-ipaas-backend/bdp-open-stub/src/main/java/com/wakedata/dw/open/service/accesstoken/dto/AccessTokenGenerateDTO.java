package com.wakedata.dw.open.service.accesstoken.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: weiliao
 * @Date: 2022-6-9
 * @Description: AccessToken
 */
@Data
public class AccessTokenGenerateDTO implements Serializable {

    private static final long serialVersionUID = 2456064318408314515L;
    
    /**
     * access_token
     */
    private String accessToken;

    /**
     * refresh_token有效期为30天，可⽤来刷新access_token
     */
    private String refreshToken;

    /**
     * access_token过期时间，单位秒
     */
    private Integer expireIn; 
}
