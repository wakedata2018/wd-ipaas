package com.wakedata.dw.lowcode.handler;

import com.wakedata.dw.lowcode.util.AppIdContext;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author wanghu@wakedata.com
 * @title 获取低代码请求头中的应用id
 * @date 2021/12/2
 * @since v1.0.0
 */
@Slf4j
@Component
public class FetchAppIdInterceptor extends HandlerInterceptorAdapter {

    /**
     * 请求头应用ID
     */
    private static final String HEADER_APP_ID = "App-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String appId = request.getHeader(HEADER_APP_ID);
        if (StringUtils.isBlank(appId)) {
            throw new OpenException(MsgCodeEnum.w_data_app_id_not_found);
        }

        AppIdContext.setAppId(Integer.valueOf(appId));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {

        AppIdContext.cleanAppId();
    }
}
