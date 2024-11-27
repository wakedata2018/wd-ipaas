package com.wakedata.dw.open.connector.dto.wakecloud;

import lombok.Data;

import java.io.Serializable;

/**
 * 惟客云API返回结果
 *
 * @author wujunqiang
 * @since 2022/12/1 17:36
 */
@Data
public class WakeCloudApiResponse<T> implements Serializable {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 成功标识
     */
    private Boolean success;

    /**
     * 响应数据
     */
    private T data;

}
