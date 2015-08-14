package com.youhl.zhihu.service;

import java.util.regex.Pattern;

import org.junit.Test;

import com.youhl.zhihu.utils.Utils;

public class TestNormal {
  @Test
  public void test1() {
    System.out.println(System.currentTimeMillis());
    System.out.println(Utils.extract("http://www.zhihu.com/people/hailang-you", Pattern.compile("(?<=http://www.zhihu.com/people/)([^/^#]+)$")));
    System.out.println("123123".matches("^\\d+$"));
    System.out.println(Utils.parseInt("1000  K"));
  }

}
