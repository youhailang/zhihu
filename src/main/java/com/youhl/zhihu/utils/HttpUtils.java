package com.youhl.zhihu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.collect.Lists;
import com.youhl.zhihu.entity.LoginUser;

public class HttpUtils {
  public static final String HOME_URL = "http://www.zhihu.com/";
  public static final String LOGIN_URL = "http://www.zhihu.com/login/email";

  public static void login(CloseableHttpClient httpClient, LoginUser user) {
    String xsrf = getXsrf(httpClient);
    System.out.println(xsrf);
    if (xsrf != null) {
      HttpPost post = new HttpPost(LOGIN_URL);
      List<BasicNameValuePair> pairs = Lists.newArrayList();
      pairs.add(new BasicNameValuePair("_xsrf", xsrf));
      pairs.add(new BasicNameValuePair("email", user.getUsername()));
      pairs.add(new BasicNameValuePair("password", user.getPassword()));
      pairs.add(new BasicNameValuePair("rememberme", "false"));
      CloseableHttpResponse response = null;
      InputStream is = null;
      try {
        post.setEntity(new UrlEncodedFormEntity(pairs));
        response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          is = entity.getContent();
          StringWriter sw = new StringWriter();
          IOUtils.copy(is, sw);
          Document doc = Jsoup.parse(sw.toString(), LOGIN_URL);
          System.out.println(Utils.unicode2String(sw.toString()));
        }
      } catch (IOException e) {
      } finally {
        post.abort();
      }
    }
  }

  public static String getXsrf(CloseableHttpClient httpClient) {
    Document doc = Utils.parseDocument(httpClient, HOME_URL);
    if (doc != null) {
      return Utils.content(doc, "input[name~=_xsrf]", "value");
    }
    return null;
  }

  public static CloseableHttpClient createDefault() {
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
