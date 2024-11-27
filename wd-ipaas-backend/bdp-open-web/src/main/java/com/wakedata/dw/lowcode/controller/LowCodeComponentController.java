package com.wakedata.dw.lowcode.controller;

import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.component.EditLowCodeComponentDTO;
import com.wakedata.dw.lowcode.dto.component.ExistLowCodeComponentNameDTO;
import com.wakedata.dw.lowcode.helper.FileUploadHelper;
import com.wakedata.dw.lowcode.model.LowCodeComponentPo;
import com.wakedata.dw.lowcode.service.LowCodeComponentService;
import com.wakedata.dw.lowcode.service.dto.BatchFetchLowCodeInfoDTO;
import com.wakedata.dw.lowcode.util.AppIdContext;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 低代码组件
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/24
 * @since v1.0.0
 */
@RestController
@RequestMapping("/low/code/component")
@Api(tags = "低代码组件信息")
public class LowCodeComponentController extends BaseLowCodeController {

    @Resource
    private LowCodeComponentService lowCodeComponentService;
    @Resource
    private FileUploadHelper fileUploadHelper;

    @AuditLog
    @PostMapping("/add")
    @ApiOperation("添加组件")
    public ResultDTO<Integer> add(@Valid @RequestBody EditLowCodeComponentDTO dto) {
        LowCodeComponentPo lowCodeComponentPo = convertDtoToPo(dto, LowCodeComponentPo.class);
        uploadImage(lowCodeComponentPo);

        lowCodeComponentService.add(lowCodeComponentPo);
        return ResultDTO.success(lowCodeComponentPo.getId());
    }

    private void uploadImage(LowCodeComponentPo lowCodeComponentPo) {
        //上传图片到云上
        lowCodeComponentPo.setIcon(fileUploadHelper.uploadBase64(lowCodeComponentPo.getIcon()));
        lowCodeComponentPo.setCover(fileUploadHelper.uploadBase64(lowCodeComponentPo.getCover()));
    }

    @AuditLog
    @PostMapping("/update")
    @ApiOperation("编辑组件")
    public ResultDTO<Boolean> update(@Valid @RequestBody EditLowCodeComponentDTO dto) {
        if (Objects.isNull(dto.getId())) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        LowCodeComponentPo lowCodeComponentPo = convertDtoToPo(dto, LowCodeComponentPo.class);
        uploadImage(lowCodeComponentPo);

        return ResultDTO.success(lowCodeComponentService.update(lowCodeComponentPo) > 0);
    }

    @AuditLog
    @PostMapping("/delete")
    @ApiOperation("删除组件")
    public ResultDTO<Boolean> delete(@Valid @RequestBody IdDTO dto) {
        return ResultDTO.success(lowCodeComponentService.delete(dto.getId()) > 0);
    }

    @AuditLog
    @PostMapping("/detail")
    @ApiOperation("组件详情")
    public ResultDTO<EditLowCodeComponentDTO> detail(@Valid @RequestBody IdDTO dto) {
        LowCodeComponentPo lowCodeComponentPo = lowCodeComponentService.show(dto.getId());
        return ResultDTO.success(convertPoToDto(lowCodeComponentPo, EditLowCodeComponentDTO.class));
    }

    @AuditLog
    @PostMapping("/existsComponentName")
    @ApiOperation("存在组件名称(标识符)")
    public ResultDTO<Boolean> existsComponentName(@Valid @RequestBody ExistLowCodeComponentNameDTO dto) {
        return ResultDTO.success(
            lowCodeComponentService.existsComponentName(dto.getName(), dto.getId(), AppIdContext.getAppId()));
    }

    @PostMapping("/listByBatch")
    @ApiOperation("批量获取组件")
    public ResultDTO<List<EditLowCodeComponentDTO>> listByBatch(
        @RequestBody List<BatchFetchLowCodeInfoDTO> dtoList) {

        List<LowCodeComponentPo> componentPos = lowCodeComponentService.listByBatch(dtoList, AppIdContext.getAppId());
        return ResultDTO.success(convertPoListToDto(componentPos, EditLowCodeComponentDTO.class));
    }

    @AuditLog
    @PostMapping("/list")
    @ApiOperation("组件列表")
    public ResultDTO<List<EditLowCodeComponentDTO>> list() {
        List<LowCodeComponentPo> lowCodeComponentPos = lowCodeComponentService.findByAppId(AppIdContext.getAppId());
        return ResultDTO.success(convertPoListToDto(lowCodeComponentPos, EditLowCodeComponentDTO.class));
    }

}
