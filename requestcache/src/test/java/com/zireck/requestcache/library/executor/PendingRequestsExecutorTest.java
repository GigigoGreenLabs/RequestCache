package com.zireck.requestcache.library.executor;

import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.network.NetworkRequestManager;
import com.zireck.requestcache.library.util.MethodType;
import io.reactivex.observers.DisposableObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class) public class PendingRequestsExecutorTest {

  private PendingRequestsExecutor pendingRequestsExecutor;

  @Mock private ThreadExecutor threadExecutor;
  @Mock private NetworkRequestManager mockNetworkRequestManager;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    pendingRequestsExecutor =
        new PendingRequestsExecutor(threadExecutor, mockNetworkRequestManager);
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
    DisposableObserver mockDisposableObserver = mock(DisposableObserver.class);

    pendingRequestsExecutor.execute(nullRequestQueue, mockDisposableObserver);

    verifyZeroInteractions(mockDisposableObserver);
  }

  private RequestModel getSomeRequestModel() {
    return new RequestModel.Builder<String>().methodType(MethodType.POST)
        .baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .body("This is the body")
        .build();
  }
}