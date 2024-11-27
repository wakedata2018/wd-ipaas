package com.wakedata.dw.lowcode.controller;

import com.github.pagehelper.Page;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.config.AddLowCodeConfigDTO;
import com.wakedata.dw.lowcode.dto.config.LowCodeConfigDTO;
import com.wakedata.dw.lowcode.dto.config.QueryLowCodeConfigDTO;
import com.wakedata.dw.lowcode.model.LowCodeConfigPo;
import com.wakedata.dw.lowcode.service.LowCodeConfigService;
import com.wakedata.dw.lowcode.util.AppIdContext;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collections;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置信息 - 控制器
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@RestController
@RequestMapping("/low/code/config")
@Api(tags = "低代码配置信息")
public class LowCodeConfigController extends BaseLowCodeController {

    @Resource
    public LowCodeConfigService lowCodeConfigService;

    @AuditLog
    @PostMapping("/save")
    @ApiOperation("更新或者编辑配置信息")
    public ResultDTO<Integer> save(@Valid @RequestBody AddLowCodeConfigDTO dto) {
        LowCodeConfigPo lowCodeConfigPo = convertDtoToPo(dto, LowCodeConfigPo.class);
        return ResultDTO.success(lowCodeConfigService.save(lowCodeConfigPo));
    }

    @AuditLog
    @PostMapping("/delete")
    @ApiOperation("删除配置信息")
    public ResultDTO<Boolean> delete(@Valid @RequestBody IdDTO dto) {
        return ResultDTO.success(lowCodeConfigService.delete(dto.getId()) > 0);
    }

    @PostMapping("/detail")
    @ApiOperation("配置信息详情")
    public ResultDTO<LowCodeConfigDTO> detail(@RequestBody QueryLowCodeConfigDTO dto) {
        if (StringUtils.isBlank(dto.getType())) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        LowCodeConfigPo lowCodeConfigPo = lowCodeConfigService.findByAppIdType(AppIdContext.getAppId(), dto.getType());
        return ResultDTO.success(convertPoToDto(lowCodeConfigPo, LowCodeConfigDTO.class));
    }

    @AuditLog
    @PostMapping("/list")
    @ApiOperation("配置信息列表")
    public PageResultDTO<Page<?>> list(@RequestBody QueryLowCodeConfigDTO dto) {
        LowCodeConfigPo configInfo = new LowCodeConfigPo();
        configInfo.setAppId(AppIdContext.getAppId());

        Page<LowCodeConfigPo> configInfoPage = lowCodeConfigService
            .selectPageLikeOrderBy(configInfo, dto.getPageNo(), dto.getPageSize(), dto.getType(),
                Collections.singletonList("type"), "create_time", false
                , null, null, null, null, null);

        return PageResultDTO.success(covertPoListToDto(configInfoPage, LowCodeConfigDTO.class));
    }


}
