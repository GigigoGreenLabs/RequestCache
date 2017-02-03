package com.zireck.requestcache.library.cache;

import com.zireck.requestcache.library.model.RequestModel;
import java.util.List;

interface IterableQueue<T> {
  boolean isEmpty();
  void add(T element);
  void add(List<T> elements);
  boolean hasNext();
  RequestModel next();
  void remove();
  boolean clear();
}
