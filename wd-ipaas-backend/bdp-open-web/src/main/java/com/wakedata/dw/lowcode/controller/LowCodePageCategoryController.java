package com.wakedata.dw.lowcode.controller;

import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.category.AddLowCodePageCategoryDTO;
import com.wakedata.dw.lowcode.dto.category.LowCodePageCategoryDTO;
import com.wakedata.dw.lowcode.model.LowCodePageCategoryPo;
import com.wakedata.dw.lowcode.service.LowCodePageCategoryService;
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
 * 分类信息 - 控制器
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@RestController
@RequestMapping("/low/code/category")
@Api(tags = "低代码分类信息")
public class LowCodePageCategoryController extends BaseLowCodeController {

    @Resource
    public LowCodePageCategoryService lowCodePageCategoryService;

    @AuditLog
    @PostMapping("/add")
    @ApiOperation("新增分类信息")
    public ResultDTO<Integer> add(@Valid @RequestBody AddLowCodePageCategoryDTO dto) {
        LowCodePageCategoryPo lowCodePageCategoryPo = convertDtoToPo(dto, LowCodePageCategoryPo.class);
        lowCodePageCategoryService.add(lowCodePageCategoryPo);

        return ResultDTO.success(lowCodePageCategoryPo.getId());
    }

    @AuditLog
    @PostMapping("/update")
    @ApiOperation("编辑分类信息")
    public ResultDTO<Boolean> update(@Valid @RequestBody AddLowCodePageCategoryDTO dto) {
        if (Objects.isNull(dto.getId())) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        LowCodePageCategoryPo lowCodePageCategoryPo = convertDtoToPo(dto, LowCodePageCategoryPo.class);
        return ResultDTO.success(lowCodePageCategoryService.update(lowCodePageCategoryPo) > 0);
    }

    @AuditLog
    @PostMapping("/delete")
    @ApiOperation("删除分类信息")
    public ResultDTO<Boolean> delete(@Valid @RequestBody IdDTO dto) {
        return ResultDTO.success(lowCodePageCategoryService.delete(dto.getId()) > 0);
    }

    @AuditLog
    @PostMapping("/detail")
    @ApiOperation("分类信息详情")
    public ResultDTO<LowCodePageCategoryDTO> detail(@Valid @RequestBody IdDTO dto) {
        LowCodePageCategoryPo lowCodePageCategoryPo = lowCodePageCategoryService.show(dto.getId());
        return ResultDTO.success(convertPoToDto(lowCodePageCategoryPo, LowCodePageCategoryDTO.class));
    }

    @AuditLog
    @PostMapping("/list")
    @ApiOperation("分类信息列表")
    public ResultDTO<List<LowCodePageCategoryDTO>> list() {
        LowCodePageCategoryPo po = new LowCodePageCategoryPo();
        po.setAppId(AppIdContext.getAppId());

        List<LowCodePageCategoryPo> categoryPos = lowCodePageCategoryService.find(po);
        return ResultDTO.success(convertPoListToDto(categoryPos, LowCodePageCategoryDTO.class));
    }

}
