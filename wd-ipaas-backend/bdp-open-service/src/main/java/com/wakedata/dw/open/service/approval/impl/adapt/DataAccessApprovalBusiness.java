package com.wakedata.dw.open.service.approval.impl.adapt;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.AppApprovalMapper;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.mapper.datasource.DataSourceMapper;
import com.wakedata.dw.open.model.api.*;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.approval.ApprovalBusiness;
import com.wakedata.dw.open.service.approval.vo.DataAccessApprovalDetailVo;
import com.wakedata.dw.open.service.approval.vo.DataAccessApprovalHistoryVo;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.utils.CollectionTransformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yiyufeng
 * @title DataAccessApprovalBusiness
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
public class DataAccessApprovalBusiness implements ApprovalBusiness {

    private static final String DATA_LOSE_MESSAGE = "数据已被删除";

    @Autowired
    private DataSourceMapper dataSourceMapper;
    @Autowired
    private AppApprovalMapper appApprovalMapper;
    @Autowired
    private DataAssetApiMapper dataAssetApiMapper;
    @Autowired
    private AppAccessRuleService appAccessRuleService;
    @Autowired
    private AppAccessService appAccessService;
    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Autowired
    private DataAssetService dataAssetService;
    @Autowired
    private CurdService<AppApprovalPo> curdService;

    @Autowired
    private ApiConditionMapper apiConditionMapper;

    @Override
    public ApprovalBusinessTypeEnum getBusinessType() {
        return ApprovalBusinessTypeEnum.DATA_ACCESS;
    }

    @Override
    public List<AppApprovalPo> analyzeApprovalBody(String userIdentification, Object approvalBody) {
        if (!(approvalBody instanceof List)) {
            throw new OpenException(MsgCodeEnum.w_data_approval_param_illegal);
        }
        List<AppAccessRuleDo> dataAccessRuleList = this.approvalBodyValidate((List) approvalBody);
        List<AppApprovalPo> result = new ArrayList<>(dataAccessRuleList.size());
        for (AppAccessRuleDo dataAccessRule : dataAccessRuleList) {
            //判断是否已有该申请
            Integer appId = dataAccessRule.getDataAccessAppId();
            Integer apiId = dataAccessRule.getDataAssetApiId();
            if (appApprovalMapper.selectApproveInfoId(appId, apiId, AuthUtils.currentAppId()).size() > 0
                && appAccessRuleService.searchDataAppRuleOfApi(appId,apiId).size() > 0) {
                throw new OpenException(MsgCodeEnum.w_approval_already_exist);
            }

            AppApprovalPo approval = new AppApprovalPo();
            DataAssetApiPo dataAssetApiPo = dataAssetApiMapper.selectByPrimaryKey(dataAccessRule.getDataAssetApiId());
            dataAccessRule.setDataAssetFieldName(dataAssetApiPo.getDataAssetName());
            approval.setApprovalBody(JSON.toJSONString(dataAccessRule));
            approval.setApprovalSubject(String.valueOf(dataAccessRule.getDataAccessAppId()));
            approval.setAccessAppId(appId);
            approval.setDataAssetId(apiId);
            result.add(approval);
        }
        return result;
    }


    private List<AppAccessRuleDo> approvalBodyValidate(List<AppAccessRuleDo> dataAccessRuleList) {
        // 校验请求参数不为空
        if (CollectionUtils.isEmpty(dataAccessRuleList)) {
            throw new OpenException(MsgCodeEnum.w_data_approval_create_empty);
        }
        Set<Integer> dataAccessAppIdSet = new HashSet<>();
        Set<Integer> dataAssetApiIdSet = new HashSet<>();
        for (AppAccessRulePo dataAccessRule : dataAccessRuleList) {
            Integer dataAccessAppId = dataAccessRule.getDataAccessAppId();
            if (null == dataAccessAppId) {
                throw new OpenException(MsgCodeEnum.w_data_approval_app_null);
            }
            dataAccessAppIdSet.add(dataAccessAppId);
            Integer dataAssetApiId = dataAccessRule.getDataAssetApiId();
            if (null == dataAssetApiId) {
                throw new OpenException(MsgCodeEnum.w_data_approval_api_null);
            }
            dataAssetApiIdSet.add(dataAssetApiId);
        }
        // 校验dataAccessAppId是否有效
        List<AppAccessPo> dataAccessAppList = this.appAccessService.selectByIdList(new ArrayList<>(dataAccessAppIdSet));
        if (CollectionUtils.isEmpty(dataAccessAppList) || dataAccessAppIdSet.size() - dataAccessAppList.size() != 0) {
            throw new OpenException(MsgCodeEnum.w_data_approval_app_not_exist);
        }
        // 校验dataAssetApiId是否有效
        List<DataAssetApiPo> dataAssetApiList = this.dataAssetApiService.selectByIdList(new ArrayList<>(dataAssetApiIdSet));
        if (CollectionUtils.isEmpty(dataAssetApiList) || dataAssetApiList.size() - dataAssetApiIdSet.size() != 0) {
            throw new OpenException(MsgCodeEnum.w_data_approval_api_not_exist);
        }
        // 校验请求参数中的field是否有效
        Map<Integer, Set<String>> dataAssetApiIdColumnMap = this.dataAssetApiId2ColumnMap(dataAssetApiList);
        List<AppAccessRuleDo> resultDataAccessRuleList = new ArrayList<>(dataAccessRuleList.size());
        for (AppAccessRuleDo dataAccessRule : dataAccessRuleList) {
            Set<String> tableColumnNameList = dataAssetApiIdColumnMap.get(dataAccessRule.getDataAssetApiId());

            for (String dataAccessRuleField : dataAccessRule.getDataAccessRuleFieldList()) {
                if (dataAccessRuleField.equals(HttpExternalApiAttr.ALL_FIELD)) {
                    continue;
                }
                if (!tableColumnNameList.contains(dataAccessRuleField)) {
                    throw new OpenException(MsgCodeEnum.w_data_approval_api_column_not_exist);
                }
            }
            AppAccessRuleDo tDataAccessRule = new AppAccessRuleDo();
            tDataAccessRule.setDataAccessAppId(dataAccessRule.getDataAccessAppId());
            tDataAccessRule.setDataAssetApiId(dataAccessRule.getDataAssetApiId());
            tDataAccessRule.setDataAccessRuleFieldList(dataAccessRule.getDataAccessRuleFieldList());
            resultDataAccessRuleList.add(tDataAccessRule);
        }
        return resultDataAccessRuleList;
    }

    private Map<Integer, Set<String>> dataAssetApiId2ColumnMap(List<DataAssetApiPo> dataAssetApiList) {
        // 获取数据API的所有field
        Map<Integer, Set<String>> map = new HashMap<>();
        for (DataAssetApiPo dataAssetApiPo : dataAssetApiList) {
            ApiConditionPo apiConditionPo = new ApiConditionPo();
            apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.RESULT);
            apiConditionPo.setDataAssetId(dataAssetApiPo.getDataAssetApiId());
            List<ApiConditionPo> select = apiConditionMapper.select(apiConditionPo);
            if (!CollectionUtils.isEmpty(select)) {
                HashSet<String> strings = new HashSet(select.stream().map(obj -> obj.getAssetColumns()).collect(Collectors.toList()));
                map.put(dataAssetApiPo.getDataAssetApiId(), strings);
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleApprovalSuccess(String userIdentification, AppApprovalPo approval) {
        AppAccessRuleDo appAccessRuleDo = JSON.parseObject(approval.getApprovalBody(), AppAccessRuleDo.class);
        // 获取对于该api的已有权限
        DataAssetApiPo dataAssetApiPo = dataAssetApiMapper.selectByPrimaryKey(approval.getDataAssetId());
        if (dataAssetApiPo == null || dataAssetApiPo.getDataAssetPublishStatus().equals(DataAssetPublishStatusEnum.UN_PUBLISH)) {
            throw new OpenException("该API暂未发布");
        }

        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setDataAssetId(approval.getDataAssetId());
        apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.RESULT);
        List<ApiConditionPo> select = apiConditionMapper.select(apiConditionPo);
        if (CollectionUtils.isEmpty(select)) {
            throw new OpenException("该API没有返回条件");
        }
        Set<String> set = new HashSet<>(select.size());
        for (ApiConditionPo conditionPo : select) {
            String assetColumns = conditionPo.getAssetColumns();
            set.add(assetColumns);
        }

        Set<String> dataAccessRuleFieldSet = appAccessRuleService.
                searchDataAppRuleOfApi(appAccessRuleDo.getDataAccessAppId(), appAccessRuleDo.getDataAssetApiId());
        // 权限去重
        List<String> dataAccessRuleFieldList = appAccessRuleDo.getDataAccessRuleFieldList();
        for (String s : dataAccessRuleFieldList) {
            if (!set.contains(s)) {
                throw new OpenException("您申请的API数据或已更改，本次申请失效，请找到对应的API重新申请");
            }

        }
        dataAccessRuleFieldList.removeIf(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return dataAccessRuleFieldSet.contains(s);
            }
        });

        appAccessRuleService.storeAppAccessRule(appAccessRuleDo);
        AppApprovalPo updatePo = new AppApprovalPo();
        updatePo.setApprovalStatus(ApprovalStatusEnum.APPROVAL);
        updatePo.setUpdateTime(new Date());
        updatePo.setApprovalId(approval.getApprovalId());
        updatePo.setApprovalMessage(approval.getApprovalMessage());
        curdService.updateByPrimaryKeySelective(updatePo, this.appApprovalMapper);
        return true;
    }

    @Override
    public boolean handleApprovalFail(String userIdentification, AppApprovalPo approval) {
        AppApprovalPo updatePo = new AppApprovalPo();
        updatePo.setApprovalId(approval.getApprovalId());
        updatePo.setApprovalStatus(ApprovalStatusEnum.FAILURE_APPROVAL);
        updatePo.setUpdateTime(new Date());
        updatePo.setApprovalMessage(approval.getApprovalMessage());
        curdService.updateByPrimaryKeySelective(updatePo, appApprovalMapper);
        return true;
    }

    @Override
    public Page<AppApprovalPo> listVisibleApproval(AppApprovalPo appApprovalPo, Integer pageNo, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNo, pageSize);
        Page<AppApprovalPo> appApprovalPos = (Page) this.appApprovalMapper.selectPageApproveInfo(appApprovalPo, keyword, null);
        return appApprovalPos;
    }

    @Override
    public DataAccessApprovalHistoryVo transformApprovalHistory(String userIdentification, AppApprovalPo approval) {
        DataAccessApprovalHistoryVo accessApprovalHistory = new DataAccessApprovalHistoryVo();
        DataAccessApprovalHistoryVo.copyApproval2ApprovalHistory(approval, accessApprovalHistory);
        // 获取数据资产信息
//        DataAccessRuleDo dataAccessRule = JSON.parseObject(approval.getApprovalBody(), DataAccessRuleDo.class);
        DataAssetApiPo dataAssetApi = dataAssetApiService.show(approval.getDataAssetId());
        accessApprovalHistory.setDataAssetName(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getDataAssetName());
        accessApprovalHistory.setDataAssetDescription(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getDataAssetDescription());
        accessApprovalHistory.setDataAssetApiUri(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getDataAssetApiMethod());
        accessApprovalHistory.setApiName(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getApiName());
        // 获取接入系统信息
        AppAccessPo dataAccessApp = appAccessService.show(approval.getAccessAppId());
        if (null == dataAccessApp) {
            accessApprovalHistory.setDataAccessAppName(DATA_LOSE_MESSAGE);
        } else {
            accessApprovalHistory.setDataAccessAppName(dataAccessApp.getDataAccessAppName());
        }
        return accessApprovalHistory;
    }

    @Override
    public DataAccessApprovalDetailVo transformApprovalDetail(String userIdentification, AppApprovalPo approval) {
        // TODO employee的校验
        DataAccessApprovalDetailVo dataAccessApprovalDetail = new DataAccessApprovalDetailVo();
        AppAccessRuleDo dataAccessRule = JSON.parseObject(approval.getApprovalBody(), AppAccessRuleDo.class);
        // 获取数据资产信息
        DataAssetApiPo dataAssetApi = dataAssetApiService.show(dataAccessRule.getDataAssetApiId());
        dataAccessApprovalDetail.setDataAssetName(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getDataAssetName());
        dataAccessApprovalDetail.setDataAssetDescription(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getDataAssetDescription());
        dataAccessApprovalDetail.setDataAssetApiUri(null == dataAssetApi ? DATA_LOSE_MESSAGE : dataAssetApi.getDataAssetApiUri());
        if (null == dataAssetApi) {
            dataAccessApprovalDetail.setApprovalColumnList(new ArrayList<>(0));
            return dataAccessApprovalDetail;
        }
        // 获取申请的列的信息
        List<DatasourceTableColumnDo> datasourceTableColumnList =
                dataAssetService.listDataAssetTableColumn(getDataSourcePo(dataAssetApi.getDataSourceId()), dataAssetApi.getDataAssetName());
        Map<String, DatasourceTableColumnDo> datasourceTableColumnMap =
                CollectionTransformUtils.list2Map(datasourceTableColumnList,
                                                  DatasourceTableColumnDo::getDatasourceTableColumnName);
        datasourceTableColumnList = new ArrayList<>(dataAccessRule.getDataAccessRuleFieldList().size());
        for (String fieldName : dataAccessRule.getDataAccessRuleFieldList()) {
            DatasourceTableColumnDo datasourceTableColumn = datasourceTableColumnMap.get(fieldName);
            if (null == datasourceTableColumn) {
                datasourceTableColumn = new DatasourceTableColumnDo();
                datasourceTableColumn.setDatasourceTableColumnName(fieldName);
                datasourceTableColumn.setDatasourceTableColumnType(DATA_LOSE_MESSAGE);
                datasourceTableColumn.setDatasourceTableColumnDesc(DATA_LOSE_MESSAGE);
            }
            datasourceTableColumnList.add(datasourceTableColumn);
        }
        dataAccessApprovalDetail.setApprovalColumnList(datasourceTableColumnList);
        return dataAccessApprovalDetail;
    }


    public DataSourcePo getDataSourcePo(Integer dataSourceId) {
        DataSourcePo dataSourcePo = dataSourceMapper.selectByPrimaryKey(dataSourceId);
        return dataSourcePo;
    }
}
