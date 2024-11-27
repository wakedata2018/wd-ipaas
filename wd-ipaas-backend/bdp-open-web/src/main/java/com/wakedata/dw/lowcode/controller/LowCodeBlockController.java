package com.wakedata.dw.lowcode.controller;

import com.github.pagehelper.Page;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.block.EditLowCodeBlockDTO;
import com.wakedata.dw.lowcode.dto.block.PageLowCodeBlockDTO;
import com.wakedata.dw.lowcode.helper.FileUploadHelper;
import com.wakedata.dw.lowcode.model.LowCodeBlockPo;
import com.wakedata.dw.lowcode.service.LowCodeBlockService;
import com.wakedata.dw.lowcode.util.AppIdContext;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 低代码区块
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/24
 * @since v1.0.0
 */
@RestController
@RequestMapping("/low/code/block")
@Api(tags = "低代码区块信息")
public class LowCodeBlockController extends BaseLowCodeController {

    @Resource
    public LowCodeBlockService lowCodeBlockService;
    @Resource
    private FileUploadHelper fileUploadHelper;

    @AuditLog
    @PostMapping("/add")
    @ApiOperation("添加区块")
    public ResultDTO<Integer> add(@Valid @RequestBody EditLowCodeBlockDTO dto) {
        LowCodeBlockPo lowCodeBlockPo = convertDtoToPo(dto, LowCodeBlockPo.class);
        lowCodeBlockPo.setSnapshot(fileUploadHelper.uploadBase64(dto.getSnapshot()));

        lowCodeBlockService.add(lowCodeBlockPo);
        return ResultDTO.success(lowCodeBlockPo.getId());
    }

    @AuditLog
    @PostMapping("/update")
    @ApiOperation("编辑区块")
    public ResultDTO<Boolean> update(@Valid @RequestBody EditLowCodeBlockDTO dto) {
        if (Objects.isNull(dto.getId())) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        LowCodeBlockPo lowCodeBlockPo = convertDtoToPo(dto, LowCodeBlockPo.class);
        lowCodeBlockPo.setSnapshot(fileUploadHelper.uploadBase64(dto.getSnapshot()));

        return ResultDTO.success(lowCodeBlockService.update(lowCodeBlockPo) > 0);
    }

    @AuditLog
    @PostMapping("/delete")
    @ApiOperation("删除区块")
    public ResultDTO<Boolean> delete(@Valid @RequestBody IdDTO dto) {
        return ResultDTO.success(lowCodeBlockService.delete(dto.getId()) > 0);
    }

    @AuditLog
    @PostMapping("/detail")
    @ApiOperation("区块详情")
    public ResultDTO<EditLowCodeBlockDTO> detail(@Valid @RequestBody IdDTO dto) {
        LowCodeBlockPo lowCodeBlockPo = lowCodeBlockService.show(dto.getId());
        return ResultDTO.success(convertPoToDto(lowCodeBlockPo, EditLowCodeBlockDTO.class));
    }

    @AuditLog
    @PostMapping("/list")
    @ApiOperation("区块列表")
    public PageResultDTO<Page<?>> list(@Valid @RequestBody PageLowCodeBlockDTO dto) {
        LowCodeBlockPo queryLowCodeBlock = new LowCodeBlockPo();
        queryLowCodeBlock.setAppId(AppIdContext.getAppId());

        Page<LowCodeBlockPo> blockPoPage = lowCodeBlockService
            .selectPageLikeOrderBy(queryLowCodeBlock, dto.getPageNo(), dto.getPageSize(), dto.getName(),
                Collections.singletonList("name"), "create_time", false, null, null, null, null, null);

        return PageResultDTO.success(covertPoListToDto(blockPoPage, EditLowCodeBlockDTO.class));
    }


}
