package com.zireck.requestcache.library.util.sleeper;

public class ThreadSleeper implements Sleeper {

  @Override public void sleep(long millis) throws InterruptedException {
    Thread.sleep(millis);
  }
}
