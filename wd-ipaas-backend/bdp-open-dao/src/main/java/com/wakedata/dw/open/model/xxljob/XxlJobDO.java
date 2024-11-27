package com.wakedata.dw.open.model.xxljob;

import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.parammapping.XxlJobTaskParamMapping;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobDO
 * @date 2022/10/25 11:57
 */
@Table(name = "dw_open_xxl_job_info")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XxlJobDO extends BasePo implements Serializable {

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
     * api请求头参数
     */
    private String apiHeadParam;

    /**
     * apiQuery参数
     */
    private String apiQueryParam;

    /**
     * api请求体
     */
    private String apiBodyParam;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务状态：1 启用  2 禁用
     */
    private Integer taskType;

    /**
     * 任务执行类型：1 永久 2 自定义
     */
    private Integer taskExecuteType;

    /**
     * 自定义起始时间
     */
    private Date taskStartTime;

    /**
     * 自定义结束时间
     */
    private Date taskEndTime;

    /**
     * 执行次数
     */
    private Long taskExecuteAmount;

    /**
     * 定时任务表达式
     */
    private String taskCron;

    /**
     * 定时任务描述
     */
    private String taskDesc;

    /**
     * 租户id
     */
    private Long lesseeId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * xxlJob唯一标识：时间戳
     */
    private String xxlTimeStamp;

    /**
     * 执行时间
     */
    private Date executeTime;

    @Transient
    private List<XxlJobTaskParamMapping> apiHeadParams;

    @Transient
    private List<XxlJobTaskParamMapping> apiQueryParams;

    @Transient
    private XxlJobTaskParamMapping apiBodyParams;

    @Transient
    private String dataAssetAppName;
}
