package com.zireck.requestcache.library.cache;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class SharedPreferencesQueueTest {

  private SharedPreferencesQueue sharedPreferencesQueue;

  @Mock private SharedPreferences mockSharedPreferences;
  private Gson gson; // TODO mock this

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    gson = new Gson();
    sharedPreferencesQueue = new SharedPreferencesQueue(mockSharedPreferences, gson);
  }

  @Test public void shouldNotHaveNextItemWhenQueueIsEmpty() throws Exception {
    when(mockSharedPreferences.getString("PENDING_REQUEST_QUEUE", "")).thenReturn("");

    sharedPreferencesQueue.load();

    verify(mockSharedPreferences).getString("PENDING_REQUEST_QUEUE", "");
    assertThat(sharedPreferencesQueue.isEmpty(), is(true));
    assertThat(sharedPreferencesQueue.hasNext(), is(not(true)));
    assertThat(sharedPreferencesQueue.next(), nullValue());
  }

  @Test public void shouldHaveNextItemWhenQueueIsNotEmpty() throws Exception {
    ArrayList<RequestModel> requestModels = new ArrayList<>();
    requestModels.add(getSomeRequestModel());
    String pendingRequestQueueString = gson.toJson(requestModels);
    when(mockSharedPreferences.getString("PENDING_REQUEST_QUEUE", "")).thenReturn(
        pendingRequestQueueString);

    sharedPreferencesQueue.load();

    assertThat(sharedPreferencesQueue.hasNext(), is(true));
    RequestModel actualRequestModel = sharedPreferencesQueue.next();
    assertThat(actualRequestModel, notNullValue());
    assertThat(((String) actualRequestModel.getBody()), is("This is the body"));
  }

  @Test public void shouldProperlyClearThePersistedQueue() throws Exception {
    SharedPreferences.Editor mockSharedPreferencesEditor = mock(SharedPreferences.Editor.class);
    when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);
    when(mockSharedPreferencesEditor.commit()).thenReturn(true);

    boolean clearResult = sharedPreferencesQueue.clear();

    verify(mockSharedPreferences).edit();
    verify(mockSharedPreferencesEditor).putString("PENDING_REQUEST_QUEUE", "");
    verify(mockSharedPreferencesEditor).commit();
    verifyNoMoreInteractions(mockSharedPreferences, mockSharedPreferencesEditor);
    assertThat(clearResult, is(true));
  }

  @Test public void shouldProperlyPersistTheQueue() throws Exception {
    SharedPreferences.Editor mockSharedPreferencesEditor = mock(SharedPreferences.Editor.class);
    when(mockSharedPreferencesEditor.commit()).thenReturn(true);
    when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);

    ArrayList<RequestModel> requestModels = new ArrayList<>();
    requestModels.add(getSomeRequestModel());
    requestModels.add(getSomeRequestModel());
    sharedPreferencesQueue.add(requestModels);
    sharedPreferencesQueue.add(getSomeRequestModel());
    boolean persistResult = sharedPreferencesQueue.persist();

    verify(mockSharedPreferences).edit();
    requestModels.add(getSomeRequestModel());
    String requestModelListString = gson.toJson(requestModels);
    verify(mockSharedPreferencesEditor).putString("PENDING_REQUEST_QUEUE", requestModelListString);
    verify(mockSharedPreferencesEditor).commit();
    verifyNoMoreInteractions(mockSharedPreferences, mockSharedPreferencesEditor);
    assertThat(persistResult, is(true));
  }

  private RequestModel getSomeRequestModel() {
    return new RequestModel.Builder<String>().methodType(MethodType.POST)
        .baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .body("This is the body")
        .build();
  }
}