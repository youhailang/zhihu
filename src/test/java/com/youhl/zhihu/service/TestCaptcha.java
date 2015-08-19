package com.youhl.zhihu.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class TestCaptcha {
  String url = "http://www.zhihu.com/captcha.gif";

  public String getUrl() {
    return url + "?r=" + System.currentTimeMillis();
  }

  @Test
  public void test1() throws ClientProtocolException, IOException {
    for(int i = 0 ; i < 10;i ++){
      long r = System.currentTimeMillis();
      File file = new File("/d:/tmp/captcha", "captcha_" + r + ".gif");
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(url + "?r=" + r);
      HttpResponse response = httpClient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        IOUtils.copy(entity.getContent(), new FileOutputStream(file));
        File tiff = ImageIOHelper.createImage(file, "gif");
      }
    }
  }
}
