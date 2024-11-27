package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author tanzhi
 * @title WhiteListPo
 * @projectName bdp-open
 * @date 2019/9/17 19:40
 * @description
 */
@Table(name = "DW_OPEN_WHITE_LIST")
@Data
public class WhiteListPo extends BasePo {

    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(name = "IP")
    private String ip;
    @Column(name = "DATA_ACCESS_APP_ID")
    private Integer dataAccessAppId;
    @Column(name = "DATA_ACCESS_APP_NAME")
    private String dataAccessAppName;
}
