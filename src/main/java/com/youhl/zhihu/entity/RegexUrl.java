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
@Table(name = "z_regex_url")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RegexUrl extends BaseEntity {
  private static final long serialVersionUID = 9150791560470546655L;
  private String regex;
  // 处理类
  private String serviceName;
  // 0 关闭 1 开启
  private int flag = 1;

  public RegexUrl() {}

  public RegexUrl(String regex, String serviceName) {
    super();
    this.regex = regex;
    this.serviceName = serviceName;
  }

  @Id
  @Column(name = "regex", unique = true, nullable = false)
  public String getRegex() {
    return regex;
  }


  @Column(name = "flag", nullable = false)
  public int getFlag() {
    return flag;
  }

  @Column(name = "service_name", nullable = false)
  public String getServiceName() {
    return serviceName;
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

  public void setRegex(String regex) {
    this.regex = regex;
  }


  public void setFlag(int flag) {
    this.flag = flag;
  }


  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

}
