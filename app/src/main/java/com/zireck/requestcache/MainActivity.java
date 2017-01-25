package com.zireck.requestcache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.zireck.requestcache.library.RequestCache;
import com.zireck.requestcache.library.RequestCacheManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private RequestCache requestCache;
  private Button enqueueView;
  private Button sendView;
  private Button clearView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestCache = RequestCacheManager.getInstance();

    enqueueView = (Button) findViewById(R.id.enqueue);
    sendView = (Button) findViewById(R.id.send);
    clearView = (Button) findViewById(R.id.clear);

    enqueueView.setOnClickListener(this);
    sendView.setOnClickListener(this);
    clearView.setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    if (view == enqueueView) {
      showAlertWith("Enqueuing requests");
      requestCache.enqueueRequests();
    } else if (view == sendView) {
      showAlertWith("Sending pending requests");
      requestCache.sendPendingRequests();
    } else if (view == clearView) {
      showAlertWith("Clear request cache");
      requestCache.clearRequestsCache();
    }
  }

  private void showAlertWith(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}