package com.youhl.zhihu.service;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class TestOcr {
  @Test
  public void test1() {
    String path = "/d:/tmp/1.png";
    try {
      String valCode = new OCR().recognizeText(new File(path), "png");
      System.out.println(new String(valCode.getBytes()));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
