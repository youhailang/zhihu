package com.youhl.zhihu.dao;

import com.youhl.zhihu.entity.SeedUrl;

public interface SeedUrlDao extends GenericDao<SeedUrl, Long> {
  SeedUrl getByUrl(String url);
}
