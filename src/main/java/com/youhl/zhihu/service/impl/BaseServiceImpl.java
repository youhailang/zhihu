package com.youhl.zhihu.service.impl;

import java.io.Serializable;
import java.util.List;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.service.GenericService;

public abstract class BaseServiceImpl<T, PK extends Serializable> implements GenericService<T, PK> {

  @Override
  public abstract GenericDao<T, PK> getMainDao();


  public T load(PK id) {
    return (T) getMainDao().load(id);
  }

  public T get(PK id) {
    return (T) getMainDao().get(id);
  }

  public List<T> findAll() {
    return getMainDao().findAll();
  }

  public void persist(T entity) {
    getMainDao().persist(entity);
  }

  public PK save(T entity) {
    return getMainDao().save(entity);
  }

  public void saveOrUpdate(T entity) {
    getMainDao().saveOrUpdate(entity);
  }

  public void delete(PK id) {
    getMainDao().delete(id);
  }

  public void flush() {
    getMainDao().flush();
  }

  public int deleteAll() {
    return getMainDao().deleteAll();
  }

  @Override
  public void update(T entity) {
    getMainDao().update(entity);
  }
}
