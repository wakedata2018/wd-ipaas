package com.wakedata.dw.open.model.log;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author tanzhi
 * @title OperatorLogPo
 * @projectName bdp-open
 * @date 2019/10/9 17:47
 * @description
 */
@Data
@Table(name = "DW_OPEN_OPERATOR_LOG")
public class OperatorLogPo extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CLASS_NAME")
    private String className;
    @Column(name = "METHOD_NAME")
    private String methodName;
    @Column(name = "USER_IDENTIFICATION")
    private String userIdentification;
    @Column(name = "PARAMETER")
    private String parameter;
    @Column(name = "RESPONSE")
    private String response;
}
