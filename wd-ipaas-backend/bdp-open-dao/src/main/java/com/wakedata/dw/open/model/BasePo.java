package com.wakedata.dw.open.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * @author yiyufeng
 * @title BasePo
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
public class BasePo {

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;
    @Transient
    public static <T extends BasePo> List<T> setAllTime(List<T> list) {
        Date date = new Date();
        for (BasePo po : list) {
            po.setCreateTime(date);
            po.setUpdateTime(date);
        }
        return list;
    }
}