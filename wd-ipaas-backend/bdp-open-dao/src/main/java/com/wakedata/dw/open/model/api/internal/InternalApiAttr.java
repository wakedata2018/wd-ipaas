package com.wakedata.dw.open.model.api.internal;

import com.wakedata.dw.open.model.api.AbstractApiAttr;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author ZhangXueJun
 * @title AbstractInternalApi
 * @date 2021/3/8 14:28
 * @projectName dw-open
 * @description
 */
@Data
public abstract class InternalApiAttr extends AbstractApiAttr {

    @Column(name = "DATASOURCE_CONFIG_ID")
    private Integer dataSourceId;
}