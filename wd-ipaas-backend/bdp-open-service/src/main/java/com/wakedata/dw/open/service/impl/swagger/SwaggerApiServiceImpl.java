package com.wakedata.dw.open.service.impl.swagger;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.SwaggerApiImportStatusEnum;
import com.wakedata.dw.open.enums.SwaggerExecuteStatusEnum;
import com.wakedata.dw.open.enums.SwaggerImportTypeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.RespParamMappingRuleMapper;
import com.wakedata.dw.open.mapper.swagger.SwaggerApiMapper;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.query.SwaggerApiQuery;
import com.wakedata.dw.open.model.swagger.SwaggerApiDO;
import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.SwaggerApiDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.impl.api.attr.HttpExternalApiAttrService;
import com.wakedata.dw.open.service.impl.swagger.helper.SwaggerInfoHelper;
import com.wakedata.dw.open.service.impl.swagger.helper.TemporaryApiImportEnum;
import com.wakedata.dw.open.service.impl.swagger.helper.TemporaryApiParseUtil;
import com.wakedata.dw.open.service.listener.SwaggerRespParamParsedEvent;
import com.wakedata.dw.open.service.swagger.SwaggerApiService;
import com.wakedata.dw.open.service.swagger.SwaggerInfoService;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.swagger.SwaggerApiDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangChenSheng
 * date 2022/8/24 10:46
 */
@Service
public class SwaggerApiServiceImpl extends BaseServiceImpl<SwaggerApiDO, SwaggerApiMapper> implements SwaggerApiService {

    @Autowired
    @Override
    protected void init(CurdService<SwaggerApiDO> curdService, SwaggerApiMapper mapper) {
        super.set(curdService, mapper);
    }

    @Resource
    private DataAssetApiService dataAssetApiService;

    @Resource
    private ApiGroupService apiGroupService;

    @Resource
    private SwaggerInfoService swaggerInfoService;

    @Resource
    private HttpExternalApiAttrService httpExternalApiAttrService;

    @Resource
    private SwaggerApiMapper swaggerApiMapper;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private SwaggerInfoHelper swaggerInfoHelper;

    @Resource
    private RespParamMappingRuleMapper respParamMappingRuleMapper;

    @Override
    public PageResultDTO<List<SwaggerApiDTO>> pageList(SwaggerApiQuery query) {
        PageResultDTO<List<SwaggerApiDTO>> resultDTO = new PageResultDTO<>();
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<SwaggerApiDO> pageList = this.getMapper().selectPageList(query);
        PageInfo<SwaggerApiDO> pageInfo = new PageInfo<>(pageList);
        if (pageList.isEmpty()) {
            resultDTO.setData(new ArrayList<>());
            resultDTO.setPageNo(query.getPageNo());
            resultDTO.setPageSize(query.getPageSize());
            resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
            return resultDTO;
        }
        List<SwaggerApiDO> list = pageInfo.getList();
        List<SwaggerApiDTO> dataList = BeanUtil.copyList(list, SwaggerApiDTO.class);
        resultDTO.setData(dataList);
        resultDTO.setPageNo(query.getPageNo());
        resultDTO.setPageSize(query.getPageSize());
        resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
        return resultDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultDTO<Boolean> addSwaggerApi(SwaggerApiDTO swaggerApiDTO) throws Exception {

        // 获取swagger基本信息
        SwaggerInfoPo swaggerInfo = Optional.ofNullable(
                        swaggerInfoService.detail(swaggerApiDTO.getSwaggerId()))
                .orElseThrow(() -> new OpenException(MsgCodeEnum.w_swagger_info_not_exists));
        ApiGroupPo apiGroupPo = apiGroupService.checkForExist(swaggerInfo.getApiGroupId());

        // 判断该Swagger的导入状态(未导入则新增临时Api,部分导入和全部导入则不新增Api)
        if (ObjectUtil.notEqual(swaggerInfo.getApiAmount(), SwaggerInfoPo.DEFAULT_API_AMOUNT)
                || ObjectUtil.notEqual(swaggerInfo.getExecuteStatus(), SwaggerExecuteStatusEnum.UN_EXECUTE.getValue())) {
            return ResultDTO.success(Boolean.TRUE);
        }

        // 实例化并转换swagger api数据
        SwaggerImportTypeEnum swaggerImportType = swaggerInfo.getImportType() == null ? SwaggerImportTypeEnum.URL : swaggerInfo.getImportType();
        SwaggerApiDetail swaggerApiDetail = new SwaggerApiDetail(
                SwaggerImportTypeEnum.URL == swaggerInfo.getImportType() ? swaggerInfo.getSwaggerUrl() : null,
                apiGroupPo.getGroupPath(),
                SwaggerImportTypeEnum.URL == swaggerInfo.getImportType() ? null : swaggerInfo.getSwaggerJson(),
                swaggerImportType);
        swaggerApiDetail.setApiDomainName(swaggerInfo.getApiDomainName());
        swaggerApiDetail.transition();
        List<ApiDetailVo> apiDetailVoList = swaggerApiDetail.getApiDetailList();
        if (CollectionUtils.isEmpty(apiDetailVoList)) {
            return ResultDTO.success(Boolean.TRUE);
        }

        // 初始化SwaggerApi对象并批量插入
        List<SwaggerApiDO> swaggerApiDOList = new ArrayList<>();
        apiDetailVoList.forEach(apiDetailVo -> {
            apiDetailVo.getDataAssetApi().setApiGroupId(apiGroupPo.getId());
            swaggerApiDOList.add(TemporaryApiParseUtil.initTemporaryApiDO(apiDetailVo, swaggerApiDTO));
        });
        swaggerApiMapper.batchInsert(swaggerApiDOList);

        // 更新Swagger信息(解析得到的SwaggerApi数量)
        swaggerInfo.setApiAmount(apiDetailVoList.size());
        swaggerInfo.setParseTime(new Date());
        swaggerInfoService.update(swaggerInfo);

        return ResultDTO.success(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> deleteBatch(List<Integer> ids) {
        Example example = new Example(SwaggerApiDO.class);
        example.createCriteria().andIn("id", ids);
        return ResultDTO.success(getMapper().deleteByExample(example) > 0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultDTO<Boolean> againImportTemporaryApi(SwaggerApiDTO swaggerApiDTO) throws Exception {
        Integer swaggerId = swaggerApiDTO.getSwaggerId();

        // 更新Swagger信息为未导入
        SwaggerInfoPo swaggerInfoPo = new SwaggerInfoPo();
        swaggerInfoPo.setId(swaggerId);
        swaggerInfoPo.setApiAmount(SwaggerInfoPo.DEFAULT_API_AMOUNT);
        swaggerInfoPo.setExecuteStatus(SwaggerExecuteStatusEnum.UN_EXECUTE.getValue());
        swaggerInfoService.update(swaggerInfoPo);

        // 删除当前Swagger下的临时Api
        this.deleteBySwaggerId(swaggerId);

        // 重新拉取
        return this.addSwaggerApi(swaggerApiDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized ResultDTO<String> addApiFromSwagger(SwaggerApiDTO swaggerApiDTO) {
        List<SwaggerApiDO> list = new ArrayList<>();

        // 获取和校验Swagger分组信息
        SwaggerInfoPo swaggerInfo = swaggerInfoHelper.checkForExist(swaggerApiDTO.getSwaggerId());
        ApiGroupPo apiGroupPo = apiGroupService.checkForExist(swaggerInfo.getApiGroupId());
        List<SwaggerApiDTO> swaggerApiDTOList = this.findBySwaggerId(swaggerApiDTO.getSwaggerId());
        if (swaggerApiDTOList.isEmpty()) {
            throw new OpenException(MsgCodeEnum.w_swagger_have_not_api);
        }

        Map<Integer, SwaggerApiDTO> swaggerApiMap;
        swaggerApiDTOList.forEach(apiDTO -> apiDTO.setDataAssetApiMethod(apiDTO.getDataAssetApiMethod().replaceAll("\\{|\\}", "")));
        swaggerApiMap = swaggerApiDTOList.stream().collect(Collectors.toMap(SwaggerApiDTO::getId, apiDTO -> apiDTO, (id, dto) -> dto));

        // 不选时代表全部导入
        List<Integer> idList = CollectionUtil.isEmpty(swaggerApiDTO.getIds())
                ? swaggerApiDTOList.stream().map(SwaggerApiDTO::getId).collect(Collectors.toList())
                : swaggerApiDTO.getIds();

        List<AbstractApiAttr> httpApiAttrs = new ArrayList<>(swaggerApiDTOList.size());
        if (ObjectUtil.isEmpty(respParamMappingRuleMapper.selectByPrimaryKey(swaggerInfo.getRespMappingRule()))) {
            swaggerInfo.setRespMappingRule(DwOpenConstant.DEFAULT_RESPONSE_PARAM_MAPPING_RULE_ID);
        }
        for (Integer id : idList) {
            SwaggerApiDTO swaggerApiInfo = swaggerInfoHelper.checkSwaggerApiDtoById(id, swaggerApiMap, false);

            // 普通导入临时API
            ApiDetailVo apiDetailVo = TemporaryApiParseUtil.initApiDetailVo(swaggerApiInfo, swaggerInfo.getRespMappingRule(), swaggerApiDTO.getInCharge());
            SwaggerApiDO swaggerApiDO = swaggerInfoHelper.addApiFromSwagger(apiDetailVo, id, TemporaryApiImportEnum.IMPORT_ADD);

            list.add(swaggerApiDO);
            if (!Objects.equals(SwaggerApiImportStatusEnum.SUCCESS_IMPORT.getValue(), swaggerApiDO.getImportStatus())) {
                continue;
            }
            httpApiAttrs.add(apiDetailVo.getDataAssetApi().getApiAttr());
        }

        //发布swagger解析完成事件
        if (CollectionUtil.isNotEmpty(httpApiAttrs)) {
            applicationEventPublisher.publishEvent(new SwaggerRespParamParsedEvent(this, httpApiAttrs));
        }

        // 更新swagger信息
        swaggerInfo.setApiGroupId(apiGroupPo.getId());
        swaggerInfo.setApiAmount(swaggerApiDTOList.size());
        swaggerInfo.setUpdateTime(new Date());
        swaggerInfo.setExecuteStatus(swaggerInfoHelper.executeStatusHandle(swaggerApiDTOList, idList));
        swaggerInfoService.update(swaggerInfo);

        return ResultDTO.success(buildMsg(list));
    }


    @Override
    public ResultDTO<String> convertDateAssetApiFromTemporary(SwaggerApiDTO swaggerApiDTO) {
        List<SwaggerApiDO> list = new ArrayList<>();

        // 获取和校验Swagger分组信息
        SwaggerInfoPo swaggerInfo = swaggerInfoHelper.checkForExist(swaggerApiDTO.getSwaggerId());
        List<SwaggerApiDTO> swaggerApiDTOList = this.findBySwaggerId(swaggerApiDTO.getSwaggerId());
        if (CollectionUtil.isEmpty(swaggerApiDTOList)) {
            throw new OpenException(MsgCodeEnum.w_swagger_have_not_api);
        }

        // 构建
        swaggerApiDTOList.forEach(apiDTO -> apiDTO.setDataAssetApiMethod(apiDTO.getDataAssetApiMethod().replaceAll("\\{|\\}", "")));
        Map<Integer, SwaggerApiDTO> swaggerApiMap =
                swaggerApiDTOList.stream().collect(Collectors.toMap(SwaggerApiDTO::getId, apiDTO -> apiDTO, (id, dto) -> dto));

        // 不选时代表全部导入
        List<Integer> idList = CollectionUtil.isEmpty(swaggerApiDTO.getIds())
                ? swaggerApiDTOList.stream().map(SwaggerApiDTO::getId).collect(Collectors.toList())
                : swaggerApiDTO.getIds();

        List<AbstractApiAttr> httpApiAttrs = new ArrayList<>(swaggerApiDTOList.size());
        for (Integer id : idList) {
            SwaggerApiDTO swaggerApiInfo = swaggerInfoHelper.checkSwaggerApiDtoById(id, swaggerApiMap, true);

            // 覆盖导入临时API
            HttpExternalApiAttr httpExternalApiAttr = TemporaryApiParseUtil.convertApiInfo(swaggerApiInfo.getApiInfo(), HttpExternalApiAttr.class, DwOpenConstant.API_ATTR);
            String httpPath = Optional.ofNullable(httpExternalApiAttr).map(HttpExternalApiAttr::getPath).orElse(null);
            List<HttpExternalApiAttr> attrByPath = httpExternalApiAttrService.queryAttrByPath(httpPath);
            List<Integer> apiIdList = attrByPath.stream().map(AbstractApiAttr::getApiId).collect(Collectors.toList());
            List<DataAssetApiPo> dataAssetApiPoList = CollectionUtil.isEmpty(apiIdList) ? new ArrayList<>() : dataAssetApiService.selectByIdList(apiIdList);
            List<Integer> dataAssetApiIdList = dataAssetApiPoList.stream()
                    .filter(dataAssetApiPo -> Objects.equals(dataAssetApiPo.getApiGroupId(), swaggerApiInfo.getApiGroupId()))
                    .map(DataAssetApiPo::getDataAssetApiId)
                    .collect(Collectors.toList());

            httpApiAttrs.add(
                    CollectionUtil.isEmpty(dataAssetApiIdList)
                            ? addApi(swaggerApiInfo, swaggerInfo.getRespMappingRule(), swaggerApiDTO.getInCharge(), list)
                            : updateApi(swaggerApiInfo, dataAssetApiIdList, swaggerInfo.getRespMappingRule(), swaggerApiDTO.getInCharge(), list));
        }

        //发布swagger解析完成事件
        if (CollectionUtil.isNotEmpty(httpApiAttrs)) {
            applicationEventPublisher.publishEvent(new SwaggerRespParamParsedEvent(this, httpApiAttrs));
        }

        // 更新swagger信息
        swaggerInfo.setApiAmount(swaggerApiDTOList.size());
        swaggerInfo.setUpdateTime(new Date());
        swaggerInfo.setExecuteStatus(swaggerInfoHelper.executeStatusHandle(swaggerApiDTOList, idList));
        swaggerInfoService.update(swaggerInfo);

        return ResultDTO.success(buildMsg(list));
    }

    @Override
    public List<SwaggerApiDTO> selectByExample(Example example) {
        List<SwaggerApiDO> swaggerApiDOList = getMapper().selectByExample(example);
        return BeanUtil.copyList(swaggerApiDOList, SwaggerApiDTO.class);
    }

    @Override
    public List<SwaggerApiDTO> findBySwaggerId(Integer swaggerId) {
        SwaggerApiDO swaggerApiDO = new SwaggerApiDO();
        swaggerApiDO.setSwaggerId(swaggerId);
        List<SwaggerApiDO> swaggerApiDOList = swaggerApiMapper.select(swaggerApiDO);
        return BeanUtil.copyList(swaggerApiDOList, SwaggerApiDTO.class);
    }

    @Override
    public Boolean updateImportStatus(SwaggerApiDO swaggerApiDO) {
        return getMapper().updateImportStatus(swaggerApiDO);
    }

    @Override
    public void deleteBySwaggerId(Integer swaggerId) {
        // 删除当前Swagger下的临时Api
        Example example = new Example(SwaggerApiDO.class);
        example.createCriteria().andEqualTo("swaggerId", swaggerId);
        if (this.getMapper().deleteByExample(example) < 0) {
            throw new OpenException(MsgCodeEnum.w_swagger_delete_fail);
        }
    }

    /**
     * 覆盖导入：新增
     */
    private HttpExternalApiAttr addApi(SwaggerApiDTO swaggerApiDTO, Integer respMappingRule, String inCharge, List<SwaggerApiDO> list) {
        if (ObjectUtil.isEmpty(respParamMappingRuleMapper.selectByPrimaryKey(respMappingRule))) {
            respMappingRule = DwOpenConstant.DEFAULT_RESPONSE_PARAM_MAPPING_RULE_ID;
        }
        ApiDetailVo apiDetailVo = TemporaryApiParseUtil.initApiDetailVo(swaggerApiDTO, respMappingRule, inCharge);
        SwaggerApiDO swaggerApiDO = swaggerInfoHelper.addApiFromSwagger(apiDetailVo, swaggerApiDTO.getId(), TemporaryApiImportEnum.CONVERT_IMPORT_ADD);

        list.add(swaggerApiDO);
        if (!Objects.equals(SwaggerApiImportStatusEnum.SUCCESS_IMPORT.getValue(), swaggerApiDO.getImportStatus())) {
            return null;
        }
        return (HttpExternalApiAttr) apiDetailVo.getDataAssetApi().getApiAttr();
    }

    /**
     * 覆盖导入：更新
     */
    private HttpExternalApiAttr updateApi(SwaggerApiDTO swaggerApiDTO, List<Integer> dataAssetApiIdList, Integer respMappingRule, String inCharge, List<SwaggerApiDO> list) {
        for (Integer dataAssetApiId : dataAssetApiIdList) {
            // 构建apiDetailVo并更新
            ApiDetailVo apiDetailVo = dataAssetApiService.detailVo(dataAssetApiId, null);
            TemporaryApiParseUtil.buildConvertApiDetailVo(apiDetailVo, swaggerApiDTO, respMappingRule, inCharge);
            SwaggerApiDO swaggerApiDO = swaggerInfoHelper.updateApiFromSwagger(apiDetailVo, swaggerApiDTO.getId());

            list.add(swaggerApiDO);
            if (!Objects.equals(swaggerApiDO.getImportStatus(), SwaggerApiImportStatusEnum.SUCCESS_IMPORT.getValue())) {
                return null;
            }
            return (HttpExternalApiAttr) apiDetailVo.getDataAssetApi().getApiAttr();
        }
        return null;
    }

    public String buildMsg(List<SwaggerApiDO> list) {

        int successApi = 0;
        int failApi = 0;
        for (SwaggerApiDO swaggerApiDO : list) {
            if (Objects.equals(swaggerApiDO.getImportStatus(), SwaggerApiImportStatusEnum.SUCCESS_IMPORT.getValue())) {
                successApi += 1;
            } else if (Objects.equals(swaggerApiDO.getImportStatus(), SwaggerApiImportStatusEnum.FAIL_IMPORT.getValue())) {
                failApi += 1;
            }
        }
        return String.format(DwOpenConstant.SWAGGER_IMPORT_MSG, list.size(), successApi, failApi);
    }

}
