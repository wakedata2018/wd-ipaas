package com.wakedata.dw.open.utils.jsqlparser;

import lombok.Data;

import java.io.Serializable;

/**
 * 解析出的sql参数对象
 * @author luomeng
 * @date 2023/2/15 12:45
 */
@Data
public class SqlParameter implements Serializable {

    /**
     * 参数名称
     */
    private String param;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 是否是动态参数
     */
    private Boolean isDynamic;

    /**
     * 所属文本
     */
    private String content;

    public SqlParameter() {
    }

    public SqlParameter(String param, String type, Boolean isDynamic,String content) {
        this.param = param;
        this.type = type;
        this.isDynamic = isDynamic;
        this.content = content;
    }
}


