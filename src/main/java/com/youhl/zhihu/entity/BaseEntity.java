package com.youhl.zhihu.entity;

import java.util.Date;

public abstract class BaseEntity implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  protected Date updateTime = new Date();
  protected Date createTime = new Date();

  public BaseEntity() {}


  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }



  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

}
