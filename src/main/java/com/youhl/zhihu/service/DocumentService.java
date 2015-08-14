package com.youhl.zhihu.service;

import java.io.Serializable;

import org.jsoup.nodes.Document;

public interface DocumentService<T, PK extends Serializable> extends GenericService<T, PK> {
  void parse(String url, Document doc) throws Exception;
}
