package com.wakedata.dw.lowcode.controller;

import com.github.pagehelper.Page;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.page.CopyPageReqDTO;
import com.wakedata.dw.lowcode.dto.page.LowCodePageBatchListDTO;
import com.wakedata.dw.lowcode.dto.page.LowCodePageDetailDTO;
import com.wakedata.dw.lowcode.dto.page.SetCategoryReqDTO;
import com.wakedata.dw.lowcode.dto.page.UpdateLowCodePageDTO;
import com.wakedata.dw.lowcode.helper.FileUploadHelper;
import com.wakedata.dw.lowcode.model.LowCodePageDetailPo;
import com.wakedata.dw.lowcode.model.LowCodePageSimplePo;
import com.wakedata.dw.lowcode.service.LowCodePageSimpleService;
import com.wakedata.dw.lowcode.service.dto.BatchFetchLowCodeInfoDTO;
import com.wakedata.dw.lowcode.service.dto.QueryLowCodePageDTO;
import com.wakedata.dw.lowcode.util.AppIdContext;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 页面信息 - 控制器
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@RestController
@RequestMapping("/low/code/page")
@Api(tags = "低代码页面信息")
public class LowCodePageController extends BaseLowCodeController {

    @Resource
    public LowCodePageSimpleService lowCodePageSimpleService;
    @Resource
    private FileUploadHelper fileUploadHelper;

    @AuditLog
    @PostMapping("/add")
    @ApiOperation("新增页面信息")
    public ResultDTO<Integer> add(@Valid @RequestBody UpdateLowCodePageDTO dto) {
        LowCodePageSimplePo simplePo = convertDtoToPo(dto, LowCodePageSimplePo.class);
        simplePo.setCover(fileUploadHelper.uploadBase64(dto.getCover()));

        LowCodePageDetailPo detailPo = convertDtoToPo(dto, LowCodePageDetailPo.class);
        return ResultDTO.success(lowCodePageSimpleService.add(simplePo, detailPo));
    }

    @AuditLog
    @PostMapping("/update")
    @ApiOperation("编辑页面信息")
    public ResultDTO<Boolean> update(@Valid @RequestBody UpdateLowCodePageDTO dto) {
        LowCodePageSimplePo simplePo = convertDtoToPo(dto, LowCodePageSimplePo.class);
        simplePo.setCover(fileUploadHelper.uploadBase64(dto.getCover()));

        LowCodePageDetailPo detailPo = convertDtoToPo(dto, LowCodePageDetailPo.class);
        return ResultDTO.success(lowCodePageSimpleService.update(simplePo, detailPo));
    }

    @AuditLog
    @PostMapping("/delete")
    @ApiOperation("删除页面信息")
    public ResultDTO<Boolean> delete(@Valid @RequestBody IdDTO dto) {
        return ResultDTO.success(lowCodePageSimpleService.deleteById(dto.getId()));
    }

    @AuditLog
    @PostMapping("/exists")
    @ApiOperation("页面是否存在")
    public ResultDTO<Boolean> detail(@RequestBody QueryLowCodePageDTO dto) {
        Boolean result = lowCodePageSimpleService
            .existsPageName(dto.getName(), dto.getId(), AppIdContext.getAppId());
        return ResultDTO.success(result);
    }

    @AuditLog
    @PostMapping("/detail")
    @ApiOperation("页面详情")
    public ResultDTO<LowCodePageDetailDTO> detail(@Valid @RequestBody IdDTO dto) {
        LowCodePageSimplePo lowCodePageSimplePo = lowCodePageSimpleService.detail(dto.getId());
        return ResultDTO.success(convertPoToDto(lowCodePageSimplePo, LowCodePageDetailDTO.class));
    }

    @AuditLog
    @PostMapping("/list")
    @ApiOperation("页面基础信息列表")
    public PageResultDTO<Page<?>> list(@RequestBody @Valid QueryLowCodePageDTO query) {
        query.setAppId(AppIdContext.getAppId());
        return PageResultDTO.success(covertPoListToDto(
            lowCodePageSimpleService.selectPage(query), LowCodePageDetailDTO.class));
    }

    @AuditLog
    @PostMapping("/setCategory")
    @ApiOperation("设置页面分类")
    public ResultDTO<Boolean> setCategory(@Valid @RequestBody SetCategoryReqDTO dto) {
        return ResultDTO.success(lowCodePageSimpleService.setCategory(dto.getId(), dto.getCategoryId()));
    }

    @AuditLog
    @PostMapping("/copyPage")
    @ApiOperation("拷贝页面")
    public ResultDTO<Boolean> copyPage(@Valid @RequestBody CopyPageReqDTO dto) {
        return ResultDTO.success(lowCodePageSimpleService.copyPage(dto.getId(), dto.getName()));
    }

    @PostMapping("/listByBatch")
    @ApiOperation("批量获取页面")
    public ResultDTO<List<LowCodePageBatchListDTO>> listByBatch(
        @RequestBody List<BatchFetchLowCodeInfoDTO> dtoList) {

        List<LowCodePageSimplePo>
            lowCodePageSimplePos = lowCodePageSimpleService.listByBatch(dtoList, AppIdContext.getAppId());
        return ResultDTO.success(convertPoListToDto(lowCodePageSimplePos, LowCodePageBatchListDTO.class));
    }

}
