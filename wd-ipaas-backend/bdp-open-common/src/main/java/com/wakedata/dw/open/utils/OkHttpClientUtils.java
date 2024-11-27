package com.wakedata.dw.open.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

/**
 * @author yiyufeng
 * @title OkHttpClientUtils
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
public class OkHttpClientUtils {

    private static OkHttpClient httpClient;
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    static {
        httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();
    }

    public static Response get(String url, Map<String, Object> params) throws IOException {
        log.info("请求 {} params {}", url, params);
        Request request = new Request.Builder()
                .url(OkHttpClientUtils.buildGetRequestUrlParam(url, params))
                .get()
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response formPost(String url, Map<String, String> params) throws IOException {
        log.info("请求 {} params {}", url, params);
        FormBody.Builder formRequestParams = new FormBody.Builder();
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formRequestParams.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(formRequestParams.build())
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response jsonPost(String url, Map<String, Object> urlParams, String json) throws IOException {
        log.info("请求 {} url参数 {} json {}", url, urlParams, json);
        RequestBody jsonRequestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(OkHttpClientUtils.buildGetRequestUrlParam(url, urlParams))
                .post(jsonRequestBody)
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response jsonPost(String url, Map<String, Object> urlParams, String json, String auth) throws IOException {
        log.info("请求 {} url参数 {} json {}", url, urlParams, json);
        RequestBody jsonRequestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(OkHttpClientUtils.buildGetRequestUrlParam(url, urlParams)).header("Authorization", auth)
                .post(jsonRequestBody)
                .build();
        return httpClient.newCall(request).execute();
    }


    public static Response formPut(String url, Map<String, String> params) throws IOException {
        log.info("请求 {} params {}", url, params);
        FormBody.Builder formRequestParams = new FormBody.Builder();
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formRequestParams.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .put(formRequestParams.build())
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response jsonPut(String url, String json) throws IOException {
        log.info("请求 {} json {}", url, json);
        RequestBody jsonRequestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(jsonRequestBody)
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response pathDelete(String url) throws IOException {
        log.info("请求 {}", url);
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response paramDelete(String url, Map<String, String> params) throws IOException {
        log.info("请求 {} params {}", url, params);
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(httpUrlBuilder.build())
                .delete()
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response formDelete(String url, Map<String, String> params) throws IOException {
        log.info("请求 {} params {}", url, params);
        FormBody.Builder formRequestParams = new FormBody.Builder();
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formRequestParams.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .delete(formRequestParams.build())
                .build();
        return httpClient.newCall(request).execute();
    }

    public static Response jsonDelete(String url, String json) throws IOException {
        log.info("请求 {} json {}", url, json);
        RequestBody jsonRequestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .delete(jsonRequestBody)
                .build();
        return httpClient.newCall(request).execute();
    }

    private static HttpUrl buildGetRequestUrlParam(String url, Map<String, Object> params) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (null == entry.getValue()) {
                    continue;
                }
                httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        log.info(httpUrlBuilder.build().toString());
        return httpUrlBuilder.build();
    }

    private static class SSLSocketClient {

        //获取这个SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private static TrustManager[] getTrustManager() {
            return new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
        }

        //获取HostnameVerifier
        public static HostnameVerifier getHostnameVerifier() {
            return (s, sslSession) -> true;
        }

        public static X509TrustManager getX509TrustManager() {
            X509TrustManager trustManager = null;
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trustManager;
        }
    }

}
