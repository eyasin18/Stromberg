package de.repictures.stromberg.Firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class FingerhutFirebaseMessagingService extends FirebaseMessagingService {

    public static String ORDERS_UPDATE_BROADCAST_ACTION = "de.repictures.stromberg.ORDERS_UPDATE";
    private static String TAG = "FFbMessagingService";
    private NotificationManager mNotificationManager;
    private String notificationChannelId = "fingerhut_notfication_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("notificationId"));

        switch (remoteMessage.getData().get("notificationId")){
            case "0":
                int notificationId = Integer.parseInt(remoteMessage.getData().get("notificationId"));
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder mBuilder;

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    mBuilder = new Notification.Builder(this, notificationChannelId);
                    setNotificationChannel();
                    mBuilder.setChannelId(notificationChannelId);
                } else {
                    mBuilder = new Notification.Builder(this);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) mBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                String contentTextRaw = getResources().getString(R.string.new_transfer_notification_body);
                String accountnumber = remoteMessage.getData().get("accountnumber");
                String message = String.format(Locale.getDefault(), contentTextRaw, accountnumber);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.new_transfer_notification_title))
                        .setStyle(new Notification.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setSound(defaultSoundUri)
                        .setContentIntent(contentIntent);
                mNotificationManager.notify(notificationId, mBuilder.build());
                break;
            case "1":
                Log.d(TAG, "onMessageReceived: Message from Company Shopping Requests Update: " + remoteMessage.getData().get("updateKey"));
                Intent broadcast = new Intent();
                String[] dataNames = {"amounts", "buyerAccountnumber", "dateTime", "isSelfBuys", "number", "prices", "productCodes"};
                for (String dataName : dataNames) {
                    broadcast.putExtra(dataName, remoteMessage.getData().get(dataName));
                }
                broadcast.setAction(ORDERS_UPDATE_BROADCAST_ACTION);
                sendBroadcast(broadcast);
                break;
            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void setNotificationChannel(){
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.notification_channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(notificationChannelId, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this product.
        mChannel.setLightColor(Color.GREEN);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);
    }
}
