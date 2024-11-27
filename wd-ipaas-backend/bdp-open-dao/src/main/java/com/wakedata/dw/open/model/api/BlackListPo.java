package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author liuzheng
 * @title BlackListPo
 * @date 2021/4/6 14:50
 * @projectName bdp-open
 * @description 黑名单Po
 */
@Table(name = "DW_OPEN_BLACK_LIST")
@Data
public class BlackListPo extends BasePo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Id
    private Integer id;
    @Column(name = "IP")
    private String ip;
    @Column(name = "DATA_ACCESS_APP_ID")
    private Integer dataAccessAppId;
    @Column(name = "DATA_ACCESS_APP_NAME")
    private String dataAccessAppName;

}
