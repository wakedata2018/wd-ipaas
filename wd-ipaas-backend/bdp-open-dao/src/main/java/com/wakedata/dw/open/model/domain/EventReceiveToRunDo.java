package com.wakedata.dw.open.model.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * EventReceiveToRunDo 流程编排-事件接收-即将运行的topic和API
 *
 * @author 佟蕊
 */
@Data
public class EventReceiveToRunDo {

    private Integer realId;
    private Integer eventId;
    private Integer apiId;
    private Integer topicId;

    private String requestType;
    private String topicName;
    private String tag;
    private String apiUrl;
    private Integer publishStatus;

    @Override
    public boolean equals(Object obj) {

        boolean classFlag = obj instanceof EventReceiveToRunDo;

        if(!classFlag){
            return false;
        }

        EventReceiveToRunDo eventReceiveToRunDo = (EventReceiveToRunDo)obj;

        boolean topicIdFlag = (this.topicId == null && eventReceiveToRunDo.getTopicId() == null)
                || ((this.topicId != null) && (eventReceiveToRunDo.getTopicId() != null) && this.topicId.equals(eventReceiveToRunDo.getTopicId()));

        boolean topicNameFlag = (StringUtils.isEmpty(this.topicName) && StringUtils.isEmpty(eventReceiveToRunDo.getTopicName()))
                || (StringUtils.isNotEmpty(this.topicName) && StringUtils.isNotEmpty(eventReceiveToRunDo.getTopicName()) && this.topicName.equals(eventReceiveToRunDo.getTopicName()));

        boolean tagFlag = (StringUtils.isEmpty(this.tag) && StringUtils.isEmpty(eventReceiveToRunDo.getTag()))
                || (StringUtils.isNotEmpty(this.tag) && StringUtils.isNotEmpty(eventReceiveToRunDo.getTag()) && this.tag.equals(eventReceiveToRunDo.getTag()));

        boolean apiUrlFlag = (StringUtils.isEmpty(this.apiUrl) && StringUtils.isEmpty(eventReceiveToRunDo.getApiUrl()))
                || (StringUtils.isNotEmpty(this.apiUrl) && StringUtils.isNotEmpty(eventReceiveToRunDo.getApiUrl()) && this.apiUrl.equals(eventReceiveToRunDo.getApiUrl()));

        return topicIdFlag && topicNameFlag && tagFlag && apiUrlFlag;

    }

    @Override
    public int hashCode(){
        Integer topicIdHashContent = topicId==null?0:topicId;
        String topicNameContent = StringUtils.isEmpty(topicName)?"":topicName;
        String tagContent = StringUtils.isEmpty(tag)?"":tag;
        String apiUrlContent = StringUtils.isEmpty(apiUrl)?"":apiUrl;

        String hashContent = topicIdHashContent + topicNameContent + tagContent + apiUrlContent;
        return hashContent.hashCode();
    }
}
