package com.youhl.zhihu.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

public class ImageIOHelper {
  public static File createBinaryImage(File srcFile, String format) throws IOException {
    BufferedImage srcImage = ImageIO.read(srcFile);
    BufferedImage dstImage = createBufferedImage(srcImage, BufferedImage.TYPE_BYTE_BINARY);
    File newFile = createDstFile(srcFile, "bin");
    ImageIO.write(dstImage, format, newFile);
    return newFile;
  }

  public static File createGrayImage(File srcFile, String format) throws IOException {
    BufferedImage srcImage = ImageIO.read(srcFile);
    BufferedImage dstImage = createBufferedImage(srcImage, BufferedImage.TYPE_BYTE_GRAY);
    File newFile = createDstFile(srcFile, "gray");
    ImageIO.write(dstImage, format, newFile);
    return newFile;
  }

  public static File createDlurImage(File srcFile, String format) throws IOException {
    BufferedImage srcImage = ImageIO.read(srcFile);
    BufferedImage dstImage = createBufferedImage(srcImage, BufferedImage.TYPE_3BYTE_BGR);

    float[] data = {0.0625f, 0.125f, 0.0625f, 0.125f, 0.125f, 0.125f, 0.0625f, 0.125f, 0.0625f};

    Kernel kernel = new Kernel(3, 3, data);
    ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    co.filter(srcImage, dstImage);
    File newFile = createDstFile(srcFile, "dlur");
    ImageIO.write(dstImage, format, newFile);
    return newFile;
  }

  public static File createSharperImage(File srcFile, String format) throws IOException {
    BufferedImage srcImage = ImageIO.read(srcFile);
    BufferedImage dstImage = createBufferedImage(srcImage, BufferedImage.TYPE_3BYTE_BGR);

    float[] data = {-1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f, -1.0f, -1.0f};

    Kernel kernel = new Kernel(3, 3, data);
    ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

    co.filter(srcImage, dstImage);
    File newFile = createDstFile(srcFile, "sharper");
    ImageIO.write(dstImage, format, newFile);
    return newFile;
  }

  public static File createCleanImage(File srcFile, String format) throws IOException {
    BufferedImage srcImage = ImageIO.read(srcFile);

    int h = srcImage.getHeight();
    int w = srcImage.getWidth();
    // 灰度化
    int[][] gray = new int[w][h];
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int argb = srcImage.getRGB(x, y);
        // 图像加亮（调整亮度识别率非常高）
        int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
        int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
        int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
        if (r >= 255) {
          r = 255;
        }
        if (g >= 255) {
          g = 255;
        }
        if (b >= 255) {
          b = 255;
        }
        gray[x][y] =
            (int) Math
                .pow(
                    (Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2) * 0.6274 + Math.pow(b, 2.2) * 0.0753),
                    1 / 2.2);
      }
    }

    // 二值化
    int threshold = ostu(gray, w, h);
    BufferedImage binaryBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        if (gray[x][y] > threshold) {
          gray[x][y] |= 0x00FFFF;
        } else {
          gray[x][y] &= 0xFF0000;
        }
        binaryBufferedImage.setRGB(x, y, gray[x][y]);
      }
    }

    // 矩阵打印
    // for (int y = 0; y < h; y++) {
    // for (int x = 0; x < w; x++) {
    // if (isBlack(binaryBufferedImage.getRGB(x, y))) {
    // System.out.print("*");
    // } else {
    // System.out.print(" ");
    // }
    // }
    // System.out.println();
    // }
    File newFile = createDstFile(srcFile, "clean");
    ImageIO.write(binaryBufferedImage, format, newFile);
    return newFile;
  }

  public static boolean isBlack(int colorInt) {
    Color color = new Color(colorInt);
    if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
      return true;
    }
    return false;
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

  private static BufferedImage createBufferedImage(BufferedImage image, int imageType) {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage dstImage = new BufferedImage(width, height, imageType);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = image.getRGB(i, j);
        dstImage.setRGB(i, j, rgb);
      }
    }
    return dstImage;
  }

  private static File createDstFile(File srcFile, String key) {
    int index = srcFile.getName().lastIndexOf(".");
    String suffix = srcFile.getName().substring(index + 1);
    String prefix = srcFile.getName().substring(0, index);
    return new File(srcFile.getParent(), prefix + "-" + key + "." + suffix);
  }

  /**
   * 图片文件转换为tif格式
   * 
   * @param imageFile 文件路径
   * @param format 文件扩展名
   * @return
   * @throws IOException
   */
  public static File createImage(File srcFile, String format) throws IOException {
    // File imageFile = createGrayImage(srcFile, format);
    // imageFile = createBinaryImage(imageFile, format);
    File imageFile = createCleanImage(srcFile, format);
    imageFile = createSharperImage(imageFile, format);
    File tempFile = null;
    try {
      Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(format);
      ImageReader reader = readers.next();

      ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
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
      tempFile = tempImageFile(imageFile);
      ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
      writer.setOutput(ios);
      writer.write(streamMetadata, image, tiffWriteParam);
      ios.close();

      writer.dispose();
      reader.dispose();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return tempFile;
  }

  private static File tempImageFile(File imageFile) {
    String path = imageFile.getPath();
    int index = path.lastIndexOf('.');
    StringBuffer sb = new StringBuffer();
    if (index > 0) {
      sb.append(path.substring(0, index)).append(".tif");
    } else {
      sb.append(path).append(".tif");
    }
    File file = new File(sb.toString());
    System.out.println(file.getPath());
    return file;
  }
}
