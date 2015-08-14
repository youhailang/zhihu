package com.youhl.zhihu.dao.impl;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.ZAnswerDao;
import com.youhl.zhihu.entity.ZAnswer;

@Repository("zAnswerDao")
public class ZAnswerDaoImpl extends BaseDaoImpl<ZAnswer, Long> implements ZAnswerDao {
}
