package com.wakedata.dw.open.controller.swagger;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.SwaggerApiQuery;
import com.wakedata.dw.open.service.api.dto.SwaggerApiDTO;
import com.wakedata.dw.open.service.swagger.SwaggerApiService;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Swagger API临时数据接口
 *
 * @author wujunqiang
 * @since 2022/8/24 15:04
 */
@Validated
@Api(value = "Swagger API临时数据接口", tags = "Swagger API临时数据接口")
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/swagger/api")
public class SwaggerApiController {

    @Resource
    private SwaggerApiService swaggerApiService;

    @ApiOperation("查询swagger临时api列表")
    @PostMapping(value = "/list/page/temporary.api")
    @ApiImplicitParam(name = "swaggerInfoId", value = "swagger id", required = true)
    @AuditLog
    public PageResultDTO<List<SwaggerApiDTO>> temporaryApiPageList(@RequestBody @Valid SwaggerApiQuery swaggerApiQuery) {
        return swaggerApiService.pageList(swaggerApiQuery);
    }

    @ApiOperation("新增swagger临时api")
    @PostMapping(value = "/add/temporary.api")
    @AuditLog
    public ResultDTO<Boolean> addTemporaryApi(@RequestBody @Valid SwaggerApiDTO swaggerApiDTO) throws Exception {
        swaggerApiDTO.setInCharge(IpaasUserContext.getUserInfo().getUserIdentification());
        return swaggerApiService.addSwaggerApi(swaggerApiDTO);
    }

    @ApiOperation("批量删除临时API数据")
    @PostMapping("/delete")
    public ResultDTO<Boolean> deleteByIds(@RequestBody @NotEmpty(message = "参数ids不能为空") List<Integer> ids) {
        return swaggerApiService.deleteBatch(ids);
    }

    @ApiOperation("重新拉取swagger临时api")
    @PostMapping("/again/add.temporary.api")
    public ResultDTO<Boolean> againImportTemporaryApi(@RequestBody @Valid SwaggerApiDTO swaggerApiDTO) throws Exception {
        return swaggerApiService.againImportTemporaryApi(swaggerApiDTO);
    }

    @ApiOperation("(批量)覆盖导入")
    @PostMapping(value = "/convert/add.api")
    @AuditLog
    public ResultDTO<String> convertDateAssetApiFromTemporary(@RequestBody @Valid SwaggerApiDTO swaggerApiDTO) {
        swaggerApiDTO.setInCharge(IpaasUserContext.getUserInfo().getUserIdentification());
        return swaggerApiService.convertDateAssetApiFromTemporary(swaggerApiDTO);
    }

}
