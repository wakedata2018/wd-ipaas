package com.wakedata.dw.open.service.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.SqlAnalysisDo;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;
import com.wakedata.dw.open.model.api.dto.ApiInfoDTO;
import com.wakedata.dw.open.model.api.dto.AppAuthApiReqDTO;
import com.wakedata.dw.open.model.api.dto.AuthApiRespDTO;
import com.wakedata.dw.open.model.api.dto.NotAuthApiReqParam;
import com.wakedata.dw.open.model.domain.ApiCollectDo;
import com.wakedata.dw.open.model.domain.ApiTimesDo;
import com.wakedata.dw.open.model.domain.ApiTotalCountDo;
import com.wakedata.dw.open.model.domain.AppTimesDo;
import com.wakedata.dw.open.model.query.ApiQuery;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDTO;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDetailDTO;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.service.vo.DocumentApiDetailVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author yiyufeng
 * @title
 * @projectName bdp-open
 * @date
 * @description
 */
public interface DataAssetApiService extends BaseDbService<DataAssetApiPo> {

    /**
     * 添加
     *
     * @param dataAssetApi
     * @return
     */
    Boolean addApi(DataAssetApiPo dataAssetApi);

    /**
     * 添加
     *
     * @param apiDetailVo
     * @return
     */
    Boolean addApi(ApiDetailVo apiDetailVo);

    /**
     * 校验Redis锁配置，开启Redis锁才进行参数校验
     *
     * @param attr Redis锁配置
     */
    void checkRedisConfig(RedisLockConfigAttr attr);

    /**
     * 更新API
     *
     * @param apiDetailVo ApiDetailVo
     * @return true：更新成功 false：更新失败
     */
    Boolean updateApi(ApiDetailVo apiDetailVo);


    /**
     * 新的发布
     *
     * @param apiDetailVo
     * @return
     */
    Boolean publishDataAssetApi(ApiDetailVo apiDetailVo);

    /**
     * 发布API
     *
     * @param dataAssetApiId
     * @return
     */
    Boolean publishDataAssetApi(String username, Integer dataAssetApiId);

    /**
     * 新的下线
     *
     * @param dataAssetApiId
     * @return
     */
    Boolean unPublishDataAssetApi(Integer dataAssetApiId);

    /**
     * api批量上线
     *
     * @param publisher       发布者
     * @param dataAssetApiIds API 的Id列表
     * @return Boolean
     */
    Boolean dataAssetApiBatchPublish(String publisher, List<Integer> dataAssetApiIds);

    /**
     * api批量下线
     *
     * @param dataAssetApiIds API 的Id列表
     * @param publishStatus   api状态（入参管理决定上下线）
     * @return Boolean
     */
    Boolean dataAssetApiBatchUnPublish(List<Integer> dataAssetApiIds, DataAssetPublishStatusEnum publishStatus);


    /**
     * 添加负责人
     *
     * @param userIdentification
     * @param appendInChargeEmployeeNumber
     * @param dataAssetApiId
     * @return
     */
    Boolean appendInCharge(String userIdentification, String appendInChargeEmployeeNumber, Integer dataAssetApiId);

    /**
     * 删除负责人
     *
     * @param userIdentification
     * @param removeInChargeEmployeeNumber
     * @param dataAssetApiId
     * @return
     */
    Boolean removeInCharge(String userIdentification, String removeInChargeEmployeeNumber, Integer dataAssetApiId);

    /**
     * 批量
     *
     * @param list
     * @return
     */
    List<DataAssetApiPo> selectByIdList(List<Integer> list);

    /**
     * api详情
     *
     * @param dataAssetApiId
     * @return
     */
    DataAssetApiPo detail(Integer dataAssetApiId);

    ApiDetailVo detailVo(Integer dataAssetApiId, List<ApiConditionPo> parameters);

    /**
     * 根据API分组、API类型查询API详情集合
     *
     * @param apiGroupIdList    开放平台API分组ID集合
     * @param apiTypeList       开放平台API类型集合，对应枚举类型DataAssetEnums.DataApiType
     * @param isRemoveGroupPath 是否移除接口分组前缀
     * @return API详情集合
     */
    List<ApiDetailVo> queryApiListByGroupIdsAndTypes(List<Integer> apiGroupIdList, List<Integer> apiTypeList, Boolean isRemoveGroupPath);

    /**
     * 首页API列表，关联收藏
     *
     * @param username
     * @return
     */
    List<ApiCollectDo> listIndex(String username, Integer dataAssetId, Integer appId);

    /**
     * @param dataAssetApiId
     * @return
     */
    Integer deleteApi(Integer dataAssetApiId);

    /**
     * 我收藏的API
     *
     * @param username
     * @param pageQuery
     * @param appId
     * @param keyword
     * @return
     */
    Page<DataAssetApiPo> myCollect(String username, PageQuery pageQuery, Integer appId, Integer dataAssetId, String keyword);


    /**
     * Api详情监控信息
     *
     * @param dataAssetId
     * @return
     */
    ResultDTO<ApiTotalCountDo> assetMonitorInfo(Integer dataAssetId);

    /**
     * 按天
     *
     * @param dataAssetId
     * @return
     */
    ResultDTO<Map<String, ApiTimesDo>> countTimesDay(Integer dataAssetId);

    /**
     * 周
     *
     * @param dataAssetId
     * @return
     */
    ResultDTO<Map<String, ApiTimesDo>> countTimesWeek(Integer dataAssetId);

    /**
     * 按天查qp或耗时
     *
     * @param dataAssetId
     * @param qps
     * @return
     */
    ResultDTO<Map<String, BigDecimal>> countQpsDay(Integer dataAssetId, boolean qps);

    /**
     * 按周查qps或耗时
     *
     * @param dataAssetId
     * @param qps
     * @return
     */
    ResultDTO<Map<String, BigDecimal>> countQpsWeek(Integer dataAssetId, boolean qps);

    /**
     * api调用排行，天
     *
     * @param dataAssetId
     * @return
     */

    ResultDTO<List<AppTimesDo>> countAppTimesDay(Integer dataAssetId);

    /**
     * api调用排行,周
     *
     * @param dataAssetId
     * @return
     */
    ResultDTO<List<AppTimesDo>> countAppTimesWeek(Integer dataAssetId);

    /**
     * 自定义sql解析
     *
     * @param sql          sql
     * @param dataSourceId 数据源id
     * @return 参数解析的数据
     */
    SqlAnalysisDo analysisSql(String sql, Integer dataSourceId);

    /**
     * 通过api主题,查下游所有主题
     *
     * @param apiGroupId 主题ID
     * @return 主题列表
     */
    List<Integer> findGroupIds(Integer apiGroupId);

    DataAssetApiPo selectByApiPath(String apiPath);

    /**
     * 根据主键更新主表信息
     *
     * @param dataAssetApiPo
     * @return
     */
    Integer updateByApiId(DataAssetApiPo dataAssetApiPo);

    /**
     * 根据应用id、分组id和名称查询授权api
     *
     * @param dto 请求参数
     * @return 列表
     */
    PageResultDTO<List<AuthApiRespDTO>> listByAppIdGroupIdName(AppAuthApiReqDTO dto);

    /**
     * 查询应用还没有授权的api
     *
     * @param param 参数
     * @return api列表
     */
    PageResultDTO<List<DataAssetApiPo>> listNotAuthApi(NotAuthApiReqParam param);

    /**
     * 根据API ID获取节点信息（无论是编排或者单个API）
     *
     * @param dataAssetApiId dataAssetApiId API ID
     * @return 节点信息集合（如果非编排只有一个只有一个元素）
     */
    List<ApiInfoDTO> getApiInfos(Integer dataAssetApiId);

    /**
     * 条件查询api管理列表
     *
     * @param query 查询条件
     * @return api管理列表
     */
    PageResultDTO<List<DataAssetApiDTO>> pageList(ApiQuery query);

    /**
     * 校验apiPath是否已经存在
     *
     * @param dataAssetApi DataAssetApiPo
     */
    void checkApiMethodExists(DataAssetApiPo dataAssetApi);

    /**
     * 根据API Path集合查询对应的API数据
     *
     * @param dataAssetApiMethodList API Path集合
     * @return API数据集合
     */
    List<DataAssetApiPo> findByDataAssetApiMethodList(List<String> dataAssetApiMethodList);

    /**
     * 根据分组id列表查询API列表
     *
     * @param groupIds API分组Id列表
     * @return API数据集合
     */
    List<DataAssetApiPo> findByGroupIds(List<Integer> groupIds);

    /**
     * 校验api名称是否重复
     *
     * @param apiName api名称
     * @return true/false
     */
    Boolean checkApiName(String apiName);

    /**
     * 校验apiPath是否存在
     *
     * @param apiPath api路径
     * @return true/false
     */
    Boolean checkApiPath(String apiPath);

    /**
     * API文档：解析出符合文档需要的API详情
     *
     * @param apiDetailVo API详情
     * @return true/false
     */
    DocumentApiDetailVo parseApiDetailOfDocument(ApiDetailVo apiDetailVo);

    /**
     * 获取发布后的api列表
     *
     * @param apiGroupId
     * @param lesseeId
     * @return
     */
    List<DataAssetApiDetailDTO> getPublishApiListByGroupId(Integer apiGroupId, Long lesseeId);
}