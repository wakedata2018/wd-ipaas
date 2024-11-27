package com.wakedata.dw.open.mapper.auth;

import com.wakedata.dw.open.model.auth.AuthCountDo;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.Date;
import java.util.List;

/**
 * @author chenshaopeng
 * @date 2021/11/1
 */
public interface AuthInfoMapper extends Mapper<AuthInfoPo>, InsertListMapper<AuthInfoPo> {
    /**
     * 查询已授权的API数量
     *
     * @param collect 传入一个id的List集合
     * @return List<AuthCountDo>
     */
    List<AuthCountDo> countApiByList(List<Integer> collect);

    /**
     * 查询已授权的API的Id数组
     *
     * @param id 传入一个第三方授权管理的id
     * @return List<Integer> 返回关联表中与第三方授权管理的id关联的所有第三方应用apiId
     */
    List<Integer> selectThirdPartyApiId(Integer id);

    /**
     * 查询已授权的API的Id和对应的API名称
     *
     * @param idList apiId数组
     * @return List<Integer> 返回关联表中与第三方授权管理的id关联的所有第三方应用apiId
     */
    List<ApiInfoDo> selectApiIdAndName(List<Integer> idList);

    /**
     * 根据API的Id查询关联表中已授权的外部应用
     *
     * @param id API的ID
     * @return List<AuthInfoPo> 返回外部应用列表
     */
    List<AuthInfoPo> queryExternalApplication(Integer id);

    /**
     * 通过关联表查询当前API中最后授权的时间
     *
     * @param apiId  传入API的Id
     * @param authId 第三方应用Id
     * @return AuthInfoPo 返回一个外部应用
     */
    Date queryAuthorizationTime(@Param("apiId") Integer apiId, @Param("authId") Integer authId);

    /**
     * 通过关联表查询当前API中最后授权的时间
     *
     * @param apiId  传入API的Id
     * @param authId 第三方应用Id
     * @return AuthInfoPo 返回一个外部应用
     */
    int deleteAuthorization(@Param("apiId") Integer apiId, @Param("authId") Integer authId);

    /**
     * 根据ID集合查询第三方应用详情信息
     *
     * @param idList 第三方应用ID集合
     * @return 第三方应用集合
     */
    List<AuthInfoPo> selectByIds(List<Integer> idList);

}
