package com.wakedata.dw.lowcode.model;

import com.wakedata.dw.lowcode.common.UniqueId;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

/**
 * 页面基础信息 - 实体类
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_lowcode_page_simple")
public class LowCodePageSimplePo extends AppPo implements UniqueId {

    /**
     * 页面名称
     */
    private String name;

    /**
     * 标题
     */
    private String title;

    /**
     * 页面参数
     */
    private String params;

    /**
     * 封面
     */
    private String cover;

    /**
     * 分类 id
     */
    private Integer categoryId;

    /**
     * 内容或者压缩内容
     */
    @Transient
    private String content;

    /**
     * 页面内容 - 压缩
     */
    @Transient
    private String compressedContent;

}

