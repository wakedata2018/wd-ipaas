package com.wakedata.dw.open.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * OpenApi Module主类
 * @author 佟蕊
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OpenApiOperatorPo extends BasePo {

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 更新人
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 自增ID
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Integer id;

    /**
     * 租户id
     */
    @Column(name = "lessee_id")
    @JsonFormat
    private Long lesseeId;
}
