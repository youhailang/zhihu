package com.youhl.zhihu.service;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class TestSeleniumhq {
  @SuppressWarnings("static-access")
  @Test
  public void test() {
    WebDriver driver = new InternetExplorerDriver();
    // 让浏览器访问 Baidu
    driver.get("http://www.zhihu.com/#signin");
    // 获取 网页的 title
    System.out.println("1 Page title is: " + driver.getTitle());
    // 通过 id 找到 input 的 DOM
    List<WebElement> forms = driver.findElements(By.tagName("form"));
    for (WebElement element : forms) {
      System.out.println(element.findElement(By.tagName("input").name("account")).getText());
    }   

    driver.quit();
  }
}
