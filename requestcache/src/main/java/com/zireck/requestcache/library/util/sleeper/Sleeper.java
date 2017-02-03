package com.zireck.requestcache.library.util.sleeper;

public interface Sleeper {
  void sleep(long millis) throws InterruptedException;
}
