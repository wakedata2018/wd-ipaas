package com.wakedata.dw.open.service.auth;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.api.dto.AuthInfoDTO;
import com.wakedata.dw.open.model.auth.AuthCountDo;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.vo.ApiAuthorizationVo;

import java.util.Date;
import java.util.List;


/**
 * 认证信息服务
 *
 * @author chenshaopeng
 * @date 2021/11/8
 */
public interface AuthInfoService extends BaseDbService<AuthInfoPo> {

    /**
     * 新增认证信息
     *
     * @param authInfo 新增对象
     */
    void addAuthInfo(AuthInfoPo authInfo);

    /**
     * 修改认证信息
     *
     * @param authInfo 修改对象
     */
    void updateAuthInfo(AuthInfoPo authInfo);

    /**
     * 删除认证信息
     *
     * @param authInfoId 主键
     */
    void deleteAuthInfo(Integer authInfoId);

    /**
     * 获取认证信息详情
     *
     * @param authInfoId 主键
     * @return 认证信息对象
     */
    AuthInfoPo detail(Integer authInfoId);

    /**
     * 分页查询认证信息
     *
     * @param pageQuery   分页对象
     * @param keyword     关键字
     * @param likeColumns 关键字查询列
     * @return 分页查询结果
     */
    Page<AuthInfoPo> selectPageLikeOrderBy(PageQuery pageQuery, String keyword, List<String> likeColumns);

    /**
     * 查询已授权的api个数
     *
     * @param collect 传入第三方应用的id列表
     * @return List<AuthInfoPo> 返回一个list
     */
    List<AuthCountDo> queryApiReference(List<Integer> collect);

    /**
     * 查询已授权的api的Id和名称
     *
     * @param id 传入第三方应用管理的id
     * @return List<ThirdPartyApiVo> 返回一个第三方应用API对象的list
     */
    List<ApiInfoDo> queryThirdPartyApi(Integer id);

    /**
     * 通过关联表查询当前API中已授权的外部应用
     *
     * @param id 传入API的Id
     * @return List<AuthInfoPo> 返回一个外部应用列表
     */
    List<AuthInfoPo> queryExternalApplication(Integer id);

    /**
     * 根据无结点API ID查询已授权的第三方应用
     *
     * @param apiId API ID
     * @return 授权的第三方应用信息集合
     */
    List<AuthInfoDTO> getAuthApplicationList(Integer apiId);

    /**
     * 通过关联表查询当前API中最后授权的时间
     *
     * @param apiId  传入API的Id
     * @param authId 第三方应用Id
     * @return AuthInfoPo 返回一个外部应用
     */
    Date queryAuthorizationTime(Integer apiId, Integer authId);

    /**
     * 查询api下面所有的结点以及结点对应的已授权的第三方应用
     *
     * @param dataAssetId     传入API的Id
     * @param dataAccessAppId 第三方应用Id
     * @return AuthInfoPo 返回一个外部应用
     */
    List<ApiAuthorizationVo> queryApiAndApplication(Integer dataAssetId, Integer dataAccessAppId);

    /**
     * 通过api和appId解除授权
     *
     * @param apiId  传入API的Id
     * @param authId 第三方应用Id
     * @return Boolean 返回一个解除授权的结果
     */
    boolean deleteAuthorization(Integer apiId, Integer authId);

    /**
     * 根据ID集合查询第三方应用详情信息
     *
     * @param idList 第三方应用ID集合
     * @return 第三方应用集合
     */
    List<AuthInfoPo> selectByIds(List<Integer> idList);

}