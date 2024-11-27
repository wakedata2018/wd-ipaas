package com.wakedata.dw.lowcode.model;

import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 账号表(LowCodeAccountPo)实体类
 *
 * @author wanghu
 * @since 2021-11-25 15:27:59
 */
@Getter
@Setter
@ToString
@Table(name = "dw_open_lowcode_account")
public class LowCodeAccountPo extends AppPo {

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 密码
     */
    @Column(name = "pwd")
    private String pwd;

}

