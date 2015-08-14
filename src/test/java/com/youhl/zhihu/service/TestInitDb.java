package com.youhl.zhihu.service;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.entity.LoginUser;
import com.youhl.zhihu.entity.RegexUrl;
import com.youhl.zhihu.entity.SeedUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml", "classpath:spring-hibernate.xml"})
public class TestInitDb extends ApplicationObjectSupport {
  @Autowired
  private LoginUserService loginUserService;
  @Autowired
  private RegexUrlService regexUrlService;
  @Autowired
  private SeedUrlService seedUrlService;

  @SuppressWarnings("rawtypes")
  @Test
  public void init() {

    Map<String, GenericDao> beans = getApplicationContext().getBeansOfType(GenericDao.class);
    for (GenericDao bean : beans.values()) {
      bean.deleteAll();
    }
    // 创建用户
    loginUserService.save(new LoginUser("906669319@qq.com", "xxxxxx"));
    // 创建种子
    seedUrlService.save(new SeedUrl("http://www.zhihu.com/people/hailang-you"));
    // 创建规则
    regexUrlService.save(new RegexUrl("^http://www.zhihu.com/people/[^/^#]+$", "zPeopleService"));
    regexUrlService.save(new RegexUrl("^http://www.zhihu.com/question/\\d+$",
        "zQuestionService,zAnswerService"));

  }

}
