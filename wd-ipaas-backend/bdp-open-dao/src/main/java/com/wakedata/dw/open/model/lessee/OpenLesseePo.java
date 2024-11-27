package com.wakedata.dw.open.model.lessee;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * ipaas访问租户配置表-1、主要用来做开放平台内部的数据隔离
 * @author luomeng
 * @date 2022/8/2 17:00
 */
@Data
@Table(name="dw_open_lessee")
public class OpenLesseePo extends BasePo {
    /** ID */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    /** 名称 */
    private String name ;
    /** 可创建应用数 */
    private Integer appNum ;
    /** 关联惟客云租户id */
    private Long tenantId ;
    /** 创建人 */
    private String createBy ;
    /** 更新人 */
    private String updateBy ;
    /**
     * 是否删除 0：否 1：是
     */
    private Integer isDeleted;
}
