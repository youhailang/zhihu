package com.youhl.zhihu.service;

import java.io.Serializable;
import java.util.List;

import com.youhl.zhihu.dao.GenericDao;

public interface GenericService<T, PK extends Serializable> {

  T load(PK id);

  T get(PK id);

  List<T> findAll();

  void persist(T entity);

  PK save(T entity);

  void saveOrUpdate(T entity);

  void delete(PK id);

  void flush();

  int deleteAll();

  void update(T entity);

  GenericDao<T, PK> getMainDao();
}
