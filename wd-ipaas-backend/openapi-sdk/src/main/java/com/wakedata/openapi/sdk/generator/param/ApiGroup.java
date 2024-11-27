package com.wakedata.openapi.sdk.generator.param;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 接口分组
 * @author luomeng
 * @date 2022/10/25 21:37
 */
@Data
@NoArgsConstructor
public class ApiGroup implements Serializable {

    /**
     * 分组名，首字母要大写
     */
    private String name;

    /**
     * 分组包名
     */
    private String groupPackage;

    /**
     * 分组描述
     */
    private String desc;

    /**
     * api列表
     */
    private List<Api> apiList;

    public ApiGroup(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

}

