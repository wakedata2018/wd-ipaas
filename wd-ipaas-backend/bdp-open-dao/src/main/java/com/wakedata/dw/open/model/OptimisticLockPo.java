package com.wakedata.dw.open.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author tanzhi
 * @title OptimisticLockPo
 * @projectName bdp-open
 * @date 2019/8/23 10:31
 * @description 乐观锁对象
 */
@Data
@Table(name = "DW_OPEN_OPTIMISTIC_LOCK")
public class OptimisticLockPo extends BasePo {
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "LOCK_NAME")
    private String lockName;
    @Column(name = "IP")
    private String ip;
    @Column(name = "LOCK_STATUS")
    private Short lockStatus;
    @Column(name = "VERSION_NO")
    private Integer versionNo;
}
