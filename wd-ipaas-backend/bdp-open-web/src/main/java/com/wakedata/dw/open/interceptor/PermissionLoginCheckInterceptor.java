package com.wakedata.dw.open.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 在权限后台使用的接口校验
 * @author luomeng
 * @date 2022/8/6 10:55
 */
@Slf4j
public class PermissionLoginCheckInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String sessionId = getPermissionCookieId(request);
        if(ObjectUtil.isEmpty(sessionId)){
            return notLogin(response);
        }
        //未缓存redis,暂不校验
        return true;
    }


    private String getPermissionCookieId(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if(ObjectUtil.isEmpty(cookies)){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(DwOpenConstant.COOKIE_PERMISSION_KEY.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
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
        ResultDTO<Boolean> resultDTO = ResultDTO.fail(MsgCodeEnum.s_not_login.getCode(),MsgCodeEnum.s_not_login.getDesc());
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
