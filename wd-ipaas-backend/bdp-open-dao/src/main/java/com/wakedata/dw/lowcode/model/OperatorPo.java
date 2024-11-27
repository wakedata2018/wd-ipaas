package com.wakedata.dw.lowcode.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wakedata.dw.open.model.BasePo;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作人信息和主键
 *
 * @author wanghu
 * @date 2021-11-24 12:02:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OperatorPo extends BasePo {

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
     *
     */
    @Column(name = "lessee_id")
    @JsonFormat
    private Long lesseeId;
}
