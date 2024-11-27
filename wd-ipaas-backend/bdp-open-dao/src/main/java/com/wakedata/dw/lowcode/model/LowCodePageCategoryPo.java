package com.wakedata.dw.lowcode.model;

import javax.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

/**
 * 分类信息 - 实体类
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_lowcode_page_category")
public class LowCodePageCategoryPo extends AppPo {

    /**
     * 名称
     */
    @Column(name = "`name`")
    private String name;

}

