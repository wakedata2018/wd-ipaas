package com.wakedata.openapi.sdk.generator.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

/**
 * api属性
 * @author luomeng
 * @date 2022/10/26 19:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiField implements Serializable {

    /**
     * 属性类型
     */
    private String type;

    /**
     * 属性名
     */
    private String name;

    /**
     * 属性描述
     */
    private String desc;

    /**
     * 对象类名，属性名，首字母大写
     */
    private String itemClassName;

    /**
     * 对象内部属性
     */
    private List<ApiField> itemFieldList;


    public ApiField(String type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;
    }

}
