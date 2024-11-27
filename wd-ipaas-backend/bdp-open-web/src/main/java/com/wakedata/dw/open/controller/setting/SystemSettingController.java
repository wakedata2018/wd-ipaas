package com.wakedata.dw.open.controller.setting;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.setting.SystemSettingService;
import com.wakedata.dw.open.service.setting.dto.SystemSettingDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月22日 11:25:53
 */
@Api(value = "系统设置相关接口", tags = "系统设置相关接口")
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/sys/setting")
public class SystemSettingController {

    @Resource
    private SystemSettingService systemSettingService;

    @ApiOperation(value = "获取系统设置的系统名称和系统logo", notes = "获取系统设置的系统名称和系统logo", httpMethod = "GET")
    @GetMapping("/query/name.and.logo")
    public ResultDTO<SystemSettingDTO> getSystemInfoOfNameAndLogo() {
        return systemSettingService.getSystemInfoOfNameAndLogo();
    }


    @ApiOperation(value = "设置系统名称和系统logo", notes = "设置系统名称和系统logo", httpMethod = "POST")
    @PostMapping("/build/name.and.logo")
    public ResultDTO<Boolean> buildNameAndLogo(@RequestBody @Validated SystemSettingDTO dto) {
        return systemSettingService.buildNameAndLogo(dto);
    }


}
