package com.zireck.requestcache.library.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class GsonSerializer implements JsonSerializer {

  private final Gson gson;

  public GsonSerializer() {
    this.gson = new Gson();
  }

  @Override public String toJson(Object object) {
    return gson.toJson(object);
  }

  @Override public Object fromJson(String string, Type type) {
    return gson.fromJson(string, type);
  }
}
