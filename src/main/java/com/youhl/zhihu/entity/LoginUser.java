package com.youhl.zhihu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "z_login_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LoginUser extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private String username;
  private String password;

  public LoginUser() {}

  public LoginUser(String username, String password) {
    super();
    this.username = username;
    this.password = password;
  }

  @Id
  @Column(name = "username", unique = true, nullable = false, length = 36)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Column(name = "password", nullable = false)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_time", length = 19)
  public Date getUpdateTime() {
    return updateTime;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_time", length = 19, updatable = false)
  public Date getCreateTime() {
    return createTime;
  }
}
