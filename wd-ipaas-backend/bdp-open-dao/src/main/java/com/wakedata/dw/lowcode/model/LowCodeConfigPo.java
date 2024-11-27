package com.wakedata.dw.lowcode.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

/**
 * 配置信息 - 实体类
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_lowcode_config")
public class LowCodeConfigPo extends AppPo {

    /**
     * 类型
     */
    private String type;

    /**
     * 内容
     */
    private String content;

}

