package com.wakedata.dw.open.service.impl.nacos;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.service.nacos.NacosOpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * nacos OpenApi业务接口实现类
 *
 * @author wujunqiang
 * @date 2021/12/13 21:21
 */
@Slf4j
@Service
public class NacosOpenApiServiceImpl implements NacosOpenApiService {

    @Value("${dw.nacos.api.service-list:}")
    private String serviceListUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取服务列表
     *
     * @param pageNo   当前页
     * @param pageSize 每页数量
     * @return 服务列表
     */
    @Override
    public ResultDTO<String> getServiceList(Integer pageNo, Integer pageSize) {
        String url = String.format(serviceListUrl, pageNo, pageSize);
        try {
            return ResultDTO.success(restTemplate.getForObject(url, String.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResultDTO<String> dto = new ResultDTO<>();
            dto.setFailed(MsgCodeEnum.w_nacos_service_list_error);
            return dto;
        }
    }

}
