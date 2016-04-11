package hu.pe.thinhhoang.aaosync.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import hu.pe.thinhhoang.aaosync.MainActivity;
import hu.pe.thinhhoang.aaosync.R;
import hu.pe.thinhhoang.aaosync.settings.Settings;

/**
 * Created by hoang on 1/10/2016.
 */
public class SyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Settings.setContext(context.getApplicationContext());

            // Start the service!
            SyncHelper.cancelAlarm(context);
            Intent startServiceIntent = new Intent(context.getApplicationContext(), SyncOrchestrator.class);
            context.getApplicationContext().startService(startServiceIntent);


        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) && Settings.getAllowSync())
        {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setSmallIcon(R.drawable.ic_filter_vintage_white_24dp);
            notificationBuilder.setContentTitle("AAOSync Service");
            notificationBuilder.setContentText("Dịch vụ đồng bộ của AAOSync đang chạy nền.");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2710, notificationBuilder.build());
        }
    }
}

