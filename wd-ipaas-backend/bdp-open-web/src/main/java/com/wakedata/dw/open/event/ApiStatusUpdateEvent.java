package com.wakedata.dw.open.event;

import com.wakedata.dw.open.service.vo.ApiDetailVo;
import org.springframework.context.ApplicationEvent;

/**
 * API状态改变事件
 *
 * @author 佟蕊
 */
public class ApiStatusUpdateEvent extends ApplicationEvent {

    private String apiStatus;
    private Integer dataAssetApiId;
    private ApiDetailVo apiDetailVo;

    public ApiStatusUpdateEvent(Object source) {
        super(source);
    }

    public ApiStatusUpdateEvent(Object source,Integer dataAssetApiId ,String apiStatus) {
        super(source);
        this.apiStatus = apiStatus;
        this.dataAssetApiId=dataAssetApiId;
    }

    public ApiStatusUpdateEvent(Object source,ApiDetailVo apiDetailVo ,String apiStatus) {
        super(source);
        this.apiStatus = apiStatus;
        this.apiDetailVo=apiDetailVo;
        this.dataAssetApiId = apiDetailVo.getDataAssetApi().getDataAssetApiId();
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public Integer getDataAssetApiId() {
        return dataAssetApiId;
    }

    public void setDataAssetApiId(Integer dataAssetApiId) {
        this.dataAssetApiId = dataAssetApiId;
    }

    public ApiDetailVo getApiDetailVo() {
        return apiDetailVo;
    }

    public void setApiDetailVo(ApiDetailVo apiDetailVo) {
        this.apiDetailVo = apiDetailVo;
    }
}
