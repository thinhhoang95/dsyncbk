package hu.pe.thinhhoang.aaosync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.Date;

import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.sync.ExamsSync;
import hu.pe.thinhhoang.aaosync.sync.GradesSync;
import io.fabric.sdk.android.Fabric;

/**
 * Created by hoang on 1/10/2016.
 */
public class SyncOrchestrator extends Service {

    GradesSync syncer = new GradesSync();
    ExamsSync examsSync = new ExamsSync();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Fabric.with(this, new Crashlytics());

        // Things to be done here!
        Settings.setContext(this.getApplicationContext());
        Settings.setLastSync();

        Log.v("AAOSync Service", "The service has been launched on " + new Date().toString());
        if (!Settings.getMSSV().equals("undef") && Settings.getAllowSync())
        {
            Log.v("AAOSync","Trying to notify users about upcoming exams");
//            Settings.setContext(this.getApplicationContext());
            examsSync.IssueReminder(this.getApplicationContext());
        }

        if(SyncHelper.isNetworkAvailable(this.getApplicationContext()) && !Settings.getMSSV().equals("undef") && Settings.getAllowSync())
        {
            syncer = new GradesSync();
            examsSync = new ExamsSync();
//            Settings.setContext(this.getApplicationContext());
            doExamAndThenGradeSync();
        }

        serviceSchedule();

        stopSelf(); // Free up memory

        return super.onStartCommand(intent, flags, startId);
    }

    private void doExamAndThenGradeSync()
    {
        // Check for upcoming exams
        examsSync.premierSync(this.getApplicationContext(), new ExamsSync.AfterSyncError() {
            @Override
            public void execute() {
                Log.v("AAOSync", "Exams Sync failed!");
                doGradeSync(); // To prevent database lock!
            }
        }, new ExamsSync.AfterSyncSuccess() {
            @Override
            public void execute() {
                doGradeSync(); // To prevent database lock!
                Log.v("AAOSync", "Exam Sync successfully exited!");
            }
        });
    }

    private void doGradeSync()
    {
        // The app is configured correctly, sync now you may
        syncer.compareSync(this, new GradesSync.AfterSync() {
            @Override
            public void execute() {
                Log.v("AAOSync", "A sync was successfully performed");
            }
        }, new GradesSync.AfterSyncError() {
            @Override
            public void execute() {
                // Do nothing
                Log.v("AAOSync","An error occurred while syncing!");
            }
        });
    }

    private void serviceSchedule()
    {
        // Reschedule the alarm after 30 minutes
        Settings.setContext(this.getApplicationContext());
        if(Settings.getAllowSync() && !Settings.getMSSV().equals("undef")) {
            SyncHelper.cancelAlarm(this);
            SyncHelper.scheduleAlarm(this);
        }
        Log.v("AAOSync Service", "Service is about to end. See you later... Goodbye!");
    }

    @Override
    public void onDestroy() {
//        Log.v("AAOSync Service", "The service is about to terminate...");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
