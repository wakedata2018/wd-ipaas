package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.AppCountDo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title DataAccessAppMapper
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
public interface AppAccessMapper extends Mapper<AppAccessPo>, SelectByIdListMapper<AppAccessPo, Integer> {

    /**
     * 查询没有权限的密钥
     *
     * @param dataAccessApp
     * @return
     */
    List<AppAccessPo> selectWithOutSecret(AppAccessPo dataAccessApp);

    /**
     * 审批通过的总数
     * @param lesseeId 租户id
     * @return 审批总数
     */
    Integer count(@Param("lesseeId") Long lesseeId);

    /**
     * 求api条数
     * @param dataAssetId
     * @return
     */
    Integer countApi(@Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询接入的api引用个数
     * @param collect
     * @return
     */
    List<AppCountDo> countApiByList(List<Integer> collect);

    /**
     * 通过appid查应用的api id
     * @param appId
     * @return
     */
    List<ApiInfoDo> getAppReferenceApi(@Param("appId") Integer appId);

    /**
     * app接入在api中的授权时间
     * @param appId
     * @param apiId
     * @return
     */
    Date getAppAuthDate(@Param("appId") Integer appId,@Param("apiId") Integer apiId);

    /**
     * 权限回收时，展示授权的接入
     * @param apiId apiId
     * @return
     */
    List<AppAccessPo> listPermissionApp(@Param("apiId") Integer apiId);

    /**
     * 条件查询
     *
     * @param appAccessPo appAccessPo
     * @return List<AppAccessPo>
     */
    List<AppAccessPo> pageList(@Param("appAccessPo")AppAccessPo appAccessPo);
}
