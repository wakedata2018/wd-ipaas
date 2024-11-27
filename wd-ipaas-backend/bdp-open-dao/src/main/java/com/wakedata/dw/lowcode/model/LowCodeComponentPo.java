package com.wakedata.dw.lowcode.model;

import com.wakedata.dw.lowcode.common.UniqueId;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 自定义组件(LowCodeComponentPo)实体类
 *
 * @author wanghu
 * @since 2021-11-24 18:44:31
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_lowcode_component")
public class LowCodeComponentPo extends AppPo implements UniqueId {

    /**
     * 标识符, 必须唯一
     */
    @Column(name = "name")
    private String name;
    /**
     * 标题
     */
    @Column(name = "title")
    private String title;
    /**
     * 详细描述
     */
    @Column(name = "`desc`")
    private String desc;
    /**
     * 组件元数据,JSON 字符串
     */
    @Column(name = "meta")
    private String meta;
    /**
     * 图标
     */
    @Column(name = "icon")
    private String icon;
    /**
     * 封面
     */
    @Column(name = "cover")
    private String cover;
    /**
     * 组件详情，JSON 字符串
     */
    @Column(name = "content")
    private String content;

    /**
     * 压缩版本组件详情
     */
    @Column(name = "compressed_content")
    private String compressedContent;

}

