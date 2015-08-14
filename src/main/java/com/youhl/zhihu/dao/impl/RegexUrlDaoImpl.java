package com.youhl.zhihu.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.RegexUrlDao;
import com.youhl.zhihu.entity.RegexUrl;

@Repository("regexUrlDao")
public class RegexUrlDaoImpl extends BaseDaoImpl<RegexUrl, String> implements RegexUrlDao {

  @SuppressWarnings("unchecked")
  @Override
  public List<RegexUrl> queryAll() {
    List<RegexUrl> list =
        this.getCurrentSession().createQuery("from " + getEntityName() + " where flag = 1")
            .setCacheable(true).list();
    return list;
  }
}
