package com.youhl.zhihu.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

public class HttpUtils {
  public static HttpClient createDefault() {
    RequestConfig config =
        RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setSocketTimeout(5000)
            .setConnectTimeout(5000).build();
    return HttpClients
        .custom()
        .setDefaultRequestConfig(config)
        .setUserAgent(
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2427.7 Safari/537.36")
        .build();
  }
}
