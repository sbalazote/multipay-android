package com.multipay.android.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.multipay.android.R;
import com.multipay.android.activities.NotificationsActivity;
import com.multipay.android.activities.PaymentHistoryActivity;
import com.multipay.android.activities.PaymentLinkBeamActivity;
import com.multipay.android.activities.SimpleVaultActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	private static final String TAG = "MyFirebaseMsgService";

	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	// [START receive_message]
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// TODO(developer): Handle FCM messages here.
		if (remoteMessage.getData().get("type").equals("paymentLink")) {
			sendPaymentLink(remoteMessage.getData().get("title"), remoteMessage.getData());
		} else {
			sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData());
		}
		// If the application is in the foreground handle both data and notification messages here.
		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
		Log.d(TAG, "From: " + remoteMessage.getFrom());
		//Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
	}
	// [END receive_message]

	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 * @param messageBody FCM message body received.
	 */
	private void sendPaymentLink(String messageTitle, Map<String, String> messageBody) {
		Intent intent = new Intent(this, PaymentHistoryActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("sellerEmail", messageBody.get("seller_email"));
		intent.putExtra("description", messageBody.get("description"));
		intent.putExtra("transactionAmount", messageBody.get("transaction_amount"));

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_logo_multipay)
				.setContentTitle(messageTitle)
				.setContentText(messageBody.get("text"))
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setPriority(Notification.PRIORITY_HIGH)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}

	private void sendNotification(String messageTitle, Map<String, String> messageBody) {
		Intent intent = new Intent(this, NotificationsActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("text", messageBody.get("text"));

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_logo_multipay)
				.setContentTitle(messageTitle)
				.setContentText(messageBody.get("text"))
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setPriority(Notification.PRIORITY_HIGH)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}
}