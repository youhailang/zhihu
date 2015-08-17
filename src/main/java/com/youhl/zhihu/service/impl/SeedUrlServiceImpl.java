package com.youhl.zhihu.service.impl;

import static com.youhl.zhihu.utils.Utils.contents;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.SeedUrlDao;
import com.youhl.zhihu.entity.RegexUrl;
import com.youhl.zhihu.entity.SeedUrl;
import com.youhl.zhihu.service.RegexUrlService;
import com.youhl.zhihu.service.SeedUrlService;

@Service("seedUrlService")
public class SeedUrlServiceImpl extends BaseServiceImpl<SeedUrl, Long> implements SeedUrlService {
  private static final Logger LOGGER = Logger.getLogger(SeedUrlServiceImpl.class);
  @Autowired
  private SeedUrlDao seedUrlDao;
  @Autowired
  private RegexUrlService regexUrlService;

  @Override
  public GenericDao<SeedUrl, Long> getMainDao() {
    return seedUrlDao;
  }

  private Map<String, SeedUrl> validSeedUrls = Maps.newHashMap();
  private Map<String, SeedUrl> expiredSeedUrls = Maps.newHashMap();
  private Map<String, SeedUrl> penddingSeedUrls = Maps.newHashMap();


  @Override
  public void parse(String url, Document doc) throws Exception {
    List<String> hrefs = contents(doc, "a", "abs:href".split(","), null);
    SeedUrl parent = seedUrlDao.getByUrl(url);
    for (String href : Sets.newTreeSet(hrefs)) {
      SeedUrl seedUrl = new SeedUrl(href, parent);
      seedUrl.setRegexUrl(getRegexUrl(href));
      if (seedUrl.getRegexUrl() != null) {
        seedUrlDao.save(seedUrl);
        penddingSeedUrls.put(href, seedUrl);
        info("addSeed:" + href);
      }
    }
  }

  private RegexUrl getRegexUrl(String href) {
    if (validSeedUrls.containsKey(href) || expiredSeedUrls.containsKey(href)) {
      return null;
    }
    return regexUrlService.getVaildByUrl(href);
  }

  @Override
  public void init() {
    List<SeedUrl> list = findAll();
    for (SeedUrl seedUrl : list) {
      if (vaild(seedUrl)) {
        seedUrl.setRegexUrl(getRegexUrl(seedUrl.getUrl()));
        if (seedUrl.getRegexUrl() != null) {
          validSeedUrls.put(seedUrl.getUrl(), seedUrl);
          continue;
        }
      }
      expiredSeedUrls.put(seedUrl.getUrl(), seedUrl);
    }
  }

  private boolean vaild(SeedUrl seedUrl) {
    return (seedUrl.getFlag() == 1 && seedUrl.getFailure() < 3 && seedUrl.getSuccess() < 3)
        || seedUrl.getId() == 1;
  }

  @Override
  public void combine() {
    // info("combine-begin");
    expiredSeedUrls.putAll(validSeedUrls);
    validSeedUrls.clear();

    validSeedUrls.putAll(penddingSeedUrls);
    penddingSeedUrls.clear();
    // info("combine-end");
  }

  protected void info(String tag) {
    LOGGER.info(String.format("%s:valid-%d,expired-%d,pendding-%d", tag, validSeedUrls.size(),
        expiredSeedUrls.size(), penddingSeedUrls.size()));
  }

  @Override
  public Map<String, SeedUrl> getValidSeedUrl() {
    return validSeedUrls;
  }
}
