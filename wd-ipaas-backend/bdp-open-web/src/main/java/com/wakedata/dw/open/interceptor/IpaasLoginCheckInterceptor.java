package com.wakedata.dw.open.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.service.impl.lessee.IpaasUserInfoHelper;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ipaas后台接口登录拦截并设置上下文
 * @author luomeng
 * @date 2022/8/4 10:34
 */
public class IpaasLoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 预处理
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String sessionId = IpaasUserInfoHelper.getIpaasCookieId(request);
        if(ObjectUtil.isEmpty(sessionId)){
            return notLogin(response);
        }
        String value = RedisUtil.getInstance().get(IpaasUserInfoHelper.getIpaasUserInfoRedisKey(sessionId));
        if(ObjectUtil.isEmpty(value)){
            return notLogin(response);
        }
        IpaasUserInfo ipaasUserInfo = JSONUtil.toBean(value,IpaasUserInfo.class);
        IpaasUserContext.setUserInfo(ipaasUserInfo);
        IpaasUserInfoHelper.refreshIpaasUserRedisExpireTime(sessionId);
        return true;
    }

    /**
     * 完成之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        IpaasUserContext.removeUserInfo();
    }

    /**
     * 未登录
     * @param response
     * @return
     */
    private boolean notLogin(HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        //返回的数据
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        resultDTO.setFailed(MsgCodeEnum.s_not_login);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(JSONUtil.toJsonStr(resultDTO));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(out != null) {
                out.flush();
                out.close();
            }
        }
        return false;
    }
}
