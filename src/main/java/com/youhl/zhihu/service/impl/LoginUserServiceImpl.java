package com.youhl.zhihu.service.impl;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.LoginUserDao;
import com.youhl.zhihu.entity.LoginUser;
import com.youhl.zhihu.service.LoginUserService;
import com.youhl.zhihu.utils.HttpUtils;

@Service("loginUserService")
public class LoginUserServiceImpl extends BaseServiceImpl<LoginUser, String> implements
    LoginUserService {
  @Autowired
  private LoginUserDao loginUserDao;
  @Autowired
  private CloseableHttpClient httpClient;

  @Override
  public GenericDao<LoginUser, String> getMainDao() {
    return loginUserDao;
  }

  @Override
  public void login(String id) {
    LoginUser user = get(id);
    if (user != null) {
      HttpUtils.login(httpClient, user);
    }
  }


}
