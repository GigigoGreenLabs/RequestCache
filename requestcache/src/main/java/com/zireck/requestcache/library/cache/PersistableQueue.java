package com.zireck.requestcache.library.cache;

interface PersistableQueue {
  void loadToMemory();
  boolean persistToDisk();
}
