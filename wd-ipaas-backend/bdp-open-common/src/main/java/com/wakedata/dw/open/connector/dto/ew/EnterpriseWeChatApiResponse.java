package com.wakedata.dw.open.connector.dto.ew;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 企业微信API返回结果
 *
 * @author wujunqiang
 * @since 2022/11/18 15:21
 */
@Data
public class EnterpriseWeChatApiResponse implements Serializable {

    @JSONField(name = "errcode")
    @ApiModelProperty(value = "返回码")
    protected Integer errCode;

    @JSONField(name = "errMsg")
    @ApiModelProperty(value = "对返回码的文本描述内容")
    protected String errMsg;

    /**
     * 调用接口是否发生错误，errCode为0表示成功，非0表示调用失败
     *
     * @return true：调用成功、false：调用失败
     */
    public boolean isFail() {
        return !Objects.nonNull(errCode) || errCode != 0;
    }

    public static EnterpriseWeChatApiResponse success() {
        EnterpriseWeChatApiResponse response = new EnterpriseWeChatApiResponse();
        response.setErrCode(0);
        response.setErrMsg("ok");
        return response;
    }

}
