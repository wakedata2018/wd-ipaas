package com.wakedata.dw.open.service.impl.api;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiGroupMapper;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.domain.ApiGroupDo;
import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.ApiDocumentDataAssetApiDTO;
import com.wakedata.dw.open.service.api.dto.ApiGroupDTO;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDTO;
import com.wakedata.dw.open.service.approval.vo.ApiGroupVO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.swagger.SwaggerInfoService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tanzhi
 * @title ApiGroupServiceImpl
 * @date 2019/11/27 11:04
 * @projectName bdp-open
 * @descriptor ApiGroupService的实现类
 */
@Service
public class ApiGroupServiceImpl extends BaseServiceImpl<ApiGroupPo, ApiGroupMapper> implements ApiGroupService {

    @Autowired
    private DataAssetApiService assetApiService;

    @Autowired
    private SwaggerInfoService swaggerInfoService;

    @Autowired
    @Override
    protected void init(CurdService<ApiGroupPo> curdService, ApiGroupMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Integer addApiGroup(ApiGroupPo apiGroupPo) {
        Date date = new Date();
        apiGroupPo.setCreateTime(date);
        apiGroupPo.setCreateTime(date);
        apiGroupPo.setParentId(DwOpenConstant.DEFAULT_PARENT_ID);
        apiGroupPo.setLevel(DwOpenConstant.DEFAULT_LEVEL);
        check(apiGroupPo, false);
        return add(apiGroupPo);
    }

    @Override
    public Map<String, List<ApiGroupDo>> listWithApi(Long lesseeId) {
        List<ApiGroupDo> list = getMapper().list(lesseeId);
        Map<String, List<ApiGroupDo>> map = new LinkedHashMap<>();
        for (ApiGroupDo apiGroupDo : list) {
            String groupName = apiGroupDo.getGroupName();
            Integer groupId = apiGroupDo.getApiGroupId();
            String apiTempGroupName = groupName + "____" + groupId;
            List<ApiGroupDo> apiGroupDos = map.get(apiTempGroupName);
            if (apiGroupDos == null) {
                apiGroupDos = new ArrayList<>();
                map.put(apiTempGroupName, apiGroupDos);
            }
            if (StringUtils.isBlank(apiGroupDo.getApiName())) {
                continue;
            }
            apiGroupDos.add(apiGroupDo);
        }
        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateApiGroup(ApiGroupPo apiGroupPo) {
        check(apiGroupPo, true);
        int i = getCurdService().updateByPrimaryKeySelective(apiGroupPo, getMapper());
        if (i != 1) {
            throw new OpenException(MsgCodeEnum.w_dao_update_error);
        }

        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setApiGroupId(apiGroupPo.getId());
        List<DataAssetApiPo> assetApiPos = assetApiService.find(dataAssetApiPo);
        if (CollectionUtils.isNotEmpty(assetApiPos)) {
            for (DataAssetApiPo assetApiPo : assetApiPos) {
                if (DataAssetPublishStatusEnum.PUBLISH.equals(assetApiPo.getDataAssetPublishStatus())) {
                    throw new OpenException(MsgCodeEnum.w_cannot_edit_published_api);
                }
            }
        }
        for (DataAssetApiPo assetApiPo : assetApiPos) {
//            String path = assetApiPo.getDataAssetApiMethod();
//            String[] split = path.split("/");
//            split[0] = apiGroupPo.getGroupPath();
//            assetApiPo.setDataAssetApiMethod(split[0] + "/" + split[1]);
            String url = StringUtils.substringAfter(assetApiPo.getDataAssetApiMethod(), "/");
            assetApiPo.setDataAssetApiMethod(apiGroupPo.getGroupPath().concat("/").concat(url));
            assetApiService.update(assetApiPo);
        }
        return i;
    }

    @Override
    public Integer deleteApiGroup(Integer apiGroupId) {

        // 校验该分组下是否存在API
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setApiGroupId(apiGroupId);
        List<DataAssetApiPo> assetApiPos = assetApiService.find(dataAssetApiPo);
        if (CollectionUtils.isNotEmpty(assetApiPos)) {
            throw new OpenException(MsgCodeEnum.w_api_group_have_api);
        }
        return getCurdService().deleteByPrimaryKey(apiGroupId, getMapper());
    }

    @Override
    public List<ApiGroupPo> listAllApi() {
        ApiGroupPo po = new ApiGroupPo();
        AuthUtils.setAppId(po);
        return Optional.ofNullable(getMapper().select(po)).orElse(new ArrayList<>());
    }

    @Override
    public List<ApiGroupPo> listGroupTree() {
        List<ApiGroupPo> apiGroupTreePos = this.getMapper().listParentNode(AuthUtils.currentAppId(), ApiGroupPo.DEFAULT_PARENT_GROUP_ID);

        // 所有租户都能查得到的父级主题Swagger导入
        apiGroupTreePos.add(this.getMapper().listParentNode(AuthUtils.SWAGGER_TENANT_ID, ApiGroupPo.DEFAULT_PARENT_GROUP_ID).get(0));
        buildTree(apiGroupTreePos);
        return apiGroupTreePos;
    }

    @Override
    public ApiGroupPo addApiGroupNode(ApiGroupPo apiGroupPo) {
        Date date = new Date();
        apiGroupPo.setCreateTime(date);
        apiGroupPo.setUpdateTime(date);
        check(apiGroupPo, false);
        AuthUtils.setAppId(apiGroupPo);
        add(apiGroupPo);
        return apiGroupPo;
    }

    @Override
    public ApiGroupPo addOrUpdateApiGroup(ApiGroupPo apiGroupPo) {
        apiGroupPo.setCreateTime(new Date());
        apiGroupPo.setUpdateTime(apiGroupPo.getCreateTime());
        AuthUtils.setAppId(apiGroupPo);
        this.getMapper().addOrUpdateApiGroup(apiGroupPo);
        return apiGroupPo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiGroupPo updateApiGroupNode(ApiGroupPo apiGroupPo) {
        check(apiGroupPo, true);
        int i = getCurdService().updateByPrimaryKeySelective(apiGroupPo, getMapper());
        if (i != 1) {
            throw new OpenException(MsgCodeEnum.w_api_group_not_exists);
        }
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setApiGroupId(apiGroupPo.getId());
        List<DataAssetApiPo> assetApiPos = assetApiService.find(dataAssetApiPo);
        if (CollectionUtils.isNotEmpty(assetApiPos)) {
            for (DataAssetApiPo assetApiPo : assetApiPos) {
                if (DataAssetPublishStatusEnum.PUBLISH.equals(assetApiPo.getDataAssetPublishStatus())) {
                    throw new OpenException(MsgCodeEnum.w_cannot_edit_published_api);
                }
            }
        }
        String path = apiGroupPo.getGroupPath();
        path = buildPath(apiGroupPo.getParentId(),path);
        for (DataAssetApiPo assetApiPo : assetApiPos) {
            String methodPath = assetApiPo.getDataAssetApiMethod();
            String substring = methodPath.substring(methodPath.lastIndexOf("/") + 1);
            assetApiPo.setDataAssetApiMethod(path + "/" + substring);
            assetApiService.update(assetApiPo);
        }
        return apiGroupPo;
    }

    @Override
    public void deleteApiGroupNode(Integer apiGroupId) {
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setApiGroupId(apiGroupId);
        List<DataAssetApiPo> assetApiPos = assetApiService.find(dataAssetApiPo);
        if (CollectionUtils.isNotEmpty(assetApiPos)) {
            throw new OpenException(MsgCodeEnum.w_has_published_api);
        }
        List<ApiGroupPo> apiGroupPos = this.getMapper().listParentNode(AuthUtils.currentAppId(), apiGroupId);
        if (CollectionUtils.isNotEmpty(apiGroupPos)) {
            throw new OpenException(MsgCodeEnum.w_api_group_reference);
        }
        getCurdService().deleteByPrimaryKey(apiGroupId, getMapper());
    }

    @Override
    public List<Integer> findGroupIds(Integer apiGroupId) {
        List<Integer> groupIds = new ArrayList<>();
        groupIds.add(apiGroupId);
        buildGroupIds(groupIds,apiGroupId);
        return groupIds;
    }

    @Override
    public void buildApiGroupPos(Page<ApiGroupPo> apiGroupPos) {
        buildTree(apiGroupPos);
    }

    @Override
    public ApiGroupPo checkForExist(Integer apiGroupId) {
        ApiGroupPo apiGroupPo = this.show(apiGroupId);
        if (apiGroupPo == null) {
            throw new OpenException((MsgCodeEnum.w_api_group_not_exists));
        }
        return apiGroupPo;
    }

    @Override
    public PageResultDTO<List<ApiGroupVO>> pageLike(ApiGroupPo apiGroupPo, int pageNo, int pageSize) {
        PageResultDTO<List<ApiGroupVO>> resultDTO = new PageResultDTO<>();
        PageHelper.startPage(pageNo, pageSize);
        List<ApiGroupPo> apiGroupPoList = this.getMapper().pageLike(apiGroupPo);
        if (apiGroupPoList.isEmpty()){
            return resultDTO;
        }
        PageInfo<ApiGroupPo> pageInfo = new PageInfo<>(apiGroupPoList);
        List<ApiGroupPo> list = pageInfo.getList();
        List<ApiGroupVO> dataList = BeanUtil.copyList(list, ApiGroupVO.class);

        resultDTO.setData(dataList);
        resultDTO.setPageNo(pageNo);
        resultDTO.setPageSize(pageSize);
        resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
        return resultDTO;
    }

    @Override
    public List<ApiGroupPo> queryApiGroupListByIds(List<Integer> apiGroupIdList) {
        return Optional.ofNullable(getMapper().queryApiGroupListByIds(apiGroupIdList)).orElse(new ArrayList<>());
    }

    @Override
    public List<ApiGroupDTO> queryApiGroupAndApi() {
        //查询所有API分组
        List<ApiGroupPo> apiGroupPoList = getMapper().selectAll();
        if (CollectionUtils.isEmpty(apiGroupPoList)) {
            return Collections.emptyList();
        }
        //获取所有分组的Id
        List<Integer> ids = apiGroupPoList.stream().map(ApiGroupPo::getId).collect(Collectors.toList());
        //根据分组ids获取所有的API信息
        List<DataAssetApiDTO> dataAssetApiDTOList = BeanUtil.copyList(assetApiService.findByGroupIds(ids), DataAssetApiDTO.class);
        //结果分组类型转换
        List<ApiGroupDTO> resultList = BeanUtil.copyList(apiGroupPoList, ApiGroupDTO.class);
        //结果list拼接
        for (ApiGroupDTO apiGroupDTO : resultList) {
            List<DataAssetApiDTO> list = new ArrayList<>();
            for (DataAssetApiDTO dataAssetApiDTO : dataAssetApiDTOList) {
                if (dataAssetApiDTO.getApiGroupId().equals(apiGroupDTO.getId())) {
                    list.add(dataAssetApiDTO);
                }
            }
            List<ApiDocumentDataAssetApiDTO> apiDocumentDataAssetApiDTOList = BeanUtil.copyList(list, ApiDocumentDataAssetApiDTO.class);
            apiGroupDTO.setApiList(apiDocumentDataAssetApiDTOList);
        }
        return resultList;
    }

    @Override
    public List<ApiGroupDTO> getContainApiGroupList(Long lesseeId) {
        List<ApiGroupPo> apiGroupPoList = getMapper().getContainApiGroupList(lesseeId);
        return BeanUtil.copyList(apiGroupPoList, ApiGroupDTO.class);
    }


    private void buildGroupIds(List<Integer> groupIds, Integer apiGroupId) {
        List<Integer> ids = this.getMapper().buildGroupIds(apiGroupId);
        if (CollectionUtils.isNotEmpty(ids)) {
            groupIds.addAll(ids);
            ids.forEach(id->{
                buildGroupIds(groupIds,id);
            });
        }
    }

    private String buildPath(Integer id,String path) {
        ApiGroupPo parentNodes = this.getMapper().findParentNodes(id);
        if (parentNodes != null) {
            path = parentNodes.getGroupPath() + "/" + path;
            buildPath(parentNodes.getParentId(),path);
        }
        return path;
    }

    private void buildTree(List<ApiGroupPo> apiGroupPos) {
        if (CollectionUtils.isNotEmpty(apiGroupPos)) {
            apiGroupPos.forEach(apiGroupPo -> {
                List<ApiGroupPo> apiChildGroups = this.getMapper().listParentNode(AuthUtils.currentAppId(), apiGroupPo.getId());
                apiGroupPo.setChildren(apiChildGroups);
                buildTree(apiChildGroups);
            });
        }
    }

    /**
     * 校验接口分组公共路径 接口分组名称以及接口分组编码是否重复
     *
     * @param apiGroupPo apiGroupPo
     * @param update update
     */
    private void check(ApiGroupPo apiGroupPo, boolean update) {

        // 检验是否绑定了swagger(更新接口公共路径时)
        if (update){
            ApiGroupPo check = getCurdService().selectByPrimaryKey(ApiGroupPo.builder().id(apiGroupPo.getId()).build(), getMapper());
            if (Objects.nonNull(check) && ObjectUtils.notEqual(check.getGroupPath(),apiGroupPo.getGroupPath())){
                List<SwaggerInfoPo> swaggerInfoPos = swaggerInfoService.find(SwaggerInfoPo.builder().apiGroupId(apiGroupPo.getId()).build());
                if (CollectionUtils.isNotEmpty(swaggerInfoPos)){
                    throw new OpenException(MsgCodeEnum.w_api_group_path_have_swagger);
                }
            }
        }

        // 校验接口公共路径是否重复
        ApiGroupPo check = new ApiGroupPo();
        check.setGroupPath(apiGroupPo.getGroupPath());
        check.setLevel(apiGroupPo.getLevel());
        List<ApiGroupPo> checkList = getCurdService().select(check, getMapper());
        if (CollectionUtils.isNotEmpty(checkList)) {
            if (update) {
                for (ApiGroupPo groupPo : checkList) {
                    if (groupPo.getGroupPath().equals(apiGroupPo.getGroupPath()) && !groupPo.getId().equals(apiGroupPo.getId())) {
                        throw new OpenException(MsgCodeEnum.w_api_group_path_exists);
                    }
                }
            } else {
                throw new OpenException(MsgCodeEnum.w_api_group_path_exists);

            }
        }

        // 校验接口分组名称是否重复
        check = new ApiGroupPo();
        check.setGroupName(apiGroupPo.getGroupName());
        checkList = getCurdService().select(check, getMapper());
        if (CollectionUtils.isNotEmpty(checkList)) {
            if (update) {
                for (ApiGroupPo groupPo : checkList) {
                    if (groupPo.getGroupName().equals(apiGroupPo.getGroupName()) && !groupPo.getId().equals(apiGroupPo.getId())) {
                        throw new OpenException(MsgCodeEnum.w_api_group_name_exists);
                    }
                }
            } else {
                throw new OpenException(MsgCodeEnum.w_api_group_name_exists);
            }
        }

        // 校验接口分组编码是否存在
        check = new ApiGroupPo();
        check.setGroupCode(apiGroupPo.getGroupCode());
        checkList = getCurdService().select(check, getMapper());
        if (CollectionUtils.isNotEmpty(checkList)) {
            if (update) {
                for (ApiGroupPo groupPo : checkList) {
                    if (groupPo.getGroupCode().equals(apiGroupPo.getGroupCode()) && !groupPo.getId().equals(apiGroupPo.getId())) {
                        throw new OpenException(MsgCodeEnum.w_api_group_code_exists);
                    }
                }
            } else {
                throw new OpenException(MsgCodeEnum.w_api_group_code_exists);
            }
        }
    }
}
