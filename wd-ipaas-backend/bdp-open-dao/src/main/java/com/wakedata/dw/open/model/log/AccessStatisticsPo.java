package com.wakedata.dw.open.model.log;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tanzhi
 * @title AccessStatisticsPo
 * @projectName bdp-open
 * @date 2019/8/23 16:05
 * @description
 */
@Data
@Table(name = "DW_OPEN_ACCESS_STATISTICS")
public class AccessStatisticsPo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "STAT_TYPE")
    private Integer statType;
    @Column(name = "RECORD_DATA")
    private Integer  recordData;
    @Column(name = "DATA_TIME")
    private Date dataTime;
    @Column(name = "STAT_TIME")
    private Date statTime;
    @Column(name = "RESULT")
    private Integer result;
    @Column(name = "RECORD_ENTITY_ID")
    private Integer recordEntityId;
    @Column(name = "LESSEE_ID")
    private Long lesseeId;
    @Column(name = "data_access_app_id")
    private Integer accessAppId;

}
