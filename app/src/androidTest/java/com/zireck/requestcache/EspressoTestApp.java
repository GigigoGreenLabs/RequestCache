package com.zireck.requestcache;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.zireck.requestcache.activity.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class) @LargeTest public class EspressoTestApp {

  @Rule public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class);

  @Test public void shouldDisplayToastWhenEnqueueButtonClicked() throws Exception {
    onView(withText("Enqueue Requests")).perform(click());

    onView(withText("Enqueuing requests")).inRoot(withDecorView(
        not(is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
        .check(matches(isDisplayed()));
  }

  @Test public void shouldDisplayToastWhenSendButtonClicked() throws Exception {
    onView(withId(R.id.send)).perform(click());

    onView(withText("Sending pending requests")).inRoot(withDecorView(
        not(is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
        .check(matches(isDisplayed()));
  }

  @Test public void shouldDisplayToastWhenClearButtonClicked() throws Exception {
    onView(withId(R.id.clear)).perform(click());

    onView(withText("Clear request cache")).inRoot(withDecorView(
        not(is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
        .check(matches(isDisplayed()));
  }
}
