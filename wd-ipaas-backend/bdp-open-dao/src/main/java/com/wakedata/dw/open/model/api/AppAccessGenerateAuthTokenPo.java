package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BaseLesseePo;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 开放平台生成的授权访问api令牌-1、开放平台应用访问api前生成的accessToken
 * @author luomeng
 * @date 2022/8/6 15:19
 */
@Table(name="dw_open_api_generate_auth_token")
@Data
public class AppAccessGenerateAuthTokenPo extends BaseLesseePo {

    /** ID */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    /** 接入方应用id */
    private Integer dataAccessAppId ;
    /** 生成的token令牌 */
    private String accessToken ;
    /** token有效期 */
    private Date tokenExpireTime ;
    /** 刷新token的令牌 */
    private String refreshToken ;
    /** 刷新令牌有效期 */
    private Date refreshTokenExpireTime ;
    /** 刷新次数 */
    private Integer refreshNum ;
    /** 状态（有效、作废） */
    private Integer status ;
    /** 创建人 */
    private String createBy ;
    /** 更新人 */
    private String updateBy ;

}
