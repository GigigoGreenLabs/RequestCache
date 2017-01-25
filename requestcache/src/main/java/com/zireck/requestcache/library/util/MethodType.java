package com.zireck.requestcache.library.util;

public enum MethodType {
  GET("GET"),
  POST("POST");

  private final String methodName;

  private MethodType(final String methodName) {
    this.methodName = methodName;
  }

  @Override public String toString() {
    return methodName;
  }
}
