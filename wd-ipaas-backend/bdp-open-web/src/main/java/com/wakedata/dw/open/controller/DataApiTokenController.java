package com.wakedata.dw.open.controller;

import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.accesstoken.AccessToken;
import com.wakedata.dw.open.service.accesstoken.dto.AccessTokenGenerateDTO;
import com.wakedata.dw.open.service.accesstoken.dto.AccessTokenRefreshDTO;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author luomeng
 * @date 2022/8/6 14:09
 */
@RequestMapping("${spring.mvc.backend.api.prefix}/auth")
@RestController
@Slf4j
@Api(value = "开放平台的token获取", tags = "开放平台提供外部调用的token相关接口")
public class DataApiTokenController {

    @Resource
    private AccessTokenService accessTokenService;

    @ApiOperation(value = "获取授权access_token", notes = "获取授权access_token", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appKey",value = "应用appKey",paramType = "query"),
            @ApiImplicitParam(name = "timestamp",value = "时间戳",paramType = "query"),
            @ApiImplicitParam(name = "sign",value = "签名",paramType = "query")
    })
    @RequestMapping(value = "/get.access.token", method = RequestMethod.GET)
    public ResultDTO<AccessTokenGenerateDTO> queryToken(HttpServletRequest request, @RequestParam("appKey") String appKey, @RequestParam("timestamp") String timestamp, @RequestParam("sign") String sign) {
        log.info("get.access.token：appKey = {},timestamp = {},sign = {}",appKey,timestamp,sign);
        AccessToken accessToken = accessTokenService.generate(appKey, timestamp, sign);
        AccessTokenGenerateDTO tokenGenerateDTO = BeanUtil.copy(accessToken, AccessTokenGenerateDTO.class);
        log.info("get.access.token：resp = {}", JSONUtil.toJsonStr(tokenGenerateDTO));
        return ResultDTO.success(tokenGenerateDTO);
    }

    @ApiOperation(value = "刷新授权access_token", notes = "刷新授权access_token", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appKey",value = "应用appKey",paramType = "query"),
            @ApiImplicitParam(name = "refreshToken",value = "获取到的refreshToken",paramType = "query"),
            @ApiImplicitParam(name = "timestamp",value = "时间戳",paramType = "query"),
            @ApiImplicitParam(name = "sign",value = "签名",paramType = "query")
    })
    @RequestMapping(value = "/refresh.token", method = RequestMethod.GET)
    public ResultDTO<AccessTokenRefreshDTO> refreshToken(HttpServletRequest request,@RequestParam("appKey") String appKey,
                                                         @RequestParam("refreshToken") String refreshToken, @RequestParam("timestamp") String timestamp
                                                            , @RequestParam("sign") String sign) {
        log.info("refresh.token：appKey = {},timestamp = {},sign = {}",appKey,timestamp,sign);
        AccessToken accessToken = accessTokenService.refresh(appKey,refreshToken, timestamp, sign);
        AccessTokenRefreshDTO tokenRefreshDTO = BeanUtil.copy(accessToken, AccessTokenRefreshDTO.class);
        log.info("refresh.token：resp = {}", JSONUtil.toJsonStr(tokenRefreshDTO));
        return ResultDTO.success(tokenRefreshDTO);
    }

    @ApiOperation(value = "校验accessToken是否有效", notes = "校验accessToken是否有效", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appKey",value = "应用appKey",paramType = "query"),
            @ApiImplicitParam(name = "accessToken",value = "获取到的accessToken",paramType = "query"),
            @ApiImplicitParam(name = "timestamp",value = "时间戳",paramType = "query"),
            @ApiImplicitParam(name = "sign",value = "签名",paramType = "query")
    })
    @RequestMapping(value = "/check.token", method = RequestMethod.GET)
    public ResultDTO<Boolean> checkToken(HttpServletRequest request,@RequestParam("appKey") String appKey,
                                                         @RequestParam("accessToken") String accessToken, @RequestParam("timestamp") String timestamp
                                                            , @RequestParam("sign") String sign) {
        log.info("check.token：appKey = {},timestamp = {},sign = {}",appKey,timestamp,sign);
        Boolean status = accessTokenService.check(appKey,accessToken, timestamp, sign);
        log.info("check.token：resp = {}", status);
        return ResultDTO.success(status);
    }

}
