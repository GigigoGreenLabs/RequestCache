package com.zireck.requestcache.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.zireck.requestcache.R;
import com.zireck.requestcache.RequestCacheApplication;
import com.zireck.requestcache.library.RequestCache;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.MethodType;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  @Inject RequestCache requestCache;
  private Button enqueueView;
  private Button sendView;
  private Button cancelView;
  private Button clearView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ((RequestCacheApplication) getApplication()).getComponent().inject(this);

    initUi();
  }

  private void initUi() {
    enqueueView = (Button) findViewById(R.id.enqueue);
    sendView = (Button) findViewById(R.id.send);
    cancelView = (Button) findViewById(R.id.cancel);
    clearView = (Button) findViewById(R.id.clear);

    enqueueView.setOnClickListener(this);
    sendView.setOnClickListener(this);
    cancelView.setOnClickListener(this);
    clearView.setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    if (view == enqueueView) {
      showAlertWith("Enqueuing requests");
      requestCache.enqueueRequests(getRequests());
    } else if (view == sendView) {
      showAlertWith("Sending pending requests");
      requestCache.sendPendingRequests();
    } else if (view == cancelView) {
      requestCache.cancel();
    } else if (view == clearView) {
      showAlertWith("Clear request cache");
      requestCache.clearRequestsCache();
    }
  }

  private void showAlertWith(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private List<RequestModel> getRequests() {
    List<RequestModel> requestModels = new ArrayList<>();

    for (int i=0; i<5; i++) {
      requestModels.add(getRequest());
    }

    return requestModels;
  }

  private RequestModel getRequest() {
    return new RequestModel.Builder<>()
        .methodType(MethodType.GET)
        .baseUrl("https://api.github.com/")
        .endpoint("users/GigigoGreenLabs/repos")
        .build();
  }
}