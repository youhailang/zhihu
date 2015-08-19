package com.youhl.zhihu.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class OCR {
  private final String LANG_OPTION = "-l"; // 英文字母小写l，并非数字1

  private final String EOL = System.getProperty("line.separator");

  private String getTesseract() {
    File[] files =
        new File("/D:/Program Files (x86)/Tesseract-OCR/").listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return name.startsWith("tesseract.exe");
          }
        });
    File tesseract = files.length > 0 ? files[0] : null;
    System.out.println(tesseract.getPath());
    return tesseract.getPath();
  }

  public String recognizeText(File imageFile, String imageFormat, String lang, String others)
      throws Exception {
    File tmpImage = ImageIOHelper.createImage(imageFile, imageFormat);
    File tmpOutput =
        new File(imageFile.getParentFile(), StringUtils.substringBeforeLast(imageFile.getName(),
            "."));
    StringBuffer strB = new StringBuffer();
    List<String> cmd = new ArrayList<String>();
    cmd.add("\"" + getTesseract() + "\"");
    cmd.add("");
    cmd.add(tmpOutput.getName());
    cmd.add(LANG_OPTION);
    // chi_sim eng
    for (String l : lang.split("\\s+")) {
      cmd.add(l);
    }
    if (!StringUtils.isEmpty(others)) {
      cmd.add(others);
    }
    ProcessBuilder pb = new ProcessBuilder();
    pb.directory(imageFile.getParentFile());

    cmd.set(1, tmpImage.getName());
    pb.command(cmd);
    pb.redirectErrorStream(true);
    System.out.println("cmd:" + StringUtils.join(cmd, " "));
    Process process = pb.start();
    // tesseract.exe 1.jpg 1 -l chi_sim
    int w = process.waitFor();

    // 删除临时正在工作文件
    // tmpImage.delete();

    if (w == 0) {
      BufferedReader in =
          new BufferedReader(new InputStreamReader(new FileInputStream(tmpOutput.getAbsolutePath()
              + ".txt"), "UTF-8"));
      String str;
      while ((str = in.readLine()) != null) {
        strB.append(str).append(EOL);
      }
      in.close();
    } else {
      String msg;
      switch (w) {
        case 1:
          msg = "Errors accessing files.There may be spaces in your image's filename.";
          break;
        case 29:
          msg = "Cannot recongnize the image or its selected region.";
          break;
        case 31:
          msg = "Unsupported image format.";
          break;
        default:
          msg = "Errors occurred.";
      }
      tmpImage.delete();
      throw new RuntimeException(msg);
    }
    return strB.toString();
  }


}
