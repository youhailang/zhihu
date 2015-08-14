package com.youhl.zhihu.dao;

import java.util.Date;

import com.youhl.zhihu.entity.ZPeople;
import com.youhl.zhihu.entity.ZPeopleActivity;

public interface ZPeopleActivityDao extends GenericDao<ZPeopleActivity, Long> {
  ZPeopleActivity getByPeopleAndDataTime(ZPeople people, Date date);
}
