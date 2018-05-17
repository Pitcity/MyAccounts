package com.example.ihortovpinets.myaccounts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static com.example.ihortovpinets.myaccounts.DealsForAccountActivity.ACCOUNT_ID;

/**
 * Created by itovp on 07.08.2017.
 */

public class TryItActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkNotification();
	}

	private void checkNotification() {
		Intent intent = new Intent(this, DealsForAccountActivity.class);
		intent.putExtra(ACCOUNT_ID, "FirstAcc");
// use System.currentTimeMillis() to have a unique ID for the pending intent
		PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

		Notification n  = new Notification.Builder(this)
				.setContentTitle("Open Deal Details")
				.setContentText("blablabla")
				.setSmallIcon(R.drawable.common_google_signin_btn_icon_light_disabled)
				.setContentIntent(pIntent)
				.setAutoCancel(true)
				.addAction(R.drawable.common_google_signin_btn_icon_light_disabled,"Call", pIntent)
				.addAction(R.drawable.common_google_signin_btn_icon_light_disabled, "More", pIntent)
				.addAction(R.drawable.common_google_signin_btn_icon_light_disabled, "And more", pIntent).build();


		NotificationManager notificationManager =
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n);
	}
}
