package com.youhl.zhihu.dao.impl;

import org.springframework.stereotype.Repository;

import com.youhl.zhihu.dao.ZQuestionDao;
import com.youhl.zhihu.entity.ZQuestion;

@Repository("zQuestionDao")
public class ZQuestionDaoImpl extends BaseDaoImpl<ZQuestion, Long> implements ZQuestionDao {
}
