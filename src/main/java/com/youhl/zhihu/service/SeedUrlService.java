package com.youhl.zhihu.service;

import java.util.Map;

import com.youhl.zhihu.entity.SeedUrl;

public interface SeedUrlService extends DocumentService<SeedUrl, Long> {
  void init();

  void combine();

  Map<String, SeedUrl> getValidSeedUrl();
  
  
}
