package com.youhl.zhihu.service;

import java.io.IOException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestHtmlUnit {
  @Test
  public void test1() {
    WebClient webClient = new WebClient();
    webClient.setJavaScriptEnabled(true);
    webClient.setCssEnabled(false);
    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    webClient.setTimeout(Integer.MAX_VALUE);
    webClient.setThrowExceptionOnScriptError(false);
    HtmlPage rootPage;
    try {
      rootPage = webClient.getPage("http://tt.mop.com/read_14304066_1_0.html");
      System.out.println(rootPage.asXml());
    } catch (FailingHttpStatusCodeException | IOException e) {
      e.printStackTrace();
    }

  }
}
