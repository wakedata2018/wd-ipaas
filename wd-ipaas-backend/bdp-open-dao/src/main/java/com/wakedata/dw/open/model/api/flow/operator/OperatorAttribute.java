package com.wakedata.dw.open.model.api.flow.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.operator.edge.Edge;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

/**
 * @author ZhangXueJun
 * @title OperatorAttribute
 * @date 2021/5/11 16:06
 * @projectName dw-open
 * @description
 */
@Table(name = "dw_open_api_operator_attribute")
@Data
@Slf4j
public class OperatorAttribute {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 实现类
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 获取算子所属层级
     */
    @Column(name = "layer")
    private String layer;

    /**
     * 默认名称
     */
    @Column(name = "default_name")
    private String defaultName;

    /**
     * 唯一标记
     */
    @Column(name = "unique_name")
    private String uniqueName;

    /**
     * 算子描述
     */
    @Column(name = "description")
    private String desc;

    /**
     * 排序号
     */
    @Column(name = "order_field")
    private Integer order;

    /**
     * 是否启用
     */
    @Column(name = "used")
    @JsonIgnore
    private Boolean use = Boolean.TRUE;

    /**
     * 算子模版
     */
    @Column(name = "template")
    private String template;

    /***********************************通过扫描类获取，无需持久化******************************/

    /**
     * 入边出边类型
     */
    private Set<Edge.Type> typeOfEdge = Sets.newHashSet();
    /***********************************通过扫描类获取，无需持久化******************************/

    /**
     * 数据源
     */
    private Object components;
}