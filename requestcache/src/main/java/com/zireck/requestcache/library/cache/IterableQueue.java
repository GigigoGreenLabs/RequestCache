package com.zireck.requestcache.library.cache;

import com.zireck.requestcache.library.model.RequestModel;
import java.util.List;

interface IterableQueue {
  boolean isEmpty();
  void add(RequestModel requestModel);
  void add(List<RequestModel> requestModels);
  boolean hasNext();
  RequestModel next();
  void remove();
  boolean clear();
}
