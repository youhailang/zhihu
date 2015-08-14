package com.youhl.zhihu.service;

import java.util.List;

import com.youhl.zhihu.entity.RegexUrl;

public interface RegexUrlService extends GenericService<RegexUrl, String> {
  List<RegexUrl> queryAll();

  RegexUrl getVaildByUrl(String url);
}
