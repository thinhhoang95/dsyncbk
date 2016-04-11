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
import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.database.grades.GradesHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.utils.ThreadNotifierInterface;

/**
 * GradesSync the data, then put it in the local database
 * Created by hoang on 1/7/2016.
 * How to use: call public methods
 */
public class GradesSync {
    protected static final String GRADES_QUERY_URL_BASE = "http://thinhhoang.pe.hu/whitestar/grades_new.php?mssv=";
    // private static final String GRADES_QUERY_URL_BASE_DEV = "http://10.0.3.2/whitestar/grades.php?mssv=";

    protected String mssv = "";

    protected void setMSSV(String msv){
        mssv=msv;
    }

    public void premierSync (Context a, AfterSync afterSyncHandler, AfterSyncError afterSyncError)
    {
        Settings.setContext(a.getApplicationContext());
        setMSSV(Settings.getMSSV());
        sync(a, new SaveGradesHighPerformance(), afterSyncHandler, afterSyncError);
    }

    public void compareSync(Context context, AfterSync afterSync, AfterSyncError afterSyncError)
    {
        Settings.setContext(context.getApplicationContext());
        setMSSV(Settings.getMSSV());
        sync(context, new CompareGrades(), afterSync, afterSyncError);
    }

    protected void sync(final Context a, final PostSyncContext postSync, final AfterSync afterSyncHandler, final AfterSyncError afterSyncError){
        // GradesSync the grades
        if(mssv.isEmpty())
        {
            throw new RuntimeException("MSSV is not set, therefore can not sync Grade Data!");
        }
        // Now we're certain that mssv is not empty, we can proceed fetching data from the server!
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,100);
        // client.setTimeout(7000);
        /*client.get(GRADES_QUERY_URL_BASE_DEV + mssv, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // We have a proper JSON object returned from the server.
                postSync.execute(response, a, afterSyncHandler, afterSyncError);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v("AAOSync", "Connection aborted. Error.");
                afterSyncError.execute();
            }

        });*/
        client.get(GRADES_QUERY_URL_BASE + mssv, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.v("AAOSync", "Connection aborted. Error.");
                afterSyncError.execute();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try
                {
                    JSONObject j = new JSONObject(s);
                    postSync.execute(j,a, afterSyncHandler, afterSyncError);
                } catch (Exception e)
                {
                    Log.v("AAOSync", "Connection aborted. Error.");
                    afterSyncError.execute();
                }
            }
        });
    }

    // ACTIONS TO DO AFTER WE HAD DATA FROM SERVER

    private class SaveGrades implements PostSyncContext // A work-around since Java doesn't have delegates, this literally save the grades to the Database!
    {                                                    // Implements a POSTSYNCACTIVITY INTERFACE
        @Override
        public void execute(JSONObject response, Context a, AfterSync afterSync, AfterSyncError afterSyncError)
        {
            final GradesHelper gradeMachine = new GradesHelper(a.getApplicationContext());
            Settings.setContext(a.getApplicationContext());
            int newCount=response.optInt("count", 0);
            if (newCount>=Settings.getGradeCount())
            {
                try
                {
                    JSONArray jArray = response.optJSONArray("data");
                    if(jArray.length()>0)
                    {
                        Log.v("AAOSync","Saving student meta-data like total credits, average score...");
                        saveStudentMetaData(a, response.optString("totalCredits", "0"), response.optString("averageScore", "0.00"));
                        // Set the grade count in preferences
                        Log.v("AAOSync", "Total records: " + response.getString("count") + ". Now putting in the database...");
                        Settings.setGradeCount(response.getInt("count"));
                        gradeMachine.emptyTable();
                        Log.v("AAOSync", "Old grades are deleted");
                        for (int i=0; i<jArray.length(); i++)
                        {
                            JSONObject jObj = jArray.getJSONObject(i);
                            gradeMachine.putGrade(jObj.getString("MaMH"),jObj.getString("TenMH"),jObj.getString("Nhom"),jObj.getInt("SoTC"),jObj.getString("DiemKT"),jObj.getString("DiemThi"),jObj.getString("DiemTK"));
                        }
                        Log.v("AAOSync", "All grades are put in the database!");
                        afterSync.execute(); // Post sync!
                    } else {
                        afterSyncError.execute();
                    }

                    // gradeMachine.dumpGrades(); // TODO: PLEASE REMOVE IN PRODUCTION
                } catch (Exception e)
                {
                    Log.v("AAOSync","An error occurred while processing the response from the server! This might indicate that bad data has been received, or the server isnt ready");
                    e.printStackTrace();
                    // TODO: Call an external handler to handle the error
                    afterSyncError.execute();
                }
            } else {
                afterSyncError.execute();
            }
        }
    }

    // This action will be carried out on GradesFragment. It will save the grades regardless of "count"
    private class SaveGradesHighPerformance implements PostSyncContext // A work-around since Java doesn't have delegates, this literally save the grades to the Database!
    {                                                    // Implements a POSTSYNCACTIVITY INTERFACE
        @Override
        public void execute(JSONObject response, Context a, final AfterSync afterSync, final AfterSyncError afterSyncError) {
            final GradesHelper gradeMachine = new GradesHelper(a.getApplicationContext());
            Settings.setContext(a.getApplicationContext());
            int newCount = response.optInt("count", 0);
            try {
                // Set the grade count in preferences
                // Loop through the entire collection of grade items and sequentially put them in the database
                final JSONArray jArray = response.optJSONArray("data");

                if (jArray.length() > 0) {
                    gradeMachine.emptyTable();
                    Log.v("AAOSync", "Old grades are deleted");
                    Settings.setGradeCount(response.getInt("count"));
                    Log.v("AAOSync", "Saving student meta-data like total credits, average score...");
                    saveStudentMetaData(a, response.optString("totalCredits", "0"), response.optString("averageScore", "0.00"));

                    final ThreadNotifierInterface listener = new ThreadNotifierInterface() {
                        @Override
                        public void callback() {
                            afterSync.execute();
                        }
                    };
                    Log.v("AAOSync", "Total records: " + response.getString("count") + ". Now putting in the database...");
                    // Setup a "thread" that executes putting grades in the background!
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject jObj = jArray.getJSONObject(i);
                                    gradeMachine.putGrade(jObj.getString("MaMH"), jObj.getString("TenMH"), jObj.getString("Nhom"), jObj.getInt("SoTC"), jObj.getString("DiemKT"), jObj.getString("DiemThi"), jObj.getString("DiemTK"));
                                }
                                Log.v("AAOSync", "All grades are put in the database!");
                                listener.callback();
                            } catch (Exception e) {
                                Log.v("AAOSync", "An error occurred while adding grades to database... (from parallel thread)");
                                e.printStackTrace();
                                afterSyncError.execute();
                            }
                        }
                    };

                    // And execute the thread!
                    new Thread(runnable).start();
                }
                // gradeMachine.dumpGrades(); // TODO: PLEASE REMOVE IN PRODUCTION
            } catch (Exception e) {
                Log.v("AAOSync", "An error occurred while processing the response from the server! This might indicate that bad data has been received, or the server isnt ready");
                e.printStackTrace();
                // TODO: Call an external handler to handle the error
                afterSyncError.execute();
            }
        }
    }

    private class CompareGrades implements PostSyncContext // IMPORTANT: Consider calling this action via AsyncTask, as it might be resource-intensive
    {
        @Override
        public void execute(JSONObject response, Context a, AfterSync afterSync, AfterSyncError afterSyncError) {
            Settings.setContext(a.getApplicationContext());
            try {
                // First, compare the meta-data (total credits, average score...) and issue notification if there are changes
                checkStudentMetaData(a.getApplicationContext(),response.optString("totalCredits","0"),response.optString("averageScore","0.00"));
                // Get desired data from the response
                int newCount = response.getInt("count");
                JSONArray newJSONGradesArray = response.getJSONArray("data");
                // Convert newJSONGradesArray into ArrayList of grades
                ArrayList<Grade> newGradesArray = new ArrayList<>();
                int newJSONGradesArrayCount = newJSONGradesArray.length();
                for (int i=0; i<newJSONGradesArrayCount; i++)
                {
                    JSONObject jObj = newJSONGradesArray.getJSONObject(i);
                    newGradesArray.add(new Grade(jObj.getString("MaMH"),jObj.getString("TenMH"),jObj.getString("Nhom"),jObj.getInt("SoTC"),jObj.getString("DiemKT"),jObj.getString("DiemThi"),jObj.getString("DiemTK")));
                }
                // Get the same info from the local database
                GradesHelper gradeMachine = new GradesHelper(a.getApplicationContext());
                int count=Settings.getGradeCount();
                ArrayList<Grade> gradesArray = gradeMachine.getAllGrades();
                // Proceed to comparison
                if (count == newCount) // Eligible for comparison
                {
                    // Starts interating to find out what's the change!
                    String message="";
                    for (int i=0; i<newCount; i++)
                    {
                        boolean changed=false;
                        Grade ng = newGradesArray.get(i);
                        Grade g=gradesArray.get(i);
                        if(!ng.DiemKT.equals(g.DiemKT))
                        {
                            changed=true;
                        }
                        if(!changed && !ng.DiemThi.equals(g.DiemThi))
                        {
                            changed=true;
                        }
                        if(!changed && !ng.DiemTK.equals(g.DiemTK))
                        {
                            changed=true;
                        }
                        if(changed)
                        {
                            message+=g.TenMH+", ";
                        }
                    }
                    if (!message.isEmpty())
                    {
                        SaveGrades gradeSaver = new SaveGrades();
                        gradeSaver.execute(response, a, afterSync, afterSyncError); // Will execute afterSync right here!
                        if(message.endsWith(", "))
                        {
                            message=message.substring(0,message.length()-2);
                            message=message+".";
                        }
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(a);
                        notificationBuilder.setSmallIcon(R.drawable.ic_filter_vintage_white_24dp);
                        notificationBuilder.setContentTitle("Cập nhật điểm");
                        notificationBuilder.setContentText("Thay đổi điểm các môn " + message);
                        Intent intent = new Intent(a, MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(a);
                        stackBuilder.addNextIntent(intent);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(pendingIntent);
                        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                        NotificationManager notificationManager = (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(2709, notificationBuilder.build());
                    }
                } else if (newCount > count)
                {
                    SaveGrades gradeSaver = new SaveGrades();
                    gradeSaver.execute(response, a, afterSync, afterSyncError);
                }

                Log.v("AAOSync","CompareSync is preparing to exit... No errors.");
            }
            catch (Exception e)
            {
                Log.v("AAOSync","An error occurred trying to issue notification/comparison.");
                e.printStackTrace();
                afterSyncError.execute();
            }
        }
    }

    // Save meta data, like total credits or average score
    private void saveStudentMetaData(Context c, String mTotalCredits, String mAverageScore)
    {
        Settings.setContext(c.getApplicationContext());
        Settings.setTotalCredits(mTotalCredits);
        Settings.setAverageScore(mAverageScore);
    }

    private void checkStudentMetaData(Context c, String nTotalCredits, String nAverageScore)
    {
        Settings.setContext(c.getApplicationContext());
        String TotalCredits = Settings.getTotalCredits();
        String AverageScore = Settings.getAverageScore();
        if(!nTotalCredits.equals(TotalCredits) || !nAverageScore.equals(AverageScore))
        {
            String message="Điểm TB mới: "+nAverageScore+", Số TC mới: "+nTotalCredits+".";

            saveStudentMetaData(c, nTotalCredits, nAverageScore);

            // Build a notification and send to system!
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c);
            notificationBuilder.setSmallIcon(R.drawable.ic_filter_vintage_white_24dp);
            notificationBuilder.setContentTitle("AAOSync");
            notificationBuilder.setContentText(message);
            Intent intent = new Intent(c, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2715, notificationBuilder.build());
        }
    }

    // Interfaces

    protected interface PostSyncContext
    {
        void execute(JSONObject response, Context a, AfterSync afterSync, AfterSyncError afterSyncError);
    }

    public interface AfterSync{ // User-defined action to call after everything is settled
        void execute();
    }

    public interface AfterSyncError{ // // User-defined action to call after everything is settled - but with error - to display error message, etc...
        void execute();
    }
}
