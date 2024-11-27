package com.wakedata.dw.open.model.warn;

import com.wakedata.dw.warn.enums.WarnTypeEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wq
 * @title ApiWarnLogPo
 * @date 2020/10/9 16:44
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "DW_OPEN_API_WARN_LOG")
public class ApiWarnLogPo {
    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_asset_api_id")
    private Integer apiId;

    @Column(name = "WARN_USER")
    private String warnUser;

    @Column(name ="WARN_TYPE")
    private WarnTypeEnum warnTypeEnum;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "WARN_CLASSIFY")
    private Integer type;

}
