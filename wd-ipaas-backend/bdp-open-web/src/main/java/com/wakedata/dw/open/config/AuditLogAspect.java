package com.wakedata.dw.open.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.AuditLogResultEnum;
import com.wakedata.dw.open.model.UserPageActionPo;
import com.wakedata.dw.open.service.log.UserPageActionService;
import com.wakedata.dw.open.util.RemoteIpUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author wq
 * @title AuditLogAspect
 * @date 2020/11/6 17:05
 * @projectName dw-open
 * @description
 */
@Component
@Aspect
@Lazy(false)
public class AuditLogAspect {
    @Autowired
    private UserPageActionService userPageActionService;

    @Pointcut("@annotation(com.wakedata.dw.open.annotation.AuditLog)")
    private void cutMethod() {
    }

    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPageActionPo userPageAction = new UserPageActionPo();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //获取请求参数，将处理后的请求参数放进操作日志记录中
        userPageAction.setRequestParams(getParameter(method, joinPoint.getArgs(), signature.getParameterNames()));
        //此前相当于@Before注解，在执行目标方法之前执行
        //执行目标方法，并且将执行结果复制给object，最后把执行结果返回
        Object object = joinPoint.proceed();
        //此后相当于@After注解，在执行目标方法之后执行
        Class<?> aClass = joinPoint.getTarget().getClass();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        Api api = aClass.getAnnotation(Api.class);
        if (api != null && apiOperation != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            //获取接口返回结果
            userPageAction.setResponseParams(JSONUtil.toJsonStr(object));
            //获取操作结果
            userPageAction.setOperatingResult(((ResultDTO) object).isSuccess() ? AuditLogResultEnum.SUCCESS : AuditLogResultEnum.FAILURE);
            userPageAction.setPageResource(api.value());
            userPageAction.setPageEvent(apiOperation.value());
            userPageAction.setActionTime(new Date());
            userPageAction.setIp(RemoteIpUtils.realIP(request));
            userPageAction.setRequestUrl(request.getRequestURI());
            userPageAction.setActionUser(IpaasUserContext.getUserInfo().getUserIdentification());
            userPageAction.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
            userPageActionService.insert(userPageAction);
        }
        return object;
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private String getParameter(Method method, Object[] args, String[] parameterNames) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            map.put(parameterNames[i], args[i]);
        }
        return JSON.toJSONString(map);
    }

}