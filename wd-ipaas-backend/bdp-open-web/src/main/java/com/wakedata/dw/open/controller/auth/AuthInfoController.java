package com.wakedata.dw.open.controller.auth;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.dto.AuthInfoDTO;
import com.wakedata.dw.open.model.auth.AuthAuthorizationPo;
import com.wakedata.dw.open.model.auth.AuthCountDo;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.auth.AuthInfoApiService;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import com.wakedata.dw.open.util.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证信息管理控制器
 *
 * @author chenshaopeng
 * @date 2021/11/2
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/auth")
@Api(value = "认证管理", tags = "认证信息管理")
@Validated
@Slf4j
public class AuthInfoController extends BaseController {

    @Resource
    private AuthInfoService authInfoService;

    @Resource
    private AuthInfoApiService authInfoApiService;


    @ApiOperation("添加认证信息")
    @PostMapping(value = "/add")
    @AuditLog
    public ResultDTO<AuthInfoPo> add(@RequestBody @Valid AuthInfoPo authInfo) {
        authInfo.setCreateUser(WebUtils.getCurrentUserInfo().getAccount());
        authInfoService.addAuthInfo(authInfo);
        return show(authInfo.getId());
    }

    @ApiOperation("修改认证信息")
    @AuditLog
    @PostMapping(value = "/update")
    public ResultDTO<AuthInfoPo> update(@RequestBody @Valid AuthInfoPo authInfo) {
        authInfoService.updateAuthInfo(authInfo);
        return show(authInfo.getId());
    }

    @PostMapping("/delete")
    @ApiOperation("删除认证信息")
    @AuditLog
    public ResultDTO<Object> delete(@NotNull(message = "authInfoId不能为空") Integer authInfoId) {
        authInfoService.deleteAuthInfo(authInfoId);
        return new ResultDTO<>();
    }

    @GetMapping("/show")
    @ApiOperation("查询Swagger信息")
    public ResultDTO<AuthInfoPo> show(@NotNull(message = "authInfoId不能为空") Integer authInfoId) {
        AuthInfoPo authInfo = authInfoService.detail(authInfoId);
        ResultDTO<AuthInfoPo> resultDTO = new ResultDTO<>();
        resultDTO.setData(authInfo);
        return resultDTO;
    }

    @GetMapping("/list/page/like")
    @ApiOperation("查询认证信息列表")
    @AuditLog
    public PageResultDTO<Page<AuthInfoPo>> pageLike(PageQuery pageQuery, String keyword) {
        // 关键字查询的列
        List<String> likeColumns = Arrays.asList("name", "appKey");

        Page<AuthInfoPo> authInfos = authInfoService.selectPageLikeOrderBy(pageQuery, keyword, likeColumns);
        if (CollectionUtils.isNotEmpty(authInfos)) {
            //求接入在多少个api里面授权了
            List<Integer> list = authInfos.stream().map(AuthInfoPo::getId).collect(Collectors.toList());
            List<AuthCountDo> appCountDos = authInfoService.queryApiReference(authInfos.stream()
                    .map(AuthInfoPo::getId).collect(Collectors.toList()));
            Map<Integer, Integer> collect = appCountDos.stream().collect(Collectors.toMap(AuthCountDo::getAuthId, AuthCountDo::getApiNum));
            authInfos.forEach(po -> {
                po.setApiNum(collect.getOrDefault(po.getId(), 0));
            });
        }
        PageResultDTO<Page<AuthInfoPo>> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount((int) authInfos.getTotal());
        resultDTO.setData(authInfos);
        return resultDTO;
    }

    @GetMapping("/list/query/name")
    @ApiOperation("查询已授权API的id和名称")
    @AuditLog
    public ResultDTO<List<ApiInfoDo>> apiList(Integer id) {
        List<ApiInfoDo> apiInfoDoList = authInfoService.queryThirdPartyApi(id);
        ResultDTO<List<ApiInfoDo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(apiInfoDoList);
        return resultDTO;
    }

    @PostMapping("/batch/authorization")
    @ApiOperation("批量授权")
    @AuditLog
    public ResultDTO<Boolean> authorizationApi(@RequestBody List<AuthAuthorizationPo> authAuthorizationPoList) {
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        resultDTO.setSuccess(authInfoApiService.batchAuthorization(authAuthorizationPoList));
        return resultDTO;
    }

    @GetMapping("/query/application/list")
    @ApiOperation("无结点API查询已授权的第三方应用")
    public ResultDTO<List<AuthInfoPo>> queryApplicationList(@RequestParam Integer apiId) {
        List<AuthInfoPo> authInfoPos = authInfoService.queryExternalApplication(apiId);
        ResultDTO<List<AuthInfoPo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(authInfoPos);
        return resultDTO;
    }

    /**
     * 根据无结点API ID查询已授权的第三方应用
     *
     * @param apiId API ID
     * @return 授权的第三方应用信息集合
     */
    @GetMapping("/query/getAuthApplicationList")
    @ApiOperation("根据无结点API ID查询已授权的第三方应用")
    public ResultDTO<List<AuthInfoDTO>> getAuthApplicationList(Integer apiId) {
        return ResultDTO.success(authInfoService.getAuthApplicationList(apiId));
    }

    @GetMapping("/delete/authorization")
    @ApiOperation("解除api和第三方应用授权")
    public ResultDTO<Boolean> deleteAuthorization(@RequestParam Integer apiId, @RequestParam Integer authId) {
        Boolean result = authInfoService.deleteAuthorization(apiId, authId);
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        resultDTO.setData(result);
        return resultDTO;
    }
}
