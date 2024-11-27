package com.wakedata.dw.open.request;

import com.jayway.jsonpath.JsonPath;
//import com.wakedata.dw.open.config.ServerConfig;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.utils.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * XxlJobInfo xxljob任务数据表的操作
 *
 * @author 佟蕊
 */
@Slf4j
@Component
public class XxlJobInfoCRUDRequest {

//    @Autowired
//    private ServerConfig serverConfig;

    private static String XXLJOB_ADD = "xxljob/add";
    private static String XXLJOB_UPDATE = "xxljob/update";
    private static String XXLJOB_STOP = "xxljob/update";
    private static String XXLJOB_DELETE = "xxljob/delete";
    private static String XXLJOB_GROUP = "xxljob/getGroup";
    private static String XXLJOB_FINDJOB = "xxljob/findJobInfo";
    private static Integer GROUP_ID = null;

    private final RestTemplate restTemplate;

    public XxlJobInfoCRUDRequest(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getMethodFullPathUrl(String method){
//        return serverConfig.getMethodFullPathUrl().replace("APP_METHOD",method);
        return null;
    }

    public Integer getGroup(){
        if (null != GROUP_ID){
            return GROUP_ID;
        }

        String url = getMethodFullPathUrl(XXLJOB_GROUP);
//        String result = RestTemplateUtil.requestGet(url+"&app_name="+serverConfig.getXxjobExecutorAppName(), String.class, restTemplate);
        String result = null;
        Boolean isSuccess = JsonPath.read(result, "$.success");
        if (isSuccess){
            GROUP_ID = JsonPath.read(result, "$.data[0].id");
        }
        return GROUP_ID;
    }

    public  ResultDTO add(String jobDesc,String schedule_conf,Integer apiId){
        String url = getMethodFullPathUrl(XXLJOB_ADD);

        //提交参数设置
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("job_group", getGroup());
        map.add("job_desc", jobDesc);
        map.add("author", "XXL");
        map.add("alarm_email", "XXL");
        map.add("schedule_type", "CRON");
        map.add("job_cron", schedule_conf);
        map.add("misfire_strategy", "DO_NOTHING");
        map.add("executor_route_strategy", "FIRST");
        map.add("executor_handler", "crontabHandler");
        map.add("executor_param", apiId);
        map.add("executor_block_strategy", "SERIAL_EXECUTION");
        map.add("executor_timeout", "5");
        map.add("executor_fail_retry_count", "3");
        map.add("glue_type", "BEAN");
        map.add("glue_source", "");
        map.add("trigger_status", "1");
        map.add("glue_remark", "GLUE代码初始化");

        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        map.add("add_time", sdf.format(new Date()));
        map.add("update_time", sdf.format(new Date()));
        map.add("glue_updatetime", sdf.format(new Date()));

        ResultDTO resultDTO = RestTemplateUtil.requestBeanPost(url, map, ResultDTO.class, restTemplate);
        return resultDTO;
    }

    public ResultDTO update(String jobDesc,String schedule_conf,Integer apiId){
        String url = getMethodFullPathUrl(XXLJOB_UPDATE);
        //提交参数设置
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("job_desc", jobDesc);
        map.add("job_cron", schedule_conf);
        map.add("job_group", getGroup());
        map.add("executor_param", apiId);
        map.add("trigger_status", "1");

        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        map.add("update_time", sdf.format(new Date()));

        ResultDTO resultDTO = RestTemplateUtil.requestBeanPost(url, map, ResultDTO.class, restTemplate);
        return resultDTO;
    }

    public ResultDTO stop(Integer apiId){
        String url = getMethodFullPathUrl(XXLJOB_STOP);
        //提交参数设置
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("job_group", getGroup());
        map.add("executor_param", apiId);
        map.add("trigger_status", "0");

        ResultDTO resultDTO = RestTemplateUtil.requestBeanPost(url, map, ResultDTO.class, restTemplate);
        return resultDTO;
    }

    public ResultDTO delete(Integer apiId){
        String url = getMethodFullPathUrl(XXLJOB_DELETE);
        //提交参数设置
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("job_group", getGroup());
        map.add("executor_param", apiId);
        map.add("trigger_status", "0");
        ResultDTO resultDTO = RestTemplateUtil.requestBeanPost(url, map, ResultDTO.class, restTemplate);
        return resultDTO;
    }

    public Boolean hasJob(Integer apiId){
        String url = getMethodFullPathUrl(XXLJOB_FINDJOB);
        PageResultDTO resultDTO = RestTemplateUtil.requestGet(url+"&job_group="+getGroup()+"&executor_param="+apiId, PageResultDTO.class, restTemplate);
        Integer totalCount = resultDTO.getTotalCount();
        return totalCount==0 ? false:true;
    }

    public Integer jobCount(Integer apiId){
        String url = getMethodFullPathUrl(XXLJOB_FINDJOB);
        PageResultDTO resultDTO = RestTemplateUtil.requestGet(url+"&job_group="+getGroup()+"&executor_param="+apiId, PageResultDTO.class, restTemplate);
        Integer totalCount = resultDTO.getTotalCount();
        return totalCount;
    }

}
