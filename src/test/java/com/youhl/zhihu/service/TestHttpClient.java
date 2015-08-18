package com.youhl.zhihu.service;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TestHttpClient {
  public static final String EMAIL = "906669319@qq.com";
  public static final String PASSWORD = "qazwsx";

  @Test
  public void test1() throws Exception {
    RequestConfig config =
        RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setSocketTimeout(5000)
            .setConnectTimeout(5000).build();
    CloseableHttpClient httpClient =
        HttpClients
            .custom()
            .setDefaultRequestConfig(config)
            .setUserAgent(
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36")
            .build();

    HttpGet httpGet = new HttpGet("http://www.zhihu.com/");
    HttpResponse response = httpClient.execute(httpGet);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      for (Header header : response.getAllHeaders()) {
        System.out.println("h1:" + header.getName() + "=" + header.getValue());
      }

      String xsrf = "";
      HttpPost httpPost = new HttpPost("http://www.zhihu.com/login/email");
      List<NameValuePair> params = Lists.newArrayList();
      params.add(new BasicNameValuePair("email", EMAIL));
      params.add(new BasicNameValuePair("password", PASSWORD));
      params.add(new BasicNameValuePair("_xsrf", xsrf));
      params.add(new BasicNameValuePair("remember_me", "true"));
      httpPost.setEntity(new UrlEncodedFormEntity(params));
      response = httpClient.execute(httpPost);
      entity = response.getEntity();
      IOUtils.copy(entity.getContent(), System.out);
    }
  }
}
