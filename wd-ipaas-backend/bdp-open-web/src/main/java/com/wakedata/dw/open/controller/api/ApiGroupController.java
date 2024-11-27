package com.wakedata.dw.open.controller.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.domain.ApiGroupDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.approval.vo.ApiGroupVO;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.util.WebUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.open.vo.AddApiGroupReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @author tanzhi
 * @title ApiGroupController
 * @date 2019/11/27 11:06
 * @projectName bdp-open
 * @descriptor
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/api_group")
@Api(value = "接口分类管理", tags = "接口分类管理")
@Validated
public class ApiGroupController extends BaseController {


    @Autowired
    private ApiGroupService apiGroupService;

    @PostMapping("/add")
    @ApiOperation("添加API接口分类")
    @AuditLog
    public ResultDTO<Boolean> addToWhiteList(@RequestBody @Valid AddApiGroupReq addApiGroupReq) {

        ApiGroupPo apiGroupPo = new ApiGroupPo();
        BeanUtils.copyProperties(addApiGroupReq, apiGroupPo);
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        checkAdmin(userInfo);
        apiGroupPo.setCreateUser(userInfo.getName());
        apiGroupPo.setLesseeId(userInfo.getLesseeId());
        apiGroupService.addApiGroup(apiGroupPo);
        return ResultDTO.success(Boolean.TRUE);
    }

    @PostMapping("/delete")
    @ApiOperation("删除API接口分类")
    @AuditLog
    public ResultDTO<Boolean> delete(Integer apiGroupId) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        checkAdmin(userInfo);
        apiGroupService.deleteApiGroup(apiGroupId);
        return ResultDTO.success(Boolean.TRUE);
    }

    @PostMapping("/update")
    @ApiOperation("修改API接口分类")
    public ResultDTO<Boolean> update(@RequestBody @Valid AddApiGroupReq addApiGroupReq) {
        ApiGroupPo apiGroupPo = new ApiGroupPo();
        BeanUtils.copyProperties(addApiGroupReq, apiGroupPo);
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        checkAdmin(userInfo);
        apiGroupPo.setUpdateTime(new Date());
            apiGroupService.updateApiGroup(apiGroupPo);
        return ResultDTO.success(Boolean.TRUE);
    }


    @GetMapping("/show")
    @ApiOperation("查询API接口分类")
    public ResultDTO<ApiGroupPo> show(Integer apiGroupId) {
        ApiGroupPo apiGroupPos = apiGroupService.show(apiGroupId);
        ResultDTO<ApiGroupPo> resultDTO = new ResultDTO<>();
        resultDTO.setData(apiGroupPos);
        return resultDTO;
    }

    @GetMapping("/pageList")
    @ApiOperation("查询API接口分类列表")
    @AuditLog
    public PageResultDTO<List<ApiGroupVO>> pageList(ApiGroupPo apiGroupPo, PageQuery pageQuery) {
        return apiGroupService.pageLike(apiGroupPo,pageQuery.getPageNo(),pageQuery.getPageSize());
    }


    @GetMapping("/list")
    @ApiOperation("查询API分组列表")
    public ResultDTO<List<ApiGroupPo>> list(ApiGroupPo apiGroupPo) {
//        AuthUtils.setAppId(apiGroupPo);
        List<ApiGroupPo> apiGroupPos = apiGroupService.find(apiGroupPo);
        ResultDTO<List<ApiGroupPo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(apiGroupPos);
        return resultDTO;
    }

    @GetMapping("/list/page/like")
    @ApiOperation("查询API接口分类列表")
    @AuditLog
    public PageResultDTO<Page<ApiGroupPo>> list(ApiGroupPo apiGroupPo, PageQuery pageQuery, String keyword) {
        AuthUtils.setAppId(apiGroupPo);
        //树列表结构,一级列表数据只查顶级目录
        apiGroupPo.setParentId(0);
        //变更成目录树后,兼容in
        Integer apiGroupId = apiGroupPo.getId();
        List<Integer> apiGroupIds = new ArrayList<>();
        if (apiGroupId != null) {
            apiGroupIds = apiGroupService.findGroupIds(apiGroupId);
            apiGroupPo.setId(null);
        }
        List<String> likeColumns = Arrays.asList("groupName", "groupDesc", "groupPath");
        Page<ApiGroupPo> apiGroupPos = apiGroupService.selectPageLikeOrderBy(apiGroupPo, pageQuery.getPageNo(), pageQuery.getPageSize(), keyword, likeColumns,
                "CREATE_TIME", false, null, null, null, "id", apiGroupIds);
        apiGroupService.buildApiGroupPos(apiGroupPos);
        PageResultDTO<Page<ApiGroupPo>> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount((int) apiGroupPos.getTotal());
        resultDTO.setData(apiGroupPos);
        return resultDTO;
    }

    @GetMapping("/list/tree")
    @ApiOperation("查询API接口分类树列表")
    @AuditLog
    public ResultDTO<List<ApiGroupPo>> list() {
        List<ApiGroupPo> apiGroupTreePos = apiGroupService.listGroupTree();
        ResultDTO result = new ResultDTO();
        result.setData(apiGroupTreePos);
        return result;
    }

    @PostMapping("/tree/add")
    @ApiOperation("添加API接口分类")
    @AuditLog
    public ResultDTO<ApiGroupPo> addGroupNode(@RequestBody @Valid AddApiGroupReq addApiGroupReq) {
        ApiGroupPo apiGroupPo = new ApiGroupPo();
        BeanUtils.copyProperties(addApiGroupReq, apiGroupPo);
        apiGroupPo.setCreateUser(WebUtils.getCurrentUserInfo().getAccount());
        ResultDTO result = new ResultDTO();
        result.setData(apiGroupService.addApiGroupNode(apiGroupPo));
        return result;
    }

    @PostMapping("/tree/update")
    @ApiOperation("更新API接口分类")
    @AuditLog
    public ResultDTO<ApiGroupPo> updateGroupNode(@RequestBody @Valid AddApiGroupReq addApiGroupReq) {
        ApiGroupPo apiGroupPo = new ApiGroupPo();
        BeanUtils.copyProperties(addApiGroupReq, apiGroupPo);
        apiGroupPo.setUpdateTime(new Date());
        ResultDTO result = new ResultDTO();
        result.setData(apiGroupService.updateApiGroupNode(apiGroupPo));
        return result;
    }

    @PostMapping("/tree/delete")
    @ApiOperation("删除API接口分类节点")
    @AuditLog
    public ResultDTO deleteGroupNode(@RequestParam Integer apiGroupId) {
        apiGroupService.deleteApiGroupNode(apiGroupId);
        return new ResultDTO();
    }


    @GetMapping("/list/api")
    @ApiOperation("查询带有API的分组信息")
    public ResultDTO<Map<String, List<ApiGroupDo>>> listWithApi() {
        Long lesseeId = AuthUtils.currentAppId();
        Map<String, List<ApiGroupDo>> apiGroupPos = apiGroupService.listWithApi(lesseeId);
        ResultDTO<Map<String, List<ApiGroupDo>>> resultDTO = new ResultDTO<>();
        resultDTO.setData(apiGroupPos);
        return resultDTO;
    }

    @GetMapping("/list/api_tree")
    @ApiOperation("查询带有API的分组信息树列表")
    public ResultDTO<List<ApiGroupPo>> listApiTree() {
        List<ApiGroupPo> apiGroupTreePos = apiGroupService.listGroupTree();
        ResultDTO result = new ResultDTO();
        result.setData(apiGroupTreePos);
        return result;
    }


    /**
     * 校验是否为管理员
     *
     * @param userInfo 用户信息
     */
    private void checkAdmin(IpaasUserInfo userInfo) {
        if (!userInfo.getIsPlatformAdmin()){
            throw new OpenException(MsgCodeEnum.w_auth_permission_denied);
        }
    }
}