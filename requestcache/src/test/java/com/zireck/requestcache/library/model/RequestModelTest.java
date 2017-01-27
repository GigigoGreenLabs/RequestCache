package com.zireck.requestcache.library.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.zireck.requestcache.library.util.MethodType;
import java.util.HashMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestModelTest {

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoMethodTypeGiven() throws Exception {
    new RequestModel.Builder().baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenInvalidMethodTypeGiven() throws Exception {
    new RequestModel.Builder().baseUrl("https://api.github.com/")
        .methodType(null)
        .endpoint("users/GigigoGreenLabs/repos")
        .build();
  }

  @Test(expected = IllegalStateException.class) public void shouldThrowExceptionWhenNoBaseUrlGiven()
      throws Exception {
    new RequestModel.Builder().methodType(MethodType.GET)
        .endpoint("users/GigigoGreenLabs/repos")
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenInvalidBaseUrlGiven() throws Exception {
    new RequestModel.Builder().methodType(MethodType.POST)
        .baseUrl("")
        .endpoint("users/GigigoGreenLabs/repos")
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoEndpointGiven() throws Exception {
    new RequestModel.Builder().methodType(MethodType.GET)
        .baseUrl("https://api.github.com/")
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenInvalidEndpointGiven() throws Exception {
    new RequestModel.Builder().methodType(MethodType.POST)
        .baseUrl("https://api.github.com/")
        .endpoint(null)
        .build();
  }

  @Test public void shouldProperlyBuildObjectWhenEveryFieldIsGiven() throws Exception {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Cookie", "qwdADFg45sdf454d");
    HashMap<String, String> query = new HashMap<>();
    query.put("login", "gigigo");
    query.put("password", "gigipass");
    JsonObject body = new JsonObject();
    body.addProperty("status", "success");

    RequestModel requestModel = new RequestModel.Builder<JsonObject>().methodType(MethodType.POST)
        .headers(headers)
        .baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .query(query)
        .body(body)
        .build();

    assertThat(requestModel, notNullValue());
    assertThat(requestModel.getMethodType(), is(MethodType.POST));
    assertThat(requestModel.getHeaders(), notNullValue());
    assertThat(requestModel.getHeaders().containsKey("Cookie"), is(true));
    assertThat(requestModel.getHeaders().containsValue("qwdADFg45sdf454d"), is(true));
    assertThat(requestModel.getHeaders().get("Cookie"), instanceOf(String.class));
    assertThat(((String) requestModel.getHeaders().get("Cookie")), is("qwdADFg45sdf454d"));
    assertThat(requestModel.getBaseUrl(), is("https://api.github.com/"));
    assertThat(requestModel.getEndpoint(), is("users/GigigoGreenLabs/repos"));
    assertThat(requestModel.getQuery(), notNullValue());
    assertThat(requestModel.getQuery().containsKey("login"), is(true));
    assertThat(requestModel.getQuery().containsValue("gigipass"), is(true));
    assertThat(requestModel.getQuery().get("login"), instanceOf(String.class));
    assertThat(((String) requestModel.getQuery().get("password")), is("gigipass"));
    assertThat(requestModel.getBody(), notNullValue());
    assertThat(requestModel.getBody(), instanceOf(JsonObject.class));
    assertThat(((JsonObject) requestModel.getBody()).get("status"), notNullValue());
    assertThat(((JsonObject) requestModel.getBody()).get("status"),
        instanceOf(JsonPrimitive.class));
    assertThat(((JsonObject) requestModel.getBody()).get("status").getAsString(), is("success"));
  }
}