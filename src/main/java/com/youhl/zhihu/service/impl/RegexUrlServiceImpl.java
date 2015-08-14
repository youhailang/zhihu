package com.youhl.zhihu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.RegexUrlDao;
import com.youhl.zhihu.entity.RegexUrl;
import com.youhl.zhihu.service.RegexUrlService;

@Service("regexUrlService")
public class RegexUrlServiceImpl extends BaseServiceImpl<RegexUrl, String> implements
    RegexUrlService {
  @Autowired
  private RegexUrlDao regexUrlDao;

  @Override
  public GenericDao<RegexUrl, String> getMainDao() {
    return regexUrlDao;
  }

  @Override
  public List<RegexUrl> queryAll() {
    return regexUrlDao.queryAll();
  }

  private List<RegexUrl> vaildRgexUrls = null;

  private synchronized void initVaildRgexUrls() {
    if (vaildRgexUrls == null) {
      vaildRgexUrls = Lists.newArrayList();
      for (RegexUrl regexUrl : queryAll()) {
        if (regexUrl.getFlag() == 1) {
          vaildRgexUrls.add(regexUrl);
        }
      }
    }
  }

  @Override
  public RegexUrl getVaildByUrl(String url) {
    if (vaildRgexUrls == null) {
      initVaildRgexUrls();
    }
    for (RegexUrl regexUrl : vaildRgexUrls) {
      if (regexUrl.getFlag() == 1 && url.matches(regexUrl.getRegex())) {
        return regexUrl;
      }
    }
    return null;
  }


}
