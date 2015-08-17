package com.youhl.zhihu.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class TestHtmlUnit {
  @Test
  public void test1() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
    final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    webClient.getOptions().setCssEnabled(false);
    webClient.getOptions().setJavaScriptEnabled(true);
    // webClient.setJavaScriptEnabled(true);
    // webClient.setCssEnabled(false);
    // webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    // webClient.setTimeout(Integer.MAX_VALUE);
    // webClient.setThrowExceptionOnScriptError(false);
    HtmlPage page = webClient.getPage("http://www.zhihu.com/");
    HtmlAnchor anchor = page.getAnchorByHref("#signin");
    page = anchor.click();
    HtmlForm form = page.getForms().get(0);
    HtmlTextInput account = form.getInputByName("account");
    HtmlPasswordInput password = form.getInputByName("password");
    HtmlButton submit = (HtmlButton) form.getByXPath("div/button").get(0);

    account.setValueAttribute("906669319@qq.com");
    password.setValueAttribute("qazwsx");
    page = submit.click();
    System.out.println("登陆:" + page.asXml());
    webClient.closeAllWindows();
  }
}
