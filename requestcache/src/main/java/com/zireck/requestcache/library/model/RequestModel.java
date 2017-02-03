package com.zireck.requestcache.library.model;

import com.zireck.requestcache.library.util.MethodType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestModel<T> {

  private final MethodType methodType;
  private final Map<String, String> headers;
  private final String baseUrl;
  private final String endpoint;
  private final Map<String, String> query;
  private final T body;

  private RequestModel(Builder builder) {
    this.methodType = builder.methodType;
    this.headers = Collections.unmodifiableMap(builder.headers);
    this.baseUrl = builder.baseUrl;
    this.endpoint = builder.endpoint;
    this.query = Collections.unmodifiableMap(builder.query);
    this.body = (T) builder.body;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getUrl() {
    return baseUrl + endpoint;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public Map<String, String> getQuery() {
    return query;
  }

  public T getBody() {
    return body;
  }

  public static class Builder<T> {

    private MethodType methodType;
    private Map<String, String> headers = new HashMap<>();
    private String baseUrl;
    private String endpoint;
    private Map<String, String> query = new HashMap<>();
    private T body;

    public Builder() {

    }

    public RequestModel build() {
      validateModel();
      return new RequestModel(this);
    }

    public Builder methodType(MethodType methodType) {
      this.methodType = methodType;
      return this;
    }

    public Builder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public Builder query(Map<String, String> query) {
      this.query = query;
      return this;
    }

    public Builder body(T body) {
      this.body = body;
      return this;
    }

    private void validateModel() {
      if (methodType == null) {
        throw new IllegalStateException("Method Type must be defined.");
      } else if (baseUrl == null || baseUrl.length() <= 0) {
        throw new IllegalStateException("Base Url must be defined.");
      } else if (endpoint == null || endpoint.length() <= 0) {
        throw new IllegalStateException("Endpoint must be defined.");
      }

      if (methodType == MethodType.POST && body == null) {
        throw new IllegalStateException("Method POST must have a request body.");
      }
    }
  }
}
