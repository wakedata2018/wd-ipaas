package com.wakedata.dw.open.model.api.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.MapOperatorJsonDeserializer;
import com.wakedata.dw.open.utils.JsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.Map;

/**
 * @author ZhangXueJun
 * @title ApiGraph
 * @date 2021/3/22 10:46
 * @projectName dw-open
 * @description
 */
@Slf4j
@Data
@Table(name = "dw_open_api_graph_attr")
@ApiModel("流程编排图信息")
public class ApiFlowAttr extends AbstractApiAttr {

    @Column(name = "dag_json")
    @JsonIgnore
    @ApiModelProperty("算子逻辑视图：组件、算子、边")
    private String dagJson;

    @Column(name = "location_json")
    @ApiModelProperty("算子物理视图：组件，算子等位置信息")
    private String locationJson;

    /**
     * 流程编排中的节点key->节点信息
     */
    @JsonDeserialize(using = MapOperatorJsonDeserializer.class)
    private Map<String, Operator> operators = Maps.newLinkedHashMap();

    /**
     * 算子id
     */
    @Transient
    @ApiModelProperty("算子id")
    private String operatorId;

    public ApiFlowAttr convert() {
        try {

            ApiFlowAttr apiFlowAttr = JsonUtil.getObjectMapper().readValue(dagJson, ApiFlowAttr.class);

            if (StringUtils.isNotEmpty(locationJson)) {
                apiFlowAttr.setLocationJson(locationJson);
            }

            apiFlowAttr.setId(this.getId());
            apiFlowAttr.setApiId(this.getApiId());
            return apiFlowAttr;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
