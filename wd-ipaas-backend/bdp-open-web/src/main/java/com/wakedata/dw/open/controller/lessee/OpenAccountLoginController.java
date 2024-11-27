package com.wakedata.dw.open.controller.lessee;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.lessee.OpenAccountLoginService;
import com.wakedata.dw.open.service.lessee.dto.AccountLoginDTO;
import com.wakedata.dw.open.service.lessee.dto.IpaasUserInfoDTO;
import com.wakedata.wd.permission.captcha.api.CaptchaService;
import com.wakedata.wd.permission.captcha.dto.ImageVerificationDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author luomeng
 * @date 2022/8/3 11:26
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/manage/login")
@Api(value = "Ipaas账号登录", tags = "Ipaas账号登录")
@Validated
@Slf4j
public class OpenAccountLoginController {

    @Resource
    private OpenAccountLoginService openAccountLoginService;
    @Resource
    private CaptchaService captchaService;

    @PostMapping(value = "/login")
    @ApiOperation(value = "ipaas账号登录")
    public ResultDTO<IpaasUserInfoDTO> login(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid AccountLoginDTO accountLoginDTO){

        return openAccountLoginService.login(request,response,accountLoginDTO);
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "退出登录")
    public ResultDTO<Boolean> logout(HttpServletRequest request, HttpServletResponse response){

        return openAccountLoginService.logout(request,response);
    }

    @PostMapping(value = "/get.account.info")
    @ApiOperation(value = "ipaas获取用户信息")
    public ResultDTO<IpaasUserInfoDTO> getAccountInfo(HttpServletRequest request){

        return openAccountLoginService.getAccountInfo(request);
    }

    @ApiOperation(value = "获取验证码图片",httpMethod = "POST")
    @PostMapping(value = "/captcha/get/image")
    @ResponseBody
    public com.wakedata.common.core.dto.ResultDTO<ImageVerificationDTO> getVerificationImage(String type, Integer width, Integer height){
        return captchaService.selectImageVerificationCode(type,width,height);
    }

}
