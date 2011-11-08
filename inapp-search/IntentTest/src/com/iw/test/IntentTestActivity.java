package com.iw.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IntentTestActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btn1 = (Button) this.findViewById(R.id.btn1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.SEARCH");
				intent.setClassName("com.dianping.v1",
						"com.dianping.v1.ShopListActivity");
				intent.putExtra("query", "足疗");
				startActivity(intent);
			}
		});

		Button btn2 = (Button) this.findViewById(R.id.btn2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.SEARCH");
				intent.setClassName("com.taobao.taobao",
						"com.taobao.tao.SearchListActivity");
				intent.putExtra("query", "足疗");
				startActivity(intent);
			}
		});

	}
}