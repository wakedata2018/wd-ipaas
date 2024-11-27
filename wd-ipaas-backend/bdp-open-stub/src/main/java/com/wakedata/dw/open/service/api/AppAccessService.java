package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.AppAccessListVo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.api.AppAccessVO;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.AppCountDo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.approval.vo.AppAccessVo;
import com.wakedata.wd.app.client.app.dto.AppBaseInfoDTO;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;

import java.util.Date;
import java.util.List;

/**
 * @author yiyufeng
 * @title DataAccessAppService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface AppAccessService extends SelectByIdListMapper<AppAccessPo, Integer>, BaseDbService<AppAccessPo> {

    /**
     * 新增接入端
     *
     * @param appAccess
     * @return
     */
    AppAccessPo createAccessApp(AppAccessPo appAccess);

    /**
     * 按名称搜索接入端
     *
     * @param appAccess
     * @return
     */
    List<AppAccessPo> searchAccessApp(AppAccessPo appAccess);

    /**
     * 根据应用获取接入应用
     *
     * @param appId
     * @return
     */
    AppAccessPo getAppAccess(String appId);

    /**
     * 修改接入端
     *
     * @param appAccess
     * @return
     */
    boolean editAccessApp(AppAccessPo appAccess);

    /**
     * 删除接入端
     *
     * @param dataAccessAppId
     * @return
     */
    boolean deleteAccessApp(Integer dataAccessAppId);

    /**
     * 重置接入端密码
     *
     * @param dataAccessAppId
     * @return
     */
    AppAccessPo resetAccessAppSecret(Integer dataAccessAppId);

    /**
     * 审批接入端
     *
     * @param appAccess
     * @return
     */
    AppAccessPo approvalAccessApp(AppAccessPo appAccess);


    /**
     * 查询接入的api引用个数
     * @param collect
     * @return
     */
    List<AppCountDo> queryApiReference(List<Integer> collect);

    /**
     * 查询app引用的api
     * @param appId
     * @return
     */
    List<ApiInfoDo> getAppReferenceApi(Integer appId);

    /**
     * app接入在api中的授权时间
     * @param appId
     * @param apiId
     * @return
     */
    Date getAppAuthDate(Integer appId, Integer apiId);

    /**
     * 权限回收时，展示授权的接入
     * @param apiId apiId
     * @return
     */
    List<AppAccessPo> listPermissionApp(Integer apiId);

    /**
     * 获取签名
     *
     * @param appId
     * @param appSecret
     * @param apiPath
     * @param createTime
     * @return
     */
    String getToken(String appId, String appSecret, String apiPath, Date createTime);

    /**
     * 校验签名
     *
     * @param token
     * @param appAccess
     * @param apiPath
     * @return
     */
    boolean verifyToken(String token, AppAccessPo appAccess, String apiPath);

    String normalization(String apiPath);

    /**
     * APP列表
     *
     * @param appAccess
     * @param apiId
     * @return
     */
    AppAccessListVo searchAccessAppWithDefault(AppAccessPo appAccess, Integer apiId);

    /**
     * 根据应用id查询应用详情
     *
     * @param dataAccessAppId 应用id
     * @return AppAccessPo
     */
    AppAccessVO appDetail(Integer dataAccessAppId);

    /**
     * 应用上线/下线
     *
     * @param dataAccessAppId 应用id
     * @param dataAccessAppId status
     * @return true/false
     */
    Boolean updateLine(Integer dataAccessAppId, Integer status);


    /**
     * 条件查询
     *
     * @param appAccessPo appAccessPo
     * @param pageNo pageNo
     * @param pageSize pageSize
     * @return PageResultDTO<List<AppAccessPo>>
     */
    PageResultDTO<List<AppAccessVo>> pageList(Long lesseeId, AppAccessPo appAccessPo, int pageNo, int pageSize);

    /**
     * 根据租户id查询应用信息
     * @param tenantId 惟客云租户信息
     * @return
     */
    ResultDTO<List<AppBaseInfoDTO>> queryAppInfo(Long tenantId);

    /**
     * 根据id查询应用
     * @param dataAssetAppId
     * @return
     */
    AppAccessPo queryAppInfoById(Integer dataAssetAppId);

    /**
     * 根据appKey获取应用
     * @param appKey
     * @return
     */
    AppAccessPo queryAppInfoByKey(String appKey);

    /**
     * 根据app指定前缀获取应用信息
     * @param apiPrefix
     * @return
     */
    AppAccessPo getAppAccessInfoByPrefix(String apiPrefix);
}
