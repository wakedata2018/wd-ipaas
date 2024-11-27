package com.wakedata.dw.open.model.api.flow;

import lombok.Data;

import javax.persistence.*;

/**
 * 用于服务编排关联API引用关系，防止被引用的API下线，删除等
 *
 * @author ZhangXueJun
 * @title ApiGraph
 * @date 2021/3/22 10:46
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "dw_open_api_relation")
public class ApiFlowRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "api_id")
    private Integer apiId;

    @Column(name = "graph_id")
    private Integer graphId;
}
