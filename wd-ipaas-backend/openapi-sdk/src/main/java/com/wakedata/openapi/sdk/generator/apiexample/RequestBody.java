package com.wakedata.openapi.sdk.generator.apiexample;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * api请求body参数
 * @author luomeng
 * @date 2022/10/26 14:30
 */
@Data
@NoArgsConstructor
public class RequestBody implements Serializable {

    private String tenantId;

}
