package hu.pe.thinhhoang.aaosync.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import hu.pe.thinhhoang.aaosync.settings.Settings;

/**
 * Created by hoang on 1/14/2016.
 */
public class SyncHelper {
    public static void cancelAlarm(Context context)
    {
        AlarmManager alarm = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = PendingIntent.getService(context.getApplicationContext(), 0, new Intent(context.getApplicationContext(), SyncOrchestrator.class), 0);
        p.cancel();
        alarm.cancel(p);
    }
    public static void scheduleAlarm(Context context)
    {
        // Only reschedule alarm if allow sync

        Log.v("AAOSync Service", "Sync service rescheduled");

        Settings.setContext(context.getApplicationContext());
        if(Settings.getAllowSync())
        {
           // Schedule the alarm
            AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            int syncDuration = Settings.getSyncDuration() * 60;
            // syncDuration=30; /* DEVELOPMENT PROFILE, PLEASE REMOVE IN PRODUCTION */
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(1000*syncDuration), PendingIntent.getService(context.getApplicationContext(), 0, new Intent(context.getApplicationContext(), SyncOrchestrator.class),0));
        }
    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
