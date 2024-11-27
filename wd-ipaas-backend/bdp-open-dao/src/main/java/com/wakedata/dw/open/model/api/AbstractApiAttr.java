package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author ZhangXueJun
 * @title ExternalApi
 * @date 2021/3/1 15:33
 * @projectName dw-open
 * @description
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,  property = "clazzName")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApiFlowAttr.class, name = "ApiFlowAttr"),
        @JsonSubTypes.Type(value = ApiRuleAttr.class, name = "ApiRuleAttr"),
        @JsonSubTypes.Type(value = HttpExternalApiAttr.class, name = "HttpExternalApiAttr"),
        @JsonSubTypes.Type(value = EventReceiveApiAttr.class, name = "EventReceiveApiAttr"),
        @JsonSubTypes.Type(value = EventSendApiAttr.class, name = "EventSendApiAttr"),
        @JsonSubTypes.Type(value = RedisLockConfigAttr.class, name = "RedisLockConfigAttr")
})
@Data
public abstract class AbstractApiAttr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data_asset_api_id")
    private Integer apiId;
}