package com.zireck.requestcache.activity;

import android.widget.Button;
import android.widget.Toast;
import com.zireck.requestcache.BuildConfig;
import com.zireck.requestcache.R;
import com.zireck.requestcache.RobolectricTestApp;
import com.zireck.requestcache.di.MockApplicationModule;
import com.zireck.requestcache.library.RequestCache;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = RobolectricTestApp.class)
public class MainActivityTest {

  private Button enqueueView;
  private Button sendView;
  private Button clearView;

  @Mock private RequestCache mockRequestCache;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    RobolectricTestApp robolectricTestApp = (RobolectricTestApp) RuntimeEnvironment.application;
    MockApplicationModule mockApplicationModule =
        new MockApplicationModule(robolectricTestApp, mockRequestCache);
    robolectricTestApp.setApplicationModule(mockApplicationModule);

    MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);

    enqueueView = (Button) mainActivity.findViewById(R.id.enqueue);
    sendView = (Button) mainActivity.findViewById(R.id.send);
    clearView = (Button) mainActivity.findViewById(R.id.clear);
  }

  @Test public void shouldProperlyEnqueueRequestWhenTheCorrespondingButtonIsClicked()
      throws Exception {
    enqueueView.performClick();

    verify(mockRequestCache, times(1)).enqueueRequests(any(List.class));
    verifyNoMoreInteractions(mockRequestCache);
    Toast latestToast = ShadowToast.getLatestToast();
    assertThat(latestToast, notNullValue());
    assertThat(latestToast.getDuration(), is(Toast.LENGTH_SHORT));
    String textOfLatestToast = ShadowToast.getTextOfLatestToast();
    assertThat(textOfLatestToast, is("Enqueuing requests"));
  }

  @Test public void shouldProperlySendPendingRequestsWhenTheCorrespondingButtonIsClicked()
      throws Exception {
    sendView.performClick();

    verify(mockRequestCache).sendPendingRequests();
    verifyNoMoreInteractions(mockRequestCache);
    String textOfLatestToast = ShadowToast.getTextOfLatestToast();
    assertThat(textOfLatestToast, notNullValue());
    assertThat(textOfLatestToast, is("Sending pending requests"));
  }

  @Test public void shouldShowDisplayToastWhenClearCacheButtonClicked() throws Exception {
    clearView.performClick();

    verify(mockRequestCache, times(1)).clearRequestsCache();
    verifyNoMoreInteractions(mockRequestCache);
    Toast latestToast = ShadowToast.getLatestToast();
    String textOfLatestToast = ShadowToast.getTextOfLatestToast();
    assertThat(latestToast, notNullValue());
    assertThat(textOfLatestToast, is("Clear request cache"));
  }
}