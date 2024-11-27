package com.wakedata.dw.open.model.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author tanzhi
 * @title DwOpenUserDo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
public class DwOpenUserDo {
    private Integer id;

    private String user;

    private Integer roleId;

    private Date createTime;

    private Date updateTime;
}
