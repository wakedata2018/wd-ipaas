package com.wakedata.dw.open.service.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.domain.ApiGroupDo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.api.dto.ApiGroupDTO;
import com.wakedata.dw.open.service.approval.vo.ApiGroupVO;

import java.util.List;
import java.util.Map;

/**
 * @author tanzhi
 * @title ApiGroupService
 * @date 2019/11/27 11:03
 * @projectName bdp-open
 * @descriptor
 */
public interface ApiGroupService extends BaseDbService<ApiGroupPo> {


    /**
     * 添加API分组
     * @param apiGroupPo
     * @return
     */
    Integer addApiGroup(ApiGroupPo apiGroupPo);

    /**
     * 例出所有的API
     * @return
     * @param lesseeId
     */
    Map<String, List<ApiGroupDo>> listWithApi(Long lesseeId);

    /**
     *
     *  修改API
     * @param apiGroupPo
     * @return
     */
    Integer updateApiGroup(ApiGroupPo apiGroupPo);

    /**
     *
     */
    Integer deleteApiGroup(Integer apiGroupId);

    /**
     * 查询所有api
     */
    List<ApiGroupPo> listAllApi();

    /**
     * 查询主题树
     * @return
     */
    List<ApiGroupPo> listGroupTree();

    /**
     * 新增目录树节点
     * @param apiGroupPo 节点实体
     * @return 新增后的实体
     */
    ApiGroupPo addApiGroupNode(ApiGroupPo apiGroupPo);

    /**
     * 新增或修改主题树（需唯一索引支持）
     * @param apiGroupPo 节点实体
     * @return 新增或修改后的实体
     */
    ApiGroupPo addOrUpdateApiGroup(ApiGroupPo apiGroupPo);

    /**
     * 修改目录节点
     * @param apiGroupPo 节点实体
     * @return 修改后的节点实体
     */
    ApiGroupPo updateApiGroupNode(ApiGroupPo apiGroupPo);

    /**
     * 删除节点
     * @param apiGroupId
     */
    void deleteApiGroupNode(Integer apiGroupId);

    /**
     * 通过api主题,查下游所有主题
     * @param apiGroupId 主题ID
     * @return 主题列表
     */
    List<Integer> findGroupIds(Integer apiGroupId);

    /**
     * 组装api主题子节点
     * @param apiGroupPos 主题列表
     */
    void buildApiGroupPos(Page<ApiGroupPo> apiGroupPos);

    /**
     * 根据id检查是否存在，存在返回，不存在抛异常
     *
     * @param apiGroupId 接口分类id
     * @return 接口分类项
     */
    ApiGroupPo checkForExist(Integer apiGroupId);

    /**
     * 分页查询接口分组管理
     *
     * @param apiGroupPo apiGroupPo
     * @param pageNo pageNo
     * @param pageSize pageSize
     * @return PageResultDTO<List<ApiGroupPo>>
     */
    PageResultDTO<List<ApiGroupVO>> pageLike(ApiGroupPo apiGroupPo, int pageNo, int pageSize);

    /**
     * 根据ids查询接口分组信息
     *
     * @param apiGroupIdList ids
     * @return 接口分组信息
     */
    List<ApiGroupPo> queryApiGroupListByIds(List<Integer> apiGroupIdList);

    /**
     * API文档：查询分组列表和Api名称（不需要登录）
     *
     * @return 接口分组信息
     */
    List<ApiGroupDTO> queryApiGroupAndApi();

    /**
     * 获取包含上线api的分组列表
     * @return
     */
    List<ApiGroupDTO> getContainApiGroupList(Long lesseeId);

}
