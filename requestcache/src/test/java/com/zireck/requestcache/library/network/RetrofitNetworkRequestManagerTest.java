package com.zireck.requestcache.library.network;

import com.zireck.requestcache.library.executor.ThreadExecutor;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import java.io.IOException;
import java.util.Map;
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
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class RetrofitNetworkRequestManagerTest {

  @Mock private ThreadExecutor mockThreadExecutor;
  @Mock private ApiService mockApiService;
  @Mock private NetworkResponseCallback mockNetworkResponseCallback;
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

    retrofitNetworkRequestManagerWithMockApiService =
        new RetrofitNetworkRequestManager(mockThreadExecutor, mockApiService);
  }

  @Test public void shouldReturnFailureWhenNullRequestGiven() throws Exception {
    retrofitNetworkRequestManagerWithMockApiService.getRequestStream(null, mockNetworkResponseCallback);

    verify(mockNetworkResponseCallback, times(1)).onFailure();
    verifyNoMoreInteractions(mockNetworkResponseCallback);
    verifyZeroInteractions(mockThreadExecutor);
  }

  @Test public void shouldReturnValidCallWhenValidRequestGiven() throws Exception {
    RequestModel validRequest = getValidRequest();

    Call<ResponseBody> call =
        apiService.requestGet(validRequest.getHeaders(), validRequest.getUrl(),
            validRequest.getQuery());

    assertThat(call, notNullValue());
    assertThat(call, instanceOf(Call.class));
  }

  @Test public void shouldNotifyWhenValidResponseReceived() throws Exception {
    MockResponse successfulMockResponse = getSuccessfulMockResponse();
    mockWebServer.enqueue(successfulMockResponse);
    RequestModel validRequest = getValidRequest();
    String requestUrl = mockWebServer.url("/") + validRequest.getEndpoint();

    Call<ResponseBody> call =
        apiService.requestGet(validRequest.getHeaders(), requestUrl, validRequest.getQuery());
    Response<ResponseBody> response = call.execute();

    assertThat(response, notNullValue());
    assertThat(response.isSuccessful(), is(true));
    assertThat(response.code(), is(200));
  }

  @Test public void shouldNotifyWhenInvalidResponseReceived() throws Exception {
    MockResponse unsuccessfulResponse = getUnsuccessfulResponse();
    mockWebServer.enqueue(unsuccessfulResponse);
    RequestModel validRequest = getValidRequest();
    String requestUrl = mockWebServer.url("/") + validRequest.getEndpoint();

    Call<ResponseBody> call =
        apiService.requestGet(validRequest.getHeaders(), requestUrl, validRequest.getQuery());
    Response<ResponseBody> response = call.execute();

    assertThat(response, notNullValue());
    assertThat(response.isSuccessful(), is(false));
    assertThat(response.code(), is(404));
  }

  @Test public void shouldExecuteRequestWhenValidRequestComposed() throws Exception {
    RequestModel validRequest = getValidRequest();
    Call<ResponseBody> mockRetrofitCall = mock(Call.class);
    when(mockApiService.requestGet(validRequest.getHeaders(), validRequest.getUrl(),
        validRequest.getQuery())).thenReturn(mockRetrofitCall);

    retrofitNetworkRequestManagerWithMockApiService.getRequestStream(validRequest,
        mockNetworkResponseCallback);
    retrofitNetworkRequestManagerWithMockApiService.run();

    verify(mockRetrofitCall, times(1)).execute();
    verifyNoMoreInteractions(mockRetrofitCall);
  }

  @Test public void shouldNotifyFailureWhenExceptionThrownWhileExecutingRequest() throws Exception {
    RequestModel validRequest = getValidRequest();
    Call<ResponseBody> mockRetrofitCall = mock(Call.class);
    doThrow(new IOException()).when(mockRetrofitCall).execute();
    when(mockApiService.requestGet(validRequest.getHeaders(), validRequest.getUrl(),
        validRequest.getQuery())).thenReturn(mockRetrofitCall);

    retrofitNetworkRequestManagerWithMockApiService.getRequestStream(validRequest,
        mockNetworkResponseCallback);
    retrofitNetworkRequestManagerWithMockApiService.run();

    verify(mockNetworkResponseCallback, times(1)).onFailure();
  }

  @Test public void shouldNotifyFailureWhenInvalidResponseReceived() throws Exception {
    RequestModel validRequest = getValidRequest();
    when(mockApiService.requestGet(any(Map.class), any(String.class), any(Map.class))).thenReturn(
        null);

    retrofitNetworkRequestManagerWithMockApiService.getRequestStream(validRequest,
        mockNetworkResponseCallback);
    retrofitNetworkRequestManagerWithMockApiService.run();

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