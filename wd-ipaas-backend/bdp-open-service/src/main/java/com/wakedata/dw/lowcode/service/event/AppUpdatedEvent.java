package com.wakedata.dw.lowcode.service.event;

import com.wakedata.dw.open.model.api.AppAccessPo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author wanghu@wakedata.com
 * @title 应用更新事件
 * @date 2021/11/25
 * @since v1.0.0
 */
@Getter
public class AppUpdatedEvent extends ApplicationEvent {

    /**
     * 数据访问接入端PO
     */
    private AppAccessPo appAccessPo;

    public AppUpdatedEvent(Object source, AppAccessPo appAccessPo) {
        super(source);
        this.appAccessPo = appAccessPo;
    }
}
