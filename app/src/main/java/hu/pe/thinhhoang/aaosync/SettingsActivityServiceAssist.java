package hu.pe.thinhhoang.aaosync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import hu.pe.thinhhoang.aaosync.service.SyncHelper;
import hu.pe.thinhhoang.aaosync.service.SyncOrchestrator;
import hu.pe.thinhhoang.aaosync.settings.Settings;

/**
 * Created by hoang on 1/10/2016.
 */
public class SettingsActivityServiceAssist {
    public void serviceOperationStateListener(Preference preference, final Context context)
    {
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean v = (Boolean) newValue;
                if (v) {
                    // Let's start the service!
                    SyncHelper.cancelAlarm(context);
                    Settings.setContext(context);
                    Settings.setAllowSync(true);
                    context.startService(new Intent(context.getApplicationContext(), SyncOrchestrator.class));
                } else {
                    SyncHelper.cancelAlarm(context);
                    Settings.setContext(context);
                    Settings.setAllowSync(false);
                }
                return true;
            }
        });
    }
}
