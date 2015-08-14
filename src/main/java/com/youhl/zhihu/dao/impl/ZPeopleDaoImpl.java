package com.youhl.zhihu.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.ZPeopleDao;
import com.youhl.zhihu.entity.ZPeople;

@SuppressWarnings("unchecked")
@Repository("zPeopleDao")
public class ZPeopleDaoImpl extends BaseDaoImpl<ZPeople, Long> implements ZPeopleDao {

  @Override
  public ZPeople getByUrl(String url) {
    List<ZPeople> list =
        this.getCurrentSession()
            .createQuery("from " + getEntityName() + " where url=:url order by id")
            .setParameter("url", url).setCacheable(true).list();
    return list.size() > 0 ? list.get(0) : null;
  }
}
