package hu.pe.thinhhoang.aaosync.sync;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import hu.pe.thinhhoang.aaosync.database.timetable.Subject;
import hu.pe.thinhhoang.aaosync.database.timetable.SubjectHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;

/**
 * Created by hoang on 1/31/2016.
 */
public class TimetableSync {
    String mssv;

    protected static final String URL_TIMETABLE="http://thinhhoang.pe.hu/whitestar/timetable.php?mssv=";

    public void sync(final Context context, final AfterSyncError afterSyncError, final AfterSyncSuccess afterSyncSuccess)
    {
        // Get mssv
        Settings.setContext(context.getApplicationContext());
        mssv = Settings.getMSSV();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,100);
        client.get(URL_TIMETABLE + mssv, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.v("AAOSync", "Error in connection to timetable service!");
                afterSyncError.execute();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    JSONObject j = new JSONObject(s);
                    SubjectHelper subjectHelper = new SubjectHelper(context.getApplicationContext());
                    Settings.setFDSemester(j.optString("semesterFD"));
                    JSONArray jArray = j.optJSONArray("data");
                    if(jArray.length()>0)
                    {
                        subjectHelper.emptyTable();
                        for (int k=0; k<jArray.length(); k++)
                        {
                            JSONObject jObj = jArray.getJSONObject(k);
                            Subject sj = new Subject(jObj.optString("TenMH"),jObj.optString("Nhom"),jObj.optInt("Thu"),jObj.optInt("TietTu"),jObj.optInt("TietDen"),jObj.optString("Phong"),jObj.optString("Tuan"));
                            subjectHelper.putSubject(sj);
                        }
                        Log.v("AAOSync","Timetable table is updated successfully!");
                        afterSyncSuccess.execute();
                    } else
                    {
                        afterSyncError.execute();
                    }
                } catch (Exception e) {
                    Log.v("AAOSync", "Error. Can not parse the data, may be bad data was received.");
                    e.printStackTrace();
                    afterSyncError.execute();
                }
            }
        });
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
