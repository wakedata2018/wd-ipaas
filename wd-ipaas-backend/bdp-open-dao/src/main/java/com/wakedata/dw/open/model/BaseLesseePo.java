package com.wakedata.dw.open.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author wq
 * @title BaseLesseePo
 * @date 2020/10/23 15:45
 * @projectName dw-open
 * @description
 */
@Data
public class BaseLesseePo extends BasePo {

    @Column(name = "lessee_id")
    @JsonFormat
    private Long lesseeId;

}
