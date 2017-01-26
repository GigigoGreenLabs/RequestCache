package com.zireck.requestcache.library.network;

import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class RetrofitNetworkRequestManagerTest {

  @Mock ApiService mockApiService;
  @Mock NetworkResponseCallback mockNetworkResponseCallback;
  private RetrofitNetworkRequestManager retrofitNetworkRequestManagerWithRealApiService;
  private RetrofitNetworkRequestManager retrofitNetworkRequestManagerWithMockApiService;
  private MockWebServer mockWebServer;
  private ApiService apiService;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    mockWebServer = new MockWebServer();
    mockWebServer.start();

    HttpUrl mockUrl = mockWebServer.url("/");

    ApiServiceBuilder apiServiceBuilder = new ApiServiceBuilder(mockUrl.toString());
    apiService = apiServiceBuilder.build();

    retrofitNetworkRequestManagerWithRealApiService = new RetrofitNetworkRequestManager(apiService);
    retrofitNetworkRequestManagerWithMockApiService =
        new RetrofitNetworkRequestManager(mockApiService);
  }

  @Test public void shouldReturnFailureWhenNullRequestGiven() throws Exception {
    retrofitNetworkRequestManagerWithRealApiService.sendRequest(null, mockNetworkResponseCallback);

    verify(mockNetworkResponseCallback, times(1)).onFailure();
    verifyNoMoreInteractions(mockNetworkResponseCallback);
  }

  @Test public void shouldReturnValidCallWhenValidRequestGiven() throws Exception {
    RequestModel validRequest = getValidRequest();

    Call<ResponseBody> call =
        apiService.requestGet(validRequest.getBaseUrl() + validRequest.getEndpoint());

    assertThat(call, notNullValue());
    assertThat(call, instanceOf(Call.class));
  }

  @Test public void shouldNotifyWhenValidResponseReceived() throws Exception {
    MockResponse successfulMockResponse = getSuccessfulMockResponse();
    mockWebServer.enqueue(successfulMockResponse);
    RequestModel validRequest = getValidRequest();

    Call<ResponseBody> call =
        apiService.requestGet(mockWebServer.url("/") + validRequest.getEndpoint());
    Response<ResponseBody> response = call.execute();

    assertThat(response, notNullValue());
    assertThat(response.isSuccessful(), is(true));
    assertThat(response.code(), is(200));
  }

  @Test public void shouldNotifyWhenInvalidResponseReceived() throws Exception {
    MockResponse unsuccessfulResponse = getUnsuccessfulResponse();
    mockWebServer.enqueue(unsuccessfulResponse);
    RequestModel validRequest = getValidRequest();

    Call<ResponseBody> call =
        apiService.requestGet(mockWebServer.url("/") + validRequest.getEndpoint());
    Response<ResponseBody> response = call.execute();

    assertThat(response, notNullValue());
    assertThat(response.isSuccessful(), is(false));
    assertThat(response.code(), is(404));
  }

  @Test public void shouldEnqueueRequestWhenValidRequestReceived() throws Exception {
    RequestModel validRequest = getValidRequest();
    Call<ResponseBody> mockRetrofitCallback = mock(Call.class);
    when(mockApiService.requestGet(
        validRequest.getBaseUrl() + validRequest.getEndpoint())).thenReturn(mockRetrofitCallback);

    retrofitNetworkRequestManagerWithMockApiService.sendRequest(validRequest,
        mockNetworkResponseCallback);

    verify(mockRetrofitCallback, times(1)).enqueue(any(Callback.class));
    verifyNoMoreInteractions(mockRetrofitCallback);
  }

  @Test public void shouldNotifyFailureWhenInvalidResponseReceived() throws Exception {
    RequestModel validRequest = getValidRequest();
    when(mockApiService.requestGet(any(String.class))).thenReturn(null);

    retrofitNetworkRequestManagerWithMockApiService.sendRequest(validRequest,
        mockNetworkResponseCallback);

    verify(mockNetworkResponseCallback, times(1)).onFailure();
    verifyNoMoreInteractions(mockNetworkResponseCallback);
  }

  private RequestModel getValidRequest() {
    return new RequestModel.Builder<>().methodType(MethodType.GET)
        .baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .build();
  }

  private MockResponse getSuccessfulMockResponse() {
    MockResponse mockResponse = new MockResponse();
    mockResponse.setResponseCode(200);

    return mockResponse;
  }

  private MockResponse getUnsuccessfulResponse() {
    MockResponse mockResponse = new MockResponse();
    mockResponse.setResponseCode(404);

    return mockResponse;
  }
}