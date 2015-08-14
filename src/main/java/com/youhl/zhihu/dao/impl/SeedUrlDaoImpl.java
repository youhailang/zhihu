package com.youhl.zhihu.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.SeedUrlDao;
import com.youhl.zhihu.entity.SeedUrl;

@SuppressWarnings("unchecked")
@Repository("seedUrlDao")
public class SeedUrlDaoImpl extends BaseDaoImpl<SeedUrl, Long> implements SeedUrlDao {

  @Override
  public SeedUrl getByUrl(String url) {
    List<SeedUrl> list =
        this.getCurrentSession()
            .createQuery("from " + getEntityName() + " where url=:url order by id")
            .setParameter("url", url).setCacheable(true).list();
    return list.size() > 0 ? list.get(0) : null;
  }
}
