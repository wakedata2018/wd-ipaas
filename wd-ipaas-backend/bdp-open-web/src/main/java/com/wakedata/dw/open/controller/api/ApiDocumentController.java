package com.wakedata.dw.open.controller.api;

import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.RoleAuthEnum;
import com.wakedata.dw.open.model.query.ApiDocumentQuery;
import com.wakedata.dw.open.model.query.ApiQuery;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataApiGatewayService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.ApiGroupDTO;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDTO;
import com.wakedata.dw.open.service.utils.DocumentManagementUtils;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.service.vo.DocumentApiDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zhengqinghui@wakedata.com
 * date 2022/8/19 9:50
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/document")
@Api(value = "开放平台API官方文档", tags = "开放平台API官方文档")
@Validated
public class ApiDocumentController {

    @Resource
    private ApiGroupService apiGroupService;

    @Resource
    private DataAssetApiService dataAssetApiService;

    @Resource
    private DataApiGatewayService dataApiGatewayService;


    @PostMapping("/api/group/list")
    @ApiOperation(value = "Api文档：查询接口分类列表", notes = "Api文档：查询接口分类列表", httpMethod = "POST")
    public ResultDTO<List<ApiGroupDTO>> queryApiGroupList() {
        return ResultDTO.success(apiGroupService.queryApiGroupAndApi());
    }

    /**
     * 暂时没有地方用到这个接口
     */
    @Deprecated
    @PostMapping("/api/list/page")
    @ApiOperation(value = "Api文档：查询接口分类下的API列表", notes = "Api文档：查询接口分类下的API列表", httpMethod = "POST")
    public PageResultDTO<List<DataAssetApiDTO>> queryApiList(@RequestBody @Valid ApiDocumentQuery query) {
        //限制入参
        ApiQuery apiQuery = BeanUtil.copy(query, ApiQuery.class);
        //限制权限为开发者权限
        apiQuery.setPlatformAdmin(Boolean.parseBoolean(RoleAuthEnum.DEVELOPER.getValue()));
        return dataAssetApiService.pageList(apiQuery);
    }

    @GetMapping("/api/show")
    @ApiOperation(value = "Api文档：查询API详情", notes = "Api文档：查询API详情", httpMethod = "GET")
    public ResultDTO<DocumentApiDetailVo> queryApiInformation(@RequestParam("dataAssetApiId") String dataAssetApiId) {
        ApiDetailVo apiDetailVo = dataAssetApiService.detailVo(DocumentManagementUtils.decode(dataAssetApiId), null);
        return ResultDTO.success(dataAssetApiService.parseApiDetailOfDocument(apiDetailVo));
    }

    @GetMapping("/api/get/requiredInput")
    @ApiOperation(value = "Api文档：查询公共参数", notes = "Api文档：查询公共参数", httpMethod = "GET")
    public ResultDTO<Map<String, Object>> getRequiredInput(@RequestParam("dataAssetId") String dataAssetId) {
        return dataApiGatewayService.getRequireInput(DocumentManagementUtils.decode(dataAssetId));
    }

}