package com.zireck.requestcache.library.cache;

interface PersistableQueue {
  void loadToMemory();
  void persistToDisk();
}
