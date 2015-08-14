package com.youhl.zhihu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

public class Utils {

  public static Document parseDocument(CloseableHttpClient httpClient, String url) {
    HttpGet httpGet = null;
    CloseableHttpResponse response = null;
    InputStream is = null;
    try {
      httpGet = new HttpGet(url);
      response = httpClient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        is = entity.getContent();
        StringWriter sw = new StringWriter();
        IOUtils.copy(is, sw);
        Document doc = Jsoup.parse(sw.toString(), url);
        return doc;
      }
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        if (response != null) {
          response.close();
        }
        if (httpGet != null) {
          httpGet.abort();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static Long parseLong(String data) {
    if (data == null) {
      return null;
    }
    String trim = data.replaceAll("\\s+", "");
    String value = null;
    if ((value = extract(trim, Pattern.compile("^\\d+$"))) != null) {
      return Long.parseLong(value);
    } else if ((value = extract(trim, Pattern.compile("\\d+(?=[k|K])"))) != null) {
      return Long.parseLong(value) * 1000;
    }
    return null;
  }

  public static Integer parseInt(String data) {
    if (data == null) {
      return null;
    }
    String trim = data.replaceAll("\\s+", "");
    String value = null;
    if ((value = extract(trim, Pattern.compile("^\\d+$"))) != null) {
      return Integer.parseInt(value);
    } else if ((value = extract(trim, Pattern.compile("\\d+(?=[k|K])"))) != null) {
      return Integer.parseInt(value) * 1000;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static <T> T coalesce(T... objs) {
    if (objs == null) {
      return null;
    }
    T res = null;
    for (T obj : objs) {
      res = obj;
      if (res != null) {
        break;
      }
    }
    return res;
  }

  public static String content(Element root, String query) {
    return content(root, query, null);
  }


  public static String content(Element root, String query, String name) {
    return content(root, query, name == null ? null : new String[] {name}, null);
  }

  public static String content(Element root, String query, String[] names, String regex) {
    return content(root, query, 0, names, regex);
  }

  public static String content(Element root, String query, int index, String[] names, String regex) {
    Elements eles = root.select(query);
    if (eles == null || eles.size() < index + 1) {
      return null;
    }
    Element ele = eles.get(index);
    String value = null;
    if (names == null || names.length == 0) {
      value = ele.text();
    } else {
      for (String name : names) {
        value = ele.attr(name);
        if (value != null && value.trim().length() > 0) {
          break;
        }
      }
    }
    if (value != null && regex != null) {
      value = extract(value, Pattern.compile(regex));
    }
    return value;
  }

  public static List<String> contents(Element root, String query, String[] names, String regex) {
    List<String> list = Lists.newArrayList();
    Elements eles = root.select(query);
    if (eles == null || eles.size() < 1) {
      return list;
    }
    for (Element ele : eles) {
      String value = null;
      if (names == null || names.length == 0) {
        value = ele.text();
      } else {
        for (String name : names) {
          value = ele.attr(name);
          if (value != null && value.trim().length() > 0) {
            break;
          }
        }
      }
      if (value != null && regex != null) {
        value = extract(value, Pattern.compile(regex));
      }
      if (value != null) {
        list.add(value);
      }
    }
    return list;
  }

  public static String childText(Element root, String query) {
    return childText(root, query, 0, 0, null);
  }

  public static String childText(Element root, String query, int idx1, int idx2, String regex) {
    Elements eles = root.select(query);
    if (eles == null || eles.size() < idx1 + 1) {
      return null;
    }
    Node node = eles.get(idx1).childNode(idx2);
    String value = null;
    if (node != null && node instanceof TextNode) {
      value = ((TextNode) node).getWholeText().trim();
    }
    if (value != null && regex != null) {
      value = extract(value, Pattern.compile(regex));
    }
    return value == null ? value : value.trim();
  }

  public static String nextText(Element root, String query) {
    return nextText(root, query, 0, null);
  }

  public static String nextText(Element root, String query, int index, String regex) {
    Elements eles = root.select(query);
    if (eles == null || eles.size() < index + 1) {
      return null;
    }
    Element ele = eles.get(index);
    Node node = ele.nextSibling();
    String value = null;
    if (node != null && node instanceof TextNode) {
      value = ((TextNode) node).getWholeText().trim();
    }
    if (value != null && regex != null) {
      value = extract(value, Pattern.compile(regex));
    }
    return value == null ? value : value.trim();
  }

  public static String extract(String data, Pattern pattern) {
    if (data == null) {
      return null;
    }
    Matcher matcher = pattern.matcher(data);
    while (matcher.find()) {
      return matcher.group();
    }
    return null;
  }

  // 解析带 \u9a8c 的字符串
  public static String unicode2String(String content) {
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(content, "\\u|\\U");
    while (st.hasMoreTokens()) {
      String data = st.nextToken();
      if (data.length() >= 4) {
        String unicode = data.substring(0, 4);
        if (unicode.toLowerCase().matches("[0-9a-f]{4}")) {
          sb.append((char) Integer.parseInt(unicode, 16));
          data = data.substring(4);
        }
      }
      sb.append(data);
    }
    return sb.toString();
  }

  public static boolean isInterface(Class<?> c, String interfaceName) {
    Class<?>[] face = c.getInterfaces();
    for (int i = 0, j = face.length; i < j; i++) {
      if (face[i].getName().equals(interfaceName)) {
        return true;
      } else {
        Class<?>[] faces = face[i].getInterfaces();
        for (int x = 0; x < faces.length; x++) {
          if (faces[x].getName().equals(interfaceName)) {
            return true;
          } else if (isInterface(faces[x], interfaceName)) {
            return true;
          }
        }
      }
    }
    if (null != c.getSuperclass()) {
      return isInterface(c.getSuperclass(), interfaceName);
    }
    return false;
  }

}
