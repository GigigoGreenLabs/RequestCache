package com.zireck.requestcache.library.executor;

import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import com.zireck.requestcache.library.network.NetworkResponseCallback;
import com.zireck.requestcache.library.util.logger.Logger;
import com.zireck.requestcache.library.util.MethodType;
import com.zireck.requestcache.library.util.sleeper.Sleeper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class PendingRequestsExecutorTest {

  private PendingRequestsExecutor pendingRequestsExecutor;

  @Mock private ThreadExecutor threadExecutor;
  @Mock private NetworkRequestManager mockNetworkRequestManager;
  @Mock private Logger logger;
  @Mock private Sleeper sleeper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    pendingRequestsExecutor =
        new PendingRequestsExecutor(threadExecutor, mockNetworkRequestManager, logger, sleeper);
  }

  @Test public void shouldNotBeExecutingRightAfterItsInstantiated() throws Exception {
    boolean isExecuting = pendingRequestsExecutor.isExecuting();

    assertThat(isExecuting, is(false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenNegativeIntervalTimeGiven() throws Exception {
    pendingRequestsExecutor.setIntervalTime(-1);
  }

  @Test public void shouldNotExecuteWhenNullQueueGiven() throws Exception {
    RequestQueue nullRequestQueue = null;

    boolean executeResult = pendingRequestsExecutor.execute(nullRequestQueue);

    assertThat(executeResult, is(false));
  }

  @Test public void shouldNotExecuteWhenEmptyQueueGiven() throws Exception {
    RequestQueue mockRequestQueue = mock(RequestQueue.class);
    when(mockRequestQueue.isEmpty()).thenReturn(true);
    when(mockRequestQueue.hasNext()).thenReturn(false);

    pendingRequestsExecutor.execute(mockRequestQueue);
    pendingRequestsExecutor.run();

    verify(mockRequestQueue).loadToMemory();
    verify(mockRequestQueue).isEmpty();
    verify(mockRequestQueue, atMost(1)).hasNext();
    verify(mockRequestQueue, times(1)).persistToDisk();
    verifyNoMoreInteractions(mockRequestQueue);
    assertThat(pendingRequestsExecutor.isExecuting(), is(false));
  }

  @Test public void shouldSendRequestWhenExecutingNonEmptyQueue() throws Exception {
    RequestQueue mockRequestQueue = mock(RequestQueue.class);
    when(mockRequestQueue.isEmpty()).thenReturn(false);
    when(mockRequestQueue.hasNext()).thenReturn(true);
    RequestModel mockRequestModel = mock(RequestModel.class);
    when(mockRequestQueue.next()).thenReturn(mockRequestModel);

    boolean executeResult = pendingRequestsExecutor.execute(mockRequestQueue);
    pendingRequestsExecutor.run();

    verify(mockRequestQueue).next();
    verify(mockNetworkRequestManager).sendRequest(eq(mockRequestModel),
        any(NetworkResponseCallback.class));
    assertThat(executeResult, is(true));
    assertThat(pendingRequestsExecutor.isExecuting(), is(true));
  }

  @Test public void shouldProperlyHandleSuccessfulResponse() throws Exception {
    ArgumentCaptor<NetworkResponseCallback> networkResponseCallbackArgumentCaptor =
        ArgumentCaptor.forClass(NetworkResponseCallback.class);
    RequestQueue mockRequestQueue = mock(RequestQueue.class);
    doAnswer(new Answer() {
      private boolean firstTime = true;
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        if (firstTime) {
          firstTime = false;
          return false;
        }
        return true;
      }
    }).when(mockRequestQueue).isEmpty();
    when(mockRequestQueue.hasNext()).thenReturn(true);

    pendingRequestsExecutor.execute(mockRequestQueue);
    pendingRequestsExecutor.run();

    verify(threadExecutor, times(1)).execute(pendingRequestsExecutor);
    InOrder inOrder = inOrder(mockRequestQueue);
    inOrder.verify(mockRequestQueue).loadToMemory();
    inOrder.verify(mockRequestQueue).isEmpty();
    inOrder.verify(mockRequestQueue).hasNext();
    inOrder.verify(mockRequestQueue).next();
    verify(mockNetworkRequestManager).sendRequest(any(RequestModel.class),
        networkResponseCallbackArgumentCaptor.capture());
    networkResponseCallbackArgumentCaptor.getValue().onSuccess();
    inOrder.verify(mockRequestQueue).remove();
    inOrder.verify(mockRequestQueue).isEmpty();
    inOrder.verify(mockRequestQueue).persistToDisk();
    verifyNoMoreInteractions(mockRequestQueue);
  }

  @Test public void shouldAbortExecutionWhenCancelling() throws Exception {
    ArgumentCaptor<NetworkResponseCallback> networkResponseCallbackArgumentCaptor =
        ArgumentCaptor.forClass(NetworkResponseCallback.class);
    RequestQueue mockRequestQueue = mock(RequestQueue.class);
    when(mockRequestQueue.isEmpty()).thenReturn(false);
    when(mockRequestQueue.hasNext()).thenReturn(true);

    pendingRequestsExecutor.execute(mockRequestQueue);
    pendingRequestsExecutor.run();
    pendingRequestsExecutor.cancel();
    verify(mockNetworkRequestManager).sendRequest(any(RequestModel.class),
        networkResponseCallbackArgumentCaptor.capture());
    networkResponseCallbackArgumentCaptor.getValue().onSuccess();

    verify(logger).d("Aborting pending request executor.");
    boolean executing = pendingRequestsExecutor.isExecuting();
    assertThat(executing, is(false));
  }

  private RequestModel getSomeRequestModel() {
    return new RequestModel.Builder<String>().methodType(MethodType.POST)
        .baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .body("This is the body")
        .build();
  }
}