package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.domain.ApiGroupDo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author tanzhi
 * @title ApiGroupMapper
 * @date 2019/11/27 11:02
 * @projectName bdp-open
 * @descriptor
 */
public interface ApiGroupMapper extends Mapper<ApiGroupPo> {

    /**
     * 查出带关联api的api分组
     * @param lesseeId 租户id
     * @return 主题列表
     */
    List<ApiGroupDo> list(Long lesseeId);

    /**
     * 通过
     * @param lesseeId  租户ID
     * @param parentId 父级ID
     * @return 主题列表
     */
    List<ApiGroupPo> listParentNode(@Param("lesseeId") Long lesseeId,@Param("parentId") Integer parentId);

    /**
     * 通过parentId,查父级信息
     * @param id 父节点ID
     * @return 父节点信息
     */
    ApiGroupPo findParentNodes(@Param("id") Integer id);

    /**
     * 通过主题id查子主题id
     * @param apiGroupId 主题ID
     * @return 子主题列表
     */
    List<Integer> buildGroupIds(Integer apiGroupId);

    /**
     * 新增或修改主题（需唯一索引支持）
     *
     * 以"groupName,groupPath,parentId"的唯一索引作为新增条件
     *
     * @param apiGroup 节点实体
     * @return 新增或更新结果
     */
    int addOrUpdateApiGroup(@Param("apiGroup") ApiGroupPo apiGroup);

    /**
     * 条件查询
     *
     * @param apiGroupPo apiGroupPo
     * @return List<ApiGroupPo>
     */
    List<ApiGroupPo> pageLike(@Param("apiGroupPo")ApiGroupPo apiGroupPo);

    /**
     * 根据ids查询接口分组信息
     *
     * @param apiGroupIdList ids
     * @return 接口分组信息
     */
    List<ApiGroupPo> queryApiGroupListByIds(@Param("apiGroupIdList")List<Integer> apiGroupIdList);

    /**
     * 获取包含上线api的分组列表
     * @param lesseeId
     * @return
     */
    List<ApiGroupPo> getContainApiGroupList(@Param("lesseeId") Long lesseeId);
}
