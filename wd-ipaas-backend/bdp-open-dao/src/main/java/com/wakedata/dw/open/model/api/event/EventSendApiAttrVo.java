package com.wakedata.dw.open.model.api.event;

import com.wakedata.dw.open.model.api.AbstractApiAttr;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * @author caoshuang
 * @date 2021/10
 * @projectName dw-open
 * @description
 */
@Data
@ToString
public class EventSendApiAttrVo extends AbstractApiAttr {

    private String eventCode;

    private String topicName;

    private String tags;

    private String clusterAddress;

    private Integer mqType;

}