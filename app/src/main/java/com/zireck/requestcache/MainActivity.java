package com.zireck.requestcache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private Button enqueueView;
  private Button sendView;
  private Button clearView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
    } else if (view == sendView) {
      showAlertWith("Sending pending requests");
    } else if (view == clearView) {
      showAlertWith("Clear request cache");
    }
  }

  private void showAlertWith(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}