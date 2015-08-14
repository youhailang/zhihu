package com.youhl.zhihu.dao;

import java.util.List;

import com.youhl.zhihu.entity.RegexUrl;

public interface RegexUrlDao extends GenericDao<RegexUrl, String> {
  List<RegexUrl> queryAll();
}
