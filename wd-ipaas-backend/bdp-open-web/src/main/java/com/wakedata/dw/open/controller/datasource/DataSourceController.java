package com.wakedata.dw.open.controller.datasource;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnVO;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.vo.DataSourceConfigReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author tanzhi
 * @title DatasourceConfigController
 * @date 2019/11/21 20:30
 * @projectName dw-open
 * @descriptor
 */
@Api(value = "数据源管理", tags = "数据源管理")
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/datasource/")
public class DataSourceController extends BaseController {


    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DataAssetService dataAssetService;

    @PostMapping("/add")
    @ApiOperation("添加数据源")
    @AuditLog
    public ResultDTO addToWhiteList(@Valid DataSourceConfigReq dataSourceConfigReq) {
        DataSourcePo dataSourcePo = new DataSourcePo();
        BeanUtils.copyProperties(dataSourceConfigReq, dataSourcePo);
        dataSourceService.addDataSource(dataSourcePo);
        return new ResultDTO();
    }

    @PostMapping("/delete")
    @ApiOperation("删除数据源")
    @AuditLog
    public ResultDTO deleteFromWhiteList(Integer dataSourceId) {
        dataSourceService.deleteDataSource(dataSourceId);
        return new ResultDTO();
    }

    @GetMapping("/show")
    @ApiOperation("查询数据库配置")
    @AuditLog
    public ResultDTO<DataSourcePo> show(Integer dataSourceId) {
        DataSourcePo show = dataSourceService.show(dataSourceId);
        show.setCreateTime(null);
        show.setUpdateTime(null);
        show.setDbPassword(null);
        ResultDTO<DataSourcePo> resultDTO = new ResultDTO<>();
        resultDTO.setData(show);
        return resultDTO;
    }

    @PostMapping("/update")
    @ApiOperation("修改数据源")
    @AuditLog
    public ResultDTO update(@Valid DataSourceConfigReq dataSourceConfigReq) {
        DataSourcePo dataSourcePo = new DataSourcePo();
        BeanUtils.copyProperties(dataSourceConfigReq, dataSourcePo);
        dataSourceService.updateDataSource(dataSourcePo);
        ResultDTO resultDTO = new ResultDTO();
        return resultDTO;
    }


    @GetMapping("/test")
    @ApiOperation("测试数据源连通性")
    public ResultDTO test(DataSourcePo dataSourcePo) {
        boolean test = dataSourceService.test(dataSourcePo);
        ResultDTO resultDTO = new ResultDTO();
        if (!test) {
            resultDTO.setFailed(MsgCodeEnum.w_connection_error);
        }
        return resultDTO;
    }


    @GetMapping("/list")
    @ApiOperation("查询数据源列表")
    @AuditLog
    public ResultDTO<List<DataSourcePo>> list(DataSourcePo dataSourcePo) {
        AuthUtils.setAppId(dataSourcePo);
        List<DataSourcePo> list = dataSourceService.findByPo(dataSourcePo);
        for (DataSourcePo sourcePo : list) {
            sourcePo.setDbPassword(null);
        }
        ResultDTO<List<DataSourcePo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(list);
        return resultDTO;
    }

    @GetMapping("/list/table")
    @ApiOperation("查询指定数据源下的表")
    public ResultDTO<List<DatasourceTableDo>> listTables(Integer dataSourceId) {
        List<DatasourceTableDo> datasourceTableDos = dataAssetService.listDataAssetTable(dataSourceId);
        ResultDTO<List<DatasourceTableDo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(datasourceTableDos);
        return resultDTO;
    }

    @GetMapping("/list/table/page")
    @ApiOperation("查询指定数据源下的表")
    public PageResultDTO<List<DatasourceTableDo>> listTables(Integer dataSourceId,String keyword,PageQuery query) {
        Page<DatasourceTableDo> datasourceTableDos = dataAssetService.listDataAssetTablePaged(dataSourceService.show(dataSourceId),keyword,query.getPageNo(),query.getPageSize());
        PageResultDTO<List<DatasourceTableDo>> pageResultDTO = new PageResultDTO<>();
        pageResultDTO.setData(datasourceTableDos.getResult());
        pageResultDTO.setTotalCount((int)datasourceTableDos.getTotal());
        return pageResultDTO;
    }

    @GetMapping("/list/table/column")
    @ApiOperation("查询指定数据源下表的所有列")
    public ResultDTO<List<DatasourceTableColumnDo>> listTableColumns(Integer dataSourceId, String tableName) {
        List<DatasourceTableColumnDo> datasourceTableColumnDos = dataAssetService.listDataAssetTableColumns(dataSourceId, tableName);
        ResultDTO<List<DatasourceTableColumnDo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(datasourceTableColumnDos);
        return resultDTO;
    }

    @GetMapping("/list/table/column.v2")
    @ApiOperation(value = "查询指定数据源下表的所有列V2", tags = "查询指定数据源下表的所有列V2", httpMethod = "GET")
    public ResultDTO<DatasourceTableColumnVO> listTableColumnsV2(Integer dataSourceId, String tableName, DataAssetEnums.DataApiOperationType operationType) {
        DatasourceTableColumnVO datasourceTableColumnDos = dataAssetService.listDataAssetTableColumnsV2(dataSourceId, tableName, operationType);
        ResultDTO<DatasourceTableColumnVO> resultDTO = new ResultDTO<>();
        resultDTO.setData(datasourceTableColumnDos);
        return resultDTO;
    }


    @PostMapping("/page/like")
    @ApiOperation("查询数据源列表")
    public PageResultDTO<Page<DataSourcePo>> like(
            PageQuery pageQuery,
            String keyword) {
        Page<DataSourcePo> list = dataSourceService.like(DataSourcePo.class,
                Arrays.asList(new String[]{"connectionName", "dbName"}), keyword, pageQuery.getPageNo(), pageQuery.getPageSize());
        for (DataSourcePo dataSourcePo : list) {
            dataSourcePo.setDbPassword(null);
        }
        PageResultDTO<Page<DataSourcePo>> resultDTO = new PageResultDTO<>();
        resultDTO.setData(list);
        resultDTO.setTotalCount((int) list.getTotal());
        return resultDTO;
    }
}
