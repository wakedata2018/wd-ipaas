package com.wakedata.dw.open.accesstoken;

import com.wakedata.dw.open.constant.DwOpenConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * token信息
 * @Author: weiliao
 * @Date: 2022-6-9
 * @Description: AccessToken
 */
@Data
public class AccessToken implements Serializable {
    
    private static final long serialVersionUID = -728232269487025113L;
    
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

    /**
     * 生成token，有效期2小时
     */
    public void generate() {
        //生成token
        String token = KeyTokenGenerator.generateToken(128);
        //封装返回值
        this.setAccessToken(DwOpenConstant.ACCESS_TOKEN_PREFIX + token);
        this.setExpireIn(DwOpenConstant.ACCESS_TOKEN_EXPIRE);
        this.setRefreshToken(DwOpenConstant.REFRESH_TOKEN_PREFIX + token);
    }

    /**
     * 刷新token
     */
    public void refresh() {
        //生成token
        String token = KeyTokenGenerator.generateToken(128);
        //封装返回值
        this.setAccessToken(DwOpenConstant.ACCESS_TOKEN_PREFIX + token);
        this.setExpireIn(DwOpenConstant.ACCESS_TOKEN_EXPIRE);
    }

    /**
     * 后台生成测试token，有效期5分钟
     */
    public void generateTestToken(){
        //生成token
        String token = KeyTokenGenerator.generateToken(128);
        //封装返回值
        this.setAccessToken(DwOpenConstant.TEST_ACCESS_TOKEN_PREFIX + token);
        this.setExpireIn(DwOpenConstant.TEST_ACCESS_TOKEN_EXPIRE);
    }
    
}
