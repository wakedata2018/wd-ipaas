package com.wakedata.dw.lowcode.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author wanghu@wakedata.com
 * @title 应用删除事件
 * @date 2021/11/25
 * @since v1.0.0
 */
@Getter
public class AppDeletedEvent extends ApplicationEvent {

    /**
     * 应用id
     */
    private Integer appId;

    public AppDeletedEvent(Object source, Integer appId) {
        super(source);
        this.appId = appId;
    }
}
