package com.wakedata.dw.lowcode.model;

import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 区块(LowCodeBlockPo)实体类
 *
 * @author wanghu
 * @since 2021-11-24 12:02:15
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_lowcode_block")
public class LowCodeBlockPo extends AppPo {

    /**
     * 区块名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 区块内容, JSON 字符串
     */
    @Column(name = "dsl")
    private String dsl;

    /**
     * 截图
     */
    @Column(name = "snapshot")
    private String snapshot;

}

