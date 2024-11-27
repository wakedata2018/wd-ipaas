package com.wakedata.dw.open.controller.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.wakedata.dw.open.datasource.JdbcNutDaoHolder;
import com.wakedata.dw.open.dto.ResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.nutz.dao.impl.NutDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author tanzhi
 * @title DataSourceMetricController
 * @date 2019/12/11 14:15
 * @projectName bdp-open
 * @descriptor
 */

@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/datasource/metric")
@Api("数据连接池状况")
public class DataSourceMetricController {


    @GetMapping("/jdbc")
    @ApiOperation("jdbc连接情况")
    public ResultDTO<List<Map<String, Object>>> status() {
        Map<String, NutDao> nutDaoCache = JdbcNutDaoHolder.getNutDaoCache();
        List<Map<String, Object>> list = new ArrayList<>(nutDaoCache.size());
        Iterator<Map.Entry<String, NutDao>> iterator = nutDaoCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = new HashMap<>();

            Map.Entry<String, NutDao> next = iterator.next();
            NutDao value = next.getValue();
            DruidDataSource dataSource = (DruidDataSource) value.getDataSource();
            int activeCount = dataSource.getActiveCount();
            map.put("activeCount", activeCount);
            long connectCount = dataSource.getConnectCount();
            map.put("connectCount", connectCount);

            int activePeak = dataSource.getActivePeak();
            map.put("activePeak", activePeak);

            Date activePeakTime = dataSource.getActivePeakTime();
            map.put("activePeakTime", activePeakTime);

            long closeCount = dataSource.getCloseCount();
            map.put("closeCount", closeCount);

            long connectErrorCount = dataSource.getConnectErrorCount();
            map.put("connectErrorCount", connectErrorCount);

            long createCount = dataSource.getCreateCount();
            map.put("createCount", createCount);

            long errorCount = dataSource.getErrorCount();
            map.put("errorCount", errorCount);

            long destroyCount = dataSource.getDestroyCount();
            map.put("destroyCount", destroyCount);
            int waitThreadCount = dataSource.getWaitThreadCount();
            map.put("waitThreadCount", waitThreadCount);
            int poolingCount = dataSource.getPoolingCount();
            map.put("poolingCount", poolingCount);
            int maxActive = dataSource.getMaxActive();
            map.put("maxActive", maxActive);
            int maxIdle = dataSource.getMaxIdle();
            map.put("maxIdle", maxIdle);
            long maxWait = dataSource.getMaxWait();
            map.put("maxWait", maxWait);

            list.add(map);
        }
        ResultDTO<List<Map<String, Object>>> resultDTO = new ResultDTO();
        resultDTO.setData(list);
        return resultDTO;
    }
}
