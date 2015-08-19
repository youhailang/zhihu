package com.youhl.zhihu.service;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

public class ImageTrans {
  private String format;
  private List<File> dstFiles = Lists.newArrayList();

  private File tiff;

  public ImageTrans(File srcFile) throws IOException {
    String suffix = StringUtils.substringAfterLast(srcFile.getName(), ".");
    String format = "jpg";
    if (suffix != null && !suffix.isEmpty()) {
      format = suffix;
    }
    this.format = format;
    dstFiles.add(srcFile);
  }

  public ImageTrans(File srcFile, String format) throws IOException {
    super();
    this.format = format;
    dstFiles.add(srcFile);
  }

  public void cleanFiles() {
    for (int i = 1; i < dstFiles.size(); i++) {
      dstFiles.get(i).delete();
    }
  }

  public File getLastFile() {
    return dstFiles.get(dstFiles.size() - 1);
  }

  public File getSrcFile() {
    return dstFiles.get(0);
  }

  // 灰度化
  public ImageTrans gray() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = srcImage.getRGB(i, j);
        dstImage.setRGB(i, j, rgb);
      }
    }
    ImageIO.write(dstImage, format, createDstFile());
    return this;
  }

  // 二值化
  public ImageTrans binary() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = srcImage.getRGB(i, j);
        dstImage.setRGB(i, j, rgb);
      }
    }
    ImageIO.write(dstImage, format, createDstFile());
    return this;
  }

  // 钝化
  public ImageTrans dlur() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    float[] data = {0.0625f, 0.125f, 0.0625f, 0.125f, 0.125f, 0.125f, 0.0625f, 0.125f, 0.0625f};
    Kernel kernel = new Kernel(3, 3, data);
    ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    co.filter(srcImage, dstImage);
    ImageIO.write(dstImage, format, createDstFile());
    return this;
  }

  // 锐化
  public ImageTrans sharper() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    float[] data = {-1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f, -1.0f, -1.0f};
    Kernel kernel = new Kernel(3, 3, data);
    ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    co.filter(srcImage, dstImage);
    ImageIO.write(dstImage, format, createDstFile());
    return this;
  }

  // 自定义
  public ImageTrans clean() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    int h = srcImage.getHeight();
    int w = srcImage.getWidth();
    // 灰度化
    int[][] gray = new int[w][h];
    // 亮化
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int argb = srcImage.getRGB(x, y);
        int r = Math.min((int) (((argb >> 16) & 0xFF) * 1.1 + 30), 255);
        int g = Math.min((int) (((argb >> 8) & 0xFF) * 1.1 + 30), 255);
        int b = Math.min((int) (((argb >> 0) & 0xFF) * 1.1 + 30), 255);
        gray[x][y] =
            (int) Math
                .pow(
                    (Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2) * 0.6274 + Math.pow(b, 2.2) * 0.0753),
                    1 / 2.2);
      }
    }
    // 二值化
    int threshold = ostu(gray, w, h);
    BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        if (gray[x][y] > threshold) {
          gray[x][y] |= 0x00FFFF;
        } else {
          gray[x][y] &= 0xFF0000;
        }
        dstImage.setRGB(x, y, gray[x][y]);
      }
    }
    ImageIO.write(dstImage, format, createDstFile());
    return this;
  }

  public static int ostu(int[][] gray, int w, int h) {
    int[] histData = new int[w * h];
    // Calculate histogram
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int red = 0xFF & gray[x][y];
        histData[red]++;
      }
    }
    // Total number of pixels
    int total = w * h;

    float sum = 0;
    for (int t = 0; t < 256; t++)
      sum += t * histData[t];

    float sumB = 0;
    int wB = 0;
    int wF = 0;

    float varMax = 0;
    int threshold = 0;

    for (int t = 0; t < 256; t++) {
      wB += histData[t]; // Weight Background
      if (wB == 0)
        continue;

      wF = total - wB; // Weight Foreground
      if (wF == 0)
        break;

      sumB += (float) (t * histData[t]);

      float mB = sumB / wB; // Mean Background
      float mF = (sum - sumB) / wF; // Mean Foreground

      // Calculate Between Class Variance
      float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

      // Check if new maximum found
      if (varBetween > varMax) {
        varMax = varBetween;
        threshold = t;
      }
    }

    return threshold;
  }

  private File createDstFile() {
    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    File file = getLastFile();
    int index = file.getName().lastIndexOf(".");
    String suffix = format;
    String prefix = file.getName();
    if (index > 0) {
      suffix = file.getName().substring(index + 1);
      prefix = file.getName().substring(0, index);
    }
    File newFile = new File(file.getParent(), prefix + "-" + methodName + "." + suffix);
    dstFiles.add(newFile);
    return newFile;
  }

  public ImageTrans createTiffFile() {
    File file = getLastFile();
    String path = file.getPath();
    int index = path.lastIndexOf('.');
    StringBuffer sb = new StringBuffer();
    if (index > 0) {
      sb.append(path.substring(0, index)).append(".tif");
    } else {
      sb.append(path).append(".tif");
    }
    tiff = new File(sb.toString());
    try {
      Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(format);
      ImageReader reader = readers.next();
      ImageInputStream iis = ImageIO.createImageInputStream(file);
      reader.setInput(iis);
      // Read the stream metadata
      IIOMetadata streamMetadata = reader.getStreamMetadata();
      // Set up the writeParam
      TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.CHINESE);
      tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);
      // Get tif writer and set output to file
      Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("tiff");
      ImageWriter writer = writers.next();
      BufferedImage bi = reader.read(0);
      IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(0));
      ImageOutputStream ios = ImageIO.createImageOutputStream(tiff);
      writer.setOutput(ios);
      writer.write(streamMetadata, image, tiffWriteParam);
      ios.close();
      writer.dispose();
      reader.dispose();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this;

  }

  public File getTiff() {
    return tiff;
  }
}
