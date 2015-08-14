package com.youhl.zhihu.dao;

import com.youhl.zhihu.entity.ZPeople;

public interface ZPeopleDao extends GenericDao<ZPeople, Long> {
  ZPeople getByUrl(String url);
}
