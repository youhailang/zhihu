package com.youhl.zhihu.dao.impl;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.LoginUserDao;
import com.youhl.zhihu.entity.LoginUser;

@Repository("loginUserDao")
public class LoginUserDaoImpl extends BaseDaoImpl<LoginUser, String> implements LoginUserDao {
}
