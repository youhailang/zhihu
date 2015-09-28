package com.youhl.zhihu.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;

import com.youhl.zhihu.dao.GenericDao;
import com.youhl.zhihu.entity.BaseEntity;

@SuppressWarnings("unchecked")
public abstract class BaseDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {
  @Autowired
  protected SessionFactory sessionFactory;

  protected Session getCurrentSession() {
    return this.sessionFactory.getCurrentSession();
  }

  @SuppressWarnings("rawtypes")
  private Class getGenericType(int index) {
    Type genType = getClass().getGenericSuperclass();
    if (!(genType instanceof ParameterizedType)) {
      return Object.class;
    }
    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
    if (index >= params.length || index < 0) {
      throw new RuntimeException("Index outof bounds");
    }
    if (!(params[index] instanceof Class)) {
      return Object.class;
    }
    return (Class) params[index];
  }

  protected Class<?> getEntityClass() {
    return getGenericType(0);
  }

  protected String getEntityName() {
    return getEntityClass().getSimpleName();
  }

  protected String getTableName() {
    AbstractEntityPersister classMetadata =
        (AbstractEntityPersister) getCurrentSession().getSessionFactory().getClassMetadata(
            getEntityClass());
    return classMetadata.getTableName();
  }

  public T load(PK id) {
    return (T) this.getCurrentSession().load(getEntityClass(), id);
  }

  public T get(PK id) {
    return (T) this.getCurrentSession().get(getEntityClass(), id);
  }

  public List<T> findAll() {
    List<T> list =
        this.getCurrentSession().createQuery("from " + getEntityClass().getSimpleName())
            .setCacheable(true).list();
    return list;
  }

  public void persist(T entity) {
    this.getCurrentSession().persist(entity);

  }

  public PK save(T entity) {
    return (PK) this.getCurrentSession().save(entity);
  }

  public void saveOrUpdate(T entity) {
    if (entity instanceof BaseEntity) {
      ((BaseEntity) entity).setUpdateTime(new Date());
    }
    this.getCurrentSession().saveOrUpdate(entity);
  }

  public void delete(PK id) {
    T entity = this.load(id);
    this.getCurrentSession().delete(entity);
  }

  public void flush() {
    this.getCurrentSession().flush();
  }

  @Override
  public int deleteAll() {
    return getCurrentSession().createSQLQuery("truncate table " + getTableName()).executeUpdate();
  }

  @Override
  public void update(T entity) {
    if (entity instanceof BaseEntity) {
      ((BaseEntity) entity).setUpdateTime(new Date());
    }
    this.getCurrentSession().update(entity);
  }
}
