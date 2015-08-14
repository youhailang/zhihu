package com.youhl.zhihu.service;

import com.youhl.zhihu.entity.LoginUser;

public interface LoginUserService extends GenericService<LoginUser, String> {
  /* 登陆 */
  void login(String id);
}
