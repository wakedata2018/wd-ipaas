package com.wakedata.dw.open.connector.dto.ew;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wujunqiang
 * @since 2022/11/18 15:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetTokenResponse extends EnterpriseWeChatApiResponse {

    /**
     * token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 凭证的有效时间（秒）
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

}
