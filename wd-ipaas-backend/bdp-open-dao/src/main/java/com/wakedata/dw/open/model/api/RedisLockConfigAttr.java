package com.wakedata.dw.open.model.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.common.redis.lock.module.LockType;
import com.wakedata.dw.open.enums.RedisLockConfigTypeEnum;
import com.wakedata.dw.open.handler.ListJsonHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * @author wujunqiang
 * @since 2023/2/20 18:59
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "dw_open_redis_lock_config_attr")
@ApiModel("Redis锁配置")
public class RedisLockConfigAttr extends AbstractApiAttr {

    /**
     * 锁的名称
     */
    @ApiModelProperty("锁的名称")
    @Column(name = "lock_name")
    private String lockName;

    /**
     * 锁类型
     */
    @ApiModelProperty("锁类型，Reentrant：可重入锁、Fair：公平锁、Read：读锁、Write：写锁")
    @Column(name = "lock_type")
    private LockType lockType;

    /**
     * 锁前缀
     */
    @ApiModelProperty("锁前缀")
    @Column(name = "key_prefix")
    private String keyPrefix;

    /**
     * 组成锁的请求参数名集合
     */
    @ApiModelProperty("组成锁的请求参数名集合")
    @Column(name = "key_params")
    @ColumnType(typeHandler = ListJsonHandler.class)
    private List<String> keyParams;

    /**
     * 获取锁的最长等待时间，单位秒
     */
    @ApiModelProperty("获取锁的最长等待时间，单位秒")
    @Column(name = "wait_time")
    private Long waitTime;

    /**
     * 上锁后x秒自动解锁
     */
    @ApiModelProperty("上锁后x秒自动解锁")
    @Column(name = "lease_time")
    private Long leaseTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @Column(name = "description")
    private String description;

    /**
     * 租户id
     */
    @ApiModelProperty("租户id")
    @Column(name = "lessee_id")
    private Long lesseeId;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @Column(name = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty("更新人")
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 配置对应类型，0：数据表API、1：SQL API、2：SQL算子
     */
    @ApiModelProperty("配置对应类型，0：数据表API、1：SQL API、2：SQL算子")
    @Column(name = "config_type")
    private RedisLockConfigTypeEnum configType;

    /**
     * 是否启用Redis锁，1：启用、0：禁用{@link com.wakedata.dw.open.enums.ActiveStateEnum}
     */
    @ApiModelProperty("是否启用Redis锁，1：启用、0：禁用")
    @Column(name = "is_enable")
    private Integer enableRedisLock;
}
