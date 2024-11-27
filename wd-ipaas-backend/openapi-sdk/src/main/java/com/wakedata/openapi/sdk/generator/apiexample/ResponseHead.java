package com.wakedata.openapi.sdk.generator.apiexample;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * api响应 head参数
 * @author luomeng
 * @date 2022/10/26 14:52
 */
@Data
@NoArgsConstructor
public class ResponseHead implements Serializable {

    private String id;
}
