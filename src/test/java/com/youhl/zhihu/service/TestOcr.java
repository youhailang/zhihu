package com.youhl.zhihu.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class TestOcr {
  @Test
  public void test1() {
    File[] files = new File("/d:/tmp/captcha").listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return pathname.getName().endsWith("gif") && !pathname.getName().contains("-");
      }
    });
    for (File file : files) {
      try {
        String suffix = StringUtils.substringAfterLast(file.getName(), ".");
        String valCode = new OCR().recognizeText(file, suffix, "eng", "");
        System.out.println(valCode);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void test2() {
    try {
      new ImageTrans(new File("/d:/tmp/captcha/captcha_1439964126814.gif")).binary()
          .createTiffFile().getTiff();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
