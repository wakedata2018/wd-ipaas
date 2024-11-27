package com.wakedata.dw.open.model.xxljob;

import com.wakedata.dw.open.model.BasePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobInvokeLog
 * @date 2022/11/2 16:39
 */
@Table(name = "dw_open_xxl_job_invoke_log")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XxlJobInvokeLogDO extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty("主键")
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 接入应用的id
     */
    private Integer dataAssetAppId;

    /**
     * 数据apiId
     */
    private Integer dataAssetApiId;

    /**
     * 接口分组id
     */
    private Integer apiGroupId;

    /**
     * 解析之后的HEAD
     */
    private String apiHeadParam;

    /**
     * 解析之后的QUERY
     */
    private String apiQueryParam;

    /**
     * 解析之后的BODY
     */
    private String apiBodyParam;

    /**
     * 执行状态
     */
    private Integer invokeStatus;

    /**
     * 执行结果(包含正确执行结果以及错误详情)
     */
    private String invokeResult;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

}
