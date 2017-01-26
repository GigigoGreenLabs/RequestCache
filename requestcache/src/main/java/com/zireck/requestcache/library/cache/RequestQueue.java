package com.zireck.requestcache.library.cache;

import com.zireck.requestcache.library.model.RequestModel;
import java.util.List;

public interface RequestQueue {
  boolean isEmpty();
  void add(RequestModel requestModel);
  void add(List<RequestModel> requestModels);
  void load();
  boolean persist();
  boolean hasNext();
  RequestModel next();
  void remove();
  boolean clear();
}
