package com.wakedata.dw.open.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * @author tanzhi
 * @title ListAccessLogReq
 * @date 2019/12/10 15:04
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class ListAccessLogReq {
    private String keyword;
    @NotNull(message = "pageNo不能为空")
    private Integer pageNo = 1;
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize = 10;
    @DateTimeFormat
    private String from;
    @DateTimeFormat
    private String to;
}