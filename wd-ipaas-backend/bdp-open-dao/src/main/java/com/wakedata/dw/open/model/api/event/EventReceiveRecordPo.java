package com.wakedata.dw.open.model.api.event;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;

/**
 * 消息监听表
 *
 * @author wujunqiang
 * @since 2022/10/26 10:38
 */
@Data
@Table(name = "dw_open_api_event_receive_record")
@ApiModel("事件接收请求编排结果记录")
public class EventReceiveRecordPo extends BaseLesseePo {

    /**
     * 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 消息内容
     */
    @Column(name = "message")
    private String message;

    /**
     * API ID
     */
    @Column(name = "data_asset_api_id")
    private Integer dataAssetApiId;

    /**
     * 请求服务编排URL
     */
    @Column(name = "request_url")
    private String requestUrl;

    /**
     * 请求结果，0：成功、1：失败、2：异常
     *
     * @see com.wakedata.dw.open.enums.ExecuteStatusEnum
     */
    @Column(name = "execute_status")
    private Integer executeStatus;

    /**
     * 响应数据
     */
    @Column(name = "response_body")
    private String responseBody;

    /**
     * 异常信息
     */
    @Column(name = "exception_message")
    private String exceptionMessage;

}
