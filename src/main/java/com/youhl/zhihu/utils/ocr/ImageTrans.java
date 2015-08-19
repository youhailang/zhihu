package com.youhl.zhihu.utils.ocr;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    this(srcFile, null);
  }

  public ImageTrans(File srcFile, String format) throws IOException {
    super();
    if (format == null) {
      this.format = StringUtils.defaultIfEmpty(getFormatByFile(srcFile), "jpg");
    } else {
      this.format = format;
    }
    dstFiles.add(srcFile);
  }

  public static String getFormatByFile(File srcFile) {
    String suffix = StringUtils.substringAfterLast(srcFile.getName(), ".");
    String format = null;
    if (suffix != null && !suffix.isEmpty()) {
      format = suffix;
    }
    return format;
  }

  public ImageTrans reload() {
    return reload(getSrcFile(), getFormat());
  }

  public ImageTrans reload(File srcFile) {
    return reload(srcFile, StringUtils.defaultIfEmpty(getFormatByFile(srcFile), "jpg"));
  }

  public ImageTrans reload(File srcFile, String format) {
    dstFiles.clear();
    tiff = null;
    this.format = format;
    dstFiles.add(srcFile);
    return this;
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

  public ImageTrans grey() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    ColorConvertOp ccp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    ImageIO.write(ccp.filter(srcImage, null), format, createDstFile());
    return this;
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
    float[] data = {0.0625f, 0.125f, 0.0625f, 0.125f, 0.125f, 0.125f, 0.0625f, 0.125f, 0.0625f};
    ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
    ImageIO.write(cop.filter(srcImage, null), format, createDstFile());
    return this;
  }

  // Blur by "convolving" the image with a matrix
  public ImageTrans blur() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    float[] data = {.1111f, .1111f, .1111f, .1111f, .1111f, .1111f, .1111f, .1111f, .1111f,};
    ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
    ImageIO.write(cop.filter(srcImage, null), format, createDstFile());
    return this;
  }

  // 锐化
  public ImageTrans sharper() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    float[] data = {-1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f, -1.0f, -1.0f};
    ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
    ImageIO.write(cop.filter(srcImage, null), format, createDstFile());
    return this;
  }

  // Sharpen by using a different matrix
  public ImageTrans sharpen() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    float[] data = {0.0f, -0.75f, 0.0f, -0.75f, 4.0f, -0.75f, 0.0f, -0.75f, 0.0f};
    ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
    ImageIO.write(cop.filter(srcImage, null), format, createDstFile());
    return this;
  }


  // 11) Rotate the image 180 degrees about its center point
  public ImageTrans rotate() throws IOException {
    BufferedImage srcImage = ImageIO.read(getLastFile());
    AffineTransformOp atop =
        new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI, srcImage.getWidth() / 2,
            srcImage.getHeight() / 2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    ImageIO.write(atop.filter(srcImage, null), format, createDstFile());
    return this;
  }

  // 均值去噪
  public ImageTrans avg() throws IOException {
    BufferedImage image = ImageIO.read(getLastFile());
    int iw = image.getWidth();
    int ih = image.getHeight();
    int[] srcPixels = new int[iw * ih];
    PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, srcPixels, 0, iw);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int[] dstPixels = Arrays.copyOfRange(srcPixels, 0, srcPixels.length);
    int[] bounds = new int[] {1, 1, 1, 1};
    for (int i = bounds[0]; i < ih - bounds[1]; i++) {
      for (int j = bounds[2]; j < iw - bounds[3]; j++) {
        dstPixels[i * iw + j] = avg(srcPixels, i, j, iw, ih, bounds);
      }
    }
    // 将数组中的象素产生一个图像
    Image tempImg =
        Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, dstPixels, 0, iw));
    image =
        new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null),
            BufferedImage.TYPE_INT_BGR);
    image.createGraphics().drawImage(tempImg, 0, 0, null);
    ImageIO.write(image, format, createDstFile());
    return this;
  }

  private int avg(int[] pixels, int i, int j, int iw, int ih, int[] bounds) {
    ColorModel cm = ColorModel.getRGBdefault();
    int alpha = cm.getAlpha(pixels[i * iw + j]);
    int red = 0, green = 0, blue = 0;
    for (int m = i - bounds[0]; m <= i + bounds[1]; m++) {
      for (int n = j - bounds[2]; n <= j + bounds[3]; n++) {
        int rgb = pixels[m * iw + n];
        red += cm.getRed(rgb);
        green += cm.getGreen(rgb);
        blue += cm.getBlue(rgb);
      }
    }
    red /= 9;
    green /= 9;
    blue /= 9;
    return alpha << 24 | red << 16 | green << 8 | blue;
  }

  // 中心去噪
  public ImageTrans median0() throws IOException {
    BufferedImage image = ImageIO.read(getLastFile());
    int iw = image.getWidth();
    int ih = image.getHeight();
    int[] srcPixels = new int[iw * ih];
    PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, srcPixels, 0, iw);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int[] dstPixels = Arrays.copyOfRange(srcPixels, 0, srcPixels.length);
    int[] bounds = new int[] {0, 0, 1, 1};
    for (int i = bounds[0]; i < ih - bounds[1]; i++) {
      for (int j = bounds[2]; j < iw - bounds[3]; j++) {
        dstPixels[i * iw + j] = median0(srcPixels, i, j, iw, ih, bounds);
      }
    }
    // 将数组中的象素产生一个图像
    Image tempImg =
        Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, dstPixels, 0, iw));
    image =
        new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null),
            BufferedImage.TYPE_INT_BGR);
    image.createGraphics().drawImage(tempImg, 0, 0, null);
    ImageIO.write(image, format, createDstFile());
    return this;
  }

  private int median0(int[] pixels, int i, int j, int iw, int ih, int[] bounds) {
    ColorModel cm = ColorModel.getRGBdefault();
    int alpha = cm.getAlpha(pixels[i * iw + j]);
    int length = (1 + bounds[0] + bounds[1]) * (1 + bounds[2] + bounds[3]);
    int[] reds = new int[length];
    int[] greens = new int[length];
    int[] blues = new int[length];
    int red = 0, green = 0, blue = 0;
    int index = 0;
    for (int m = i - bounds[0]; m <= i + bounds[1]; m++) {
      for (int n = j - bounds[2]; n <= j + bounds[3]; n++) {
        int rgb = pixels[m * iw + n];
        reds[index] = cm.getRed(rgb);
        greens[index] = cm.getGreen(rgb);
        blues[index] = cm.getBlue(rgb);
        index++;
      }
    }
    Arrays.sort(reds);
    Arrays.sort(greens);
    Arrays.sort(blues);
    red = reds[(reds.length+1) / 2];
    green = greens[(greens.length+1)  / 2];
    blue = blues[(blues.length+1)  / 2];
    return alpha << 24 | red << 16 | green << 8 | blue;
  }

  // 中值去噪
  public ImageTrans median() throws IOException {
    BufferedImage image = ImageIO.read(getLastFile());
    int iw = image.getWidth();
    int ih = image.getHeight();
    int[] pixels = new int[iw * ih];
    PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels, 0, iw);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // 对图像进行中值滤波，Alpha值保持不变
    ColorModel cm = ColorModel.getRGBdefault();
    for (int i = 1; i < ih - 1; i++) {
      for (int j = 1; j < iw - 1; j++) {
        int red, green, blue;
        int alpha = cm.getAlpha(pixels[i * iw + j]);

        // int red2 = cm.getRed(pixels[(i - 1) * iw + j]);
        int red4 = cm.getRed(pixels[i * iw + j - 1]);
        int red5 = cm.getRed(pixels[i * iw + j]);
        int red6 = cm.getRed(pixels[i * iw + j + 1]);
        // int red8 = cm.getRed(pixels[(i + 1) * iw + j]);

        // 水平方向进行中值滤波
        if (red4 >= red5) {
          if (red5 >= red6) {
            red = red5;
          } else {
            if (red4 >= red6) {
              red = red6;
            } else {
              red = red4;
            }
          }
        } else {
          if (red4 > red6) {
            red = red4;
          } else {
            if (red5 > red6) {
              red = red6;
            } else {
              red = red5;
            }
          }
        }

        int green4 = cm.getGreen(pixels[i * iw + j - 1]);
        int green5 = cm.getGreen(pixels[i * iw + j]);
        int green6 = cm.getGreen(pixels[i * iw + j + 1]);

        // 水平方向进行中值滤波
        if (green4 >= green5) {
          if (green5 >= green6) {
            green = green5;
          } else {
            if (green4 >= green6) {
              green = green6;
            } else {
              green = green4;
            }
          }
        } else {
          if (green4 > green6) {
            green = green4;
          } else {
            if (green5 > green6) {
              green = green6;
            } else {
              green = green5;
            }
          }
        }

        // int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);
        int blue4 = cm.getBlue(pixels[i * iw + j - 1]);
        int blue5 = cm.getBlue(pixels[i * iw + j]);
        int blue6 = cm.getBlue(pixels[i * iw + j + 1]);
        // int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);

        // 水平方向进行中值滤波
        if (blue4 >= blue5) {
          if (blue5 >= blue6) {
            blue = blue5;
          } else {
            if (blue4 >= blue6) {
              blue = blue6;
            } else {
              blue = blue4;
            }
          }
        } else {
          if (blue4 > blue6) {
            blue = blue4;
          } else {
            if (blue5 > blue6) {
              blue = blue6;
            } else {
              blue = blue5;
            }
          }
        }
        pixels[i * iw + j] = alpha << 24 | red << 16 | green << 8 | blue;
      }
    }

    // 将数组中的象素产生一个图像
    Image tempImg =
        Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, pixels, 0, iw));
    image =
        new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null),
            BufferedImage.TYPE_INT_BGR);
    image.createGraphics().drawImage(tempImg, 0, 0, null);
    ImageIO.write(image, format, createDstFile());
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

  public String getFormat() {
    return format;
  }

  public File getTiff() {
    return tiff;
  }

  public static boolean isBlack(int rgb) {
    Color color = new Color(rgb);
    if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
      return true;
    }
    return false;
  }
}
