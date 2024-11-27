package com.wakedata.dw.open.service.listener;

import com.wakedata.dw.open.model.api.AbstractApiAttr;
import java.util.List;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author wanghu@wakedata.com
 * @title Swagger响应参数解析完成事件
 * @date 2021/12/10
 * @since v1.0.0
 */
@Getter
public class SwaggerRespParamParsedEvent extends ApplicationEvent {

    /**
     * API参数属性
     */
    private List<AbstractApiAttr> httpApiAttrs;

    public SwaggerRespParamParsedEvent(Object source, List<AbstractApiAttr> httpApiAttrs) {
        super(source);
        this.httpApiAttrs = httpApiAttrs;
    }

}
