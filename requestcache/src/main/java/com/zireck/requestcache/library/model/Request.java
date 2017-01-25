package com.zireck.requestcache.library.model;

public class Request<T> {
  private String url;
  private String endpoint;
  private T body;

  public Request(String url, String endpoint) {
    this.url = url;
    this.endpoint = endpoint;
  }

  public Request(String url, String endpoint, T body) {
    this.url = url;
    this.endpoint = endpoint;
    this.body = body;
  }

  public String getUrl() {
    return url;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public boolean hasBody() {
    return body != null;
  }

  public T getBody() {
    return body;
  }
}
