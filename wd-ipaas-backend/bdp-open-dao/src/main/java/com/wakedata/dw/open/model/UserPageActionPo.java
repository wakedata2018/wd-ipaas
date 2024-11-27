package com.wakedata.dw.open.model;

import com.wakedata.dw.open.enums.AuditLogResultEnum;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author yiyufeng
 * @title UserPageActionPo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
@Table(name = "dw_open_user_page_action")
public class UserPageActionPo extends BaseLesseePo {

    /**
     * 主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 页面资源标识
     */
    @NotBlank(message = "页面资源标识不能为空")
    @Column(name = "page_resource")
    private String pageResource;

    /**
     * 页面事件标识
     */
    @NotBlank(message = "页面事件标识不能为空")
    @Column(name = "page_event")
    private String pageEvent;

    /**
     * 行为用户标识(当前操作的用户)
     */
    @NotBlank(message = "行为用户标识不能为空")
    @Column(name = "action_user")
    private String actionUser;

    /**
     * 访问时间
     */
    @Column(name = "action_time")
    private Date actionTime;

    /**
     * 时间戳
     */
    @Column(name = "time_stamp")
    private BigInteger timeStamp;

    /**
     * ip地址
     */
    @Column(name = "ip")
    private String ip;

    /**
     * 请求的接口地址
     */
    @Column(name = "request_url")
    private String requestUrl;

    /**
     * 操作结果
     */
    @Column(name = "operating_result")
    private AuditLogResultEnum operatingResult;

    /**
     * 请求参数
     */
    @Column(name = "request_params")
    private String requestParams;

    /**
     * 接口返回结果
     */
    @Column(name = "response_params")
    private String responseParams;
}
