package com.wakedata.dw.open.controller.license;

import com.wakedata.dw.open.controller.dto.VerifyDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/10/12 9:40
 */
@RestController
@Slf4j
@RequestMapping(value = "${spring.mvc.backend.prefix}/license")
@Api(value = "License管理", tags = "License管理")
public class LicenseController {

    @GetMapping("/get/information")
    @ApiOperation(value = "获取license信息", tags = "获取license信息", httpMethod = "GET")
    public ResultDTO<VerifyDTO> getLicenseInformation(HttpServletRequest request) {
        return ResultDTO.success((VerifyDTO) request.getAttribute("verifyDTO"));
    }
}
