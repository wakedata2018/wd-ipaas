package com.wakedata.dw.open.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方授权认证数据实体类
 *
 * @author wujunqiang
 * @date 2021/12/13 10:27
 */
@Getter
@ToString
public class AuthParamDTO implements Serializable {

    /**
     * 请求头需要的参数Map
     */
    @JsonProperty("HEAD")
    private final Map<String, Object> headMap;

    /**
     * 请求query需要的参数Map
     */
    @JsonProperty("QUERY")
    private final Map<String, Object> queryMap;

    /**
     * 请求body需要的参数Map
     */
    @JsonProperty("BODY")
    private final Map<String, Object> bodyMap;

    public AuthParamDTO() {
        this.headMap = new HashMap<>(16);
        this.queryMap = new HashMap<>(16);
        this.bodyMap = new HashMap<>(16);
    }

    public void putInHead(String key, Object value) {
        this.headMap.put(key, value);
    }

    public Object getInHead(String key) {
        return this.headMap.get(key);
    }

    public void putInQuery(String key, Object value) {
        this.queryMap.put(key, value);
    }

    public Object getInQuery(String key) {
        return this.queryMap.get(key);
    }

    public void putInBody(String key, Object value) {
        this.bodyMap.put(key, value);
    }

    public Object getInBody(String key) {
        return this.bodyMap.get(key);
    }
}
