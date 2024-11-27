package com.wakedata.dw.open.model.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author wq
 * @title UserActionQueryDo
 * @date 2020/10/15 15:28
 * @projectName dw-open
 * @description
 */
@Data
public class UserActionQueryDo {
    private String operator;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String menu;
    private Long lesseeId;
}
