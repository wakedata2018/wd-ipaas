package com.wakedata.dw.open.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Linchong
 * @version Id: HttpUtil.java, v 0.1 2018/7/17 16:35 Linchong Exp $$
 * @Description Http 请求
 */
@Slf4j
public class HttpUtil {


    /**
     * get 方式
     *
     * @param url
     * @param param
     * @return
     */
    public static String get(String url, Map<String, String> param,String apiKeY) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("apikey", apiKeY);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            return "error";
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return resultString;
    }





    /**
     * get 方式
     * 
     * @param url
     * @param param
     * @return
     */
    public static String get(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            return "error";
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return resultString;
    }


    /**
     * get 方式
     * 
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, null);
    }

    public static String postWithParamsForString(String url, Map<String, String> paramsMap){
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost =   new HttpPost(url);
        String s = "";
        List<NameValuePair> formParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(formParams,"UTF-8"));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            HttpResponse response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
                HttpEntity entity = response.getEntity();
                s = EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return s;
    }

    /**
     * http post的请求
     * 
     * @param url
     * @param param
     * @return
     */
    public static String post(String url, Map<String, Object> param) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // 设置请求的header
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");

            // 设置请求的参数
            JSONObject jsonParam = new JSONObject();
            for (String key : param.keySet()) {
                jsonParam.put(key, param.get(key));
            }
            StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("Http 请求异常,url={},msg={}", url, e.getMessage());
        }
        return null;
    }

    /**
     * http post url传参的请求
     *
     * @param url
     * @return
     */
    public static String postUrl(String url) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // 设置请求的header
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("Http 请求异常,url={},msg={}", url, e.getMessage());
        }
        return null;
    }

    /**
     * 调用http post
     * 
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String post(String url, String body) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // 设置请求的header
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            httpPost.setEntity(new StringEntity(body));

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            //System.out.println(code);
            String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
            //System.out.println(responseContent);

            return responseContent;
        } catch (Exception e) {
            log.error("Http 请求异常,url={},msg={}", url, e.getMessage());
        }
        return null;
    }

    /**
     * http post的请求
     *
     * @param url
     * @param param
     * @return
     */
    public static String post2(String url, Map<String, Object> param) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // 设置请求的header
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            // 设置请求的参数
            JSONObject jsonParam = new JSONObject();
            for (String key : param.keySet()) {
                jsonParam.put(key, param.get(key));
            }
            StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("Http 请求异常,url={},msg={}", url, e.getMessage());
        }
        return null;
    }

}
