package hu.pe.thinhhoang.aaosync.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import hu.pe.thinhhoang.aaosync.MainActivity;
import hu.pe.thinhhoang.aaosync.R;
import hu.pe.thinhhoang.aaosync.database.exams.Exam;
import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.database.timetable.Subject;
import hu.pe.thinhhoang.aaosync.database.timetable.SubjectHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;

/**
 * Created by hoang on 2/6/2016.
 */
public class ExamsSync {
    private String mssv;
    protected static final String URL_EXAMS="http://thinhhoang.pe.hu/whitestar/exams.php?mssv=";

    public void premierSync(final Context context, final AfterSyncError afterSyncError, final AfterSyncSuccess afterSyncSuccess)
    {
        sync(context, new Save(), afterSyncError, afterSyncSuccess);
    }

    protected void sync(final Context context, final PostSyncContext postSyncContext, final AfterSyncError afterSyncError, final AfterSyncSuccess afterSyncSuccess)
    {
        // Get mssv
        Settings.setContext(context.getApplicationContext());
        mssv = Settings.getMSSV();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,100);
        client.get(URL_EXAMS + mssv, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.v("AAOSync", "Error in connection to timetable service!");
                afterSyncError.execute();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    JSONArray j = new JSONArray(s);
                    postSyncContext.execute(j, afterSyncSuccess, afterSyncError, context);
                } catch (Exception e) {
                    Log.v("AAOSync", "Error. Can not parse the data, may be bad data was received.");
                    e.printStackTrace();
                    afterSyncError.execute();
                }
            }
        });
    }

    protected class Save implements PostSyncContext
    {
        @Override
        public void execute(JSONArray response, AfterSyncSuccess afterSyncSuccess, AfterSyncError afterSyncError, Context c) {
            try
            {
                if(response.length()>0)
                {
                    ExamsHelper examsHelper = new ExamsHelper(c.getApplicationContext());
                    examsHelper.emptyTable();
                    Log.v("AAOSync","All exams are deleted");
                    for (int k=0; k<response.length(); k++)
                    {
                        JSONObject jObj = response.getJSONObject(k);
                        examsHelper.putExam(
                                jObj.getString("MaMH"),
                                jObj.getString("TenMH"),
                                jObj.getString("Loai"),
                                jObj.getString("Nhom"),
                                jObj.getInt("Official"),
                                jObj.getString("Ngay"),
                                jObj.getString("Gio"),
                                jObj.getString("Phong")
                        );
                    }
                    Log.v("AAOSync","Exams table updated successfully! All "+ response.length()+" exam information is saved");
                    afterSyncSuccess.execute();
                } else {
                    Log.v("AAOSync", "No information available from Whitestar!");
                    afterSyncError.execute();
                }

            } catch (Exception e)
            {
                Log.v("AAOSync", "Can not parse the data when saving exams.");
                e.printStackTrace();
                afterSyncError.execute();
            }
        }
    }

    public void IssueReminder(Context context)
    {
        ExamsHelper examsHelper = new ExamsHelper(context);
        Settings.setContext(context.getApplicationContext());
        ArrayList<Exam> listOfExams = examsHelper.examsToNotify(!Settings.getShowContributedExams(), Settings.getNotifyMeBefore());
        int NOTIFICATION_INT_BASE = 2720;
        // TODO: Show notification and put in the notify table
        for (Exam e: listOfExams) {
            NOTIFICATION_INT_BASE++;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setSmallIcon(R.drawable.ic_filter_vintage_white_24dp);
            notificationBuilder.setContentTitle("Ngày thi đang đến gần");
            notificationBuilder.setContentText("Lúc "+e.getSimplifedDateReading()+": "+ e.getTenMH()+ ".");
            Intent intent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_INT_BASE, notificationBuilder.build());
            // Update the notification table, to make sure this notification is shown only once!
            examsHelper.putNotification(e.getMaMH(),e.getLoaiString(),e.getNhom(),e.getNgay(),e.getGio(),e.getPhong());
        }
    }

    public interface PostSyncContext
    {
        void execute(JSONArray response, AfterSyncSuccess afterSyncSuccess, AfterSyncError afterSyncError, Context c);
    }

    public interface AfterSyncError
    {
        void execute();
    }

    public interface AfterSyncSuccess
    {
        void execute();
    }
}
