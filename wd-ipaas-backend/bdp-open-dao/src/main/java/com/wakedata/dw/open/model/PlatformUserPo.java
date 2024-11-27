package com.wakedata.dw.open.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author tanzhi
 * @title PlatformUserPo
 * @projectName bdp-open
 * @date 2019/9/10 19:49
 * @description
 */
@Data
@Table(name = "DW_OPEN_USER_INFO")
public class PlatformUserPo {

    @Column(name = "zempl")
    private String zempl;
    /**
     * BIP账号
     */
    @Column(name = "zusrid")
    private String zusrid;
    /**
     * 中文名称
     */
    @Column(name = "znachn")
    private String znachn;
    /**
     * 人员英文名称
     */
    @Column(name = "zgesch")
    private String zgesch;
    /**
     * 核算部门名称
     */
    @Column(name = "zacctgdep")
    private String zacctgdep;
    /**
     * 核算部门编码
     */
    @Column(name = "zacctgdid")
    private String zacctgdid;
    /**
     * 核算公司编码
     */
    @Column(name = "zacctgid")
    private String zacctgid;
    /**
     * 核算组织名称
     */
    @Column(name = "zacctgorg")
    private String zacctgorg;
    /**
     * 预算部门名称
     */
    @Column(name = "zbuddept")
    private String zbuddept;
    /**
     * 预算部门名称
     */
    @Column(name = "")
    private String zbudeptid;
    /**
     * 预算单位编码
     */
    @Column(name = "zbudid")
    private String zbudid;
    /**
     * 预算组织名称
     */
    @Column(name = "zbudorg")
    private String zbudorg;
    /**
     * 岗位编码
     */
    @Column(name = "")
    private String zpostid;
    /**
     * 岗位名称
     */
    @Column(name = "zpostname")
    private String zpostname;
    /**
     * 组织编码
     */
    @Column(name = "zorgid")
    private String zorgid;
    /**
     *
     */
    @Column(name = "zorgname")
    private String zorgname;
    /**
     * 员工号
     */
    @Column(name = "zpernr")
    private String zpernr;

    @Transient
    private boolean hasPermit;


}


