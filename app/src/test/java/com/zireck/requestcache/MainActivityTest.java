package com.zireck.requestcache;

import android.widget.Button;
import android.widget.Toast;
import com.zireck.requestcache.activity.MainActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class MainActivityTest {

  private Button enqueueView;
  private Button sendView;
  private Button clearView;

  @Before public void setUp() throws Exception {
    MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);

    enqueueView = (Button) mainActivity.findViewById(R.id.enqueue);
    sendView = (Button) mainActivity.findViewById(R.id.send);
    clearView = (Button) mainActivity.findViewById(R.id.clear);
  }

  @Test public void shouldShowDisplayToastWhenClearCacheButtonClicked() throws Exception {
    clearView.performClick();

    Toast latestToast = ShadowToast.getLatestToast();
    String textOfLatestToast = ShadowToast.getTextOfLatestToast();
    assertThat(latestToast, notNullValue());
    assertThat(textOfLatestToast, is("Clear request cache"));
  }
}