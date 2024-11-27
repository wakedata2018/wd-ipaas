package com.wakedata.openapi.sdk.generator.apiexample;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * api请求head参数
 * @author luomeng
 * @date 2022/10/26 14:55
 */
@Data
@NoArgsConstructor
public class RequestHead implements Serializable {

    private Integer id;
}
