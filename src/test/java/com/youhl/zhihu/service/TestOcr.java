package com.youhl.zhihu.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.junit.Test;

import com.youhl.zhihu.utils.ocr.ImageTrans;
import com.youhl.zhihu.utils.ocr.OCR;


public class TestOcr {
  @Test
  public void test1() throws Exception {
    try {
      File[] files = new File("/d:/tmp/captcha").listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
          return pathname.getName().endsWith("gif") && !pathname.getName().contains("-");
        }
      });
      for (File file : files) {
        ImageTrans imageTrans = new ImageTrans(file);
        File tiff = imageTrans.reload().blur().createTiffFile().getTiff();
        String valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);


        tiff = imageTrans.reload().dlur().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

        tiff = imageTrans.reload().sharper().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

        tiff = imageTrans.reload().sharpen().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

        tiff = imageTrans.reload().rotate().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

        tiff = imageTrans.reload().median().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

        tiff = imageTrans.reload().median0().binary().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

        tiff = imageTrans.reload().avg().createTiffFile().getTiff();
        valCode = new OCR().recognizeText(tiff, "eng", "");
        System.out.println(valCode);

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
