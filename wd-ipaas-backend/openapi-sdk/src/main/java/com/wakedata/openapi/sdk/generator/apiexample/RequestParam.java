package com.wakedata.openapi.sdk.generator.apiexample;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * api请求query参数
 * @author luomeng
 * @date 2022/10/26 14:55
 */
@Data
@NoArgsConstructor
public class RequestParam implements Serializable {

    private String id;

    private Integer it;

    private List<Integer> res;

    private JSONObject body;

    private List<JSONObject> bodyAttr;
}
