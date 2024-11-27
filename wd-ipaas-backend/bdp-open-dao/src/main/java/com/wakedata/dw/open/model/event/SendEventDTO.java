package com.wakedata.dw.open.model.event;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author caoshuang
 * @date 2021/10/20
 */
@Getter
@Setter
@ToString
@Slf4j
public class SendEventDTO implements Serializable {

    private static final long serialVersionUID = -6547545932351810962L;

    @NotNull(message = "集团id不能为空")
    private Long epId;

    @NotNull(message = "应用id不能为空")
    private Long appId;

    @NotBlank(message = "源事件唯一ID不能为空")
    private String sourceEventId;

    @NotBlank(message = "事件编码不能为空")
    private String eventCode;

    @NotBlank(message = "来源系统编码不能为空")
    private String sourceSystemCode;

    @NotNull(message = "消息发送的时间戳不能为空")
    private Long messageSendTimestamp;

    @NotNull(message = "事件发生的时间戳不能为空")
    private Long eventHappenTimestamp;

    @NotBlank(message = "消息内容不能为空")
    private String infoData;

    /**
     * 接收类型（http, kafka, mq）
     */
    private Integer receiveType;

    /**
     * 消息标签（以，隔开）
     */
    private String tags;



    public  Boolean validData() {
        if (this.getAppId() == null || this.getAppId() <= 0) {
            log.error("应用id不能为空! params:{}" ,this.toString());
            return false;
        }
        if (this.getEpId() == null || this.getEpId() <= 0) {
            log.error("集团id不能为空! params:{}" ,this.toString());
            return false;
        }
        if (StringUtils.isEmpty(this.getEventCode())) {
            log.error("事件编码不能为空! params:{}" ,this.toString());
            return false;
        }
        if (StringUtils.isEmpty(this.getSourceEventId())) {
            log.error("源事件唯一ID不能为空! params:{}" ,this.toString());
            return false;
        }
//        if (StringUtils.isEmpty(this.getSourceSystemCode())) {
//            log.error("来源系统编码不能为空! params:{}" ,this.toString());
//            return false;
//        }
        if (this.getEventHappenTimestamp() == null || this.getEventHappenTimestamp() <= 0) {
            log.error("事件发生的时间戳不能为空! params:{}" ,this.toString());
            return false;
        }
        if (this.getMessageSendTimestamp() == null || this.getMessageSendTimestamp() <= 0) {
            log.error("消息发送的时间戳不能为空! params:{}" ,this.toString());
            return false;
        }
        if (StringUtils.isEmpty(this.getInfoData())) {
            log.error("消息内容不能为空! params:{}" ,this.toString());
            return false;
        }
        return true;
    }
}
