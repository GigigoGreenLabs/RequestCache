package com.zireck.requestcache.library.model;

import com.zireck.requestcache.library.util.MethodType;
import java.util.HashMap;
import java.util.Map;

public class RequestModel<T> {

  private MethodType methodType;
  private Map<String, String> headers = new HashMap<>();
  private String baseUrl;
  private String endpoint;
  private Map<String, String> query = new HashMap<>();
  private T body;

  private RequestModel() {

  }

  public MethodType getMethodType() {
    return methodType;
  }

  public Map<String, String> getHeaders() {
    return headers;
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
    private RequestModel requestModel;

    public Builder() {
      requestModel = new RequestModel();
    }

    public RequestModel build() {
      validateModel();
      return requestModel;
    }

    public Builder methodType(MethodType methodType) {
      requestModel.methodType = methodType;
      return this;
    }

    public Builder headers(Map<String, String> headers) {
      requestModel.headers = headers;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      requestModel.baseUrl = baseUrl;
      return this;
    }

    public Builder endpoint(String endpoint) {
      requestModel.endpoint = endpoint;
      return this;
    }

    public Builder query(Map<String, String> query) {
      requestModel.query = query;
      return this;
    }

    public Builder body(T body) {
      requestModel.body = body;
      return this;
    }

    private void validateModel() {
      if (requestModel.methodType == null) {
        throw new IllegalStateException("Method Type must be defined.");
      } else if (requestModel.baseUrl == null || requestModel.baseUrl.length() <= 0) {
        throw new IllegalStateException("Base Url must be defined.");
      } else if (requestModel.endpoint == null || requestModel.endpoint.length() <= 0) {
        throw new IllegalStateException("Endpoint must be defined.");
      }

      if (requestModel.methodType == MethodType.POST && requestModel.body == null) {
        throw new IllegalStateException("Method POST must have a request body.");
      }
    }
  }
}
