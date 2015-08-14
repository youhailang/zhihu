package com.youhl.zhihu.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.ZPeopleActivityDao;
import com.youhl.zhihu.entity.ZPeople;
import com.youhl.zhihu.entity.ZPeopleActivity;

@SuppressWarnings("unchecked")
@Repository("zPeopleActivityDao")
public class ZPeopleActivityDaoImpl extends BaseDaoImpl<ZPeopleActivity, Long> implements
    ZPeopleActivityDao {

  @Override
  public ZPeopleActivity getByPeopleAndDataTime(ZPeople people, Date dataTime) {
    List<ZPeopleActivity> list =
        this.getCurrentSession()
            .createQuery(
                "from " + getEntityName()
                    + " where people=:people and dataTime=:dataTime order by id")
            .setParameter("people", people).setParameter("dataTime", dataTime).setCacheable(true)
            .list();
    return list.size() > 0 ? list.get(0) : null;
  }

}
