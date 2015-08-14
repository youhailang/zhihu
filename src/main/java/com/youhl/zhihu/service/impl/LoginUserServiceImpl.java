package com.youhl.zhihu.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.dao.LoginUserDao;
import com.youhl.zhihu.entity.LoginUser;
import com.youhl.zhihu.service.LoginUserService;
import com.youhl.zhihu.utils.Utils;

@Service("loginUserService")
public class LoginUserServiceImpl extends BaseServiceImpl<LoginUser, String> implements
    LoginUserService {
  public static final String LOGIN_URL = "http://www.zhihu.com/login/email";
  @Autowired
  private LoginUserDao loginUserDao;
  @Autowired
  private HttpClient httpClient;

  @Override
  public GenericDao<LoginUser, String> getMainDao() {
    return loginUserDao;
  }

  @Override
  public void login(String id) {
    LoginUser user = get(id);
    if (user != null) {
      HttpPost httpPost = new HttpPost(LOGIN_URL);
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("email", user.getUsername()));
      params.add(new BasicNameValuePair("password", user.getPassword()));
      try {
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          // 有验证码 暂不解析
          StringWriter sw = new StringWriter();
          IOUtils.copy(entity.getContent(), sw);
          System.out.println(Utils.unicode2String(sw.toString()));
          httpPost.abort();
        }
      } catch (ClientProtocolException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
