package hu.pe.thinhhoang.aaosync.sync;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import hu.pe.thinhhoang.aaosync.database.grades.Grade;

/**
 * Created by hoang on 1/29/2016.
 */
public class QuickViewGradesSync extends GradesSync {
    public void quickSync(String mssv, Context c, AfterSyncParams afterSync, AfterSyncError afterSyncError)
    {
        setMSSV(mssv);
        sync(c,new AfterQuickSync(),afterSync,afterSyncError);
    }

    private interface PostSyncContextParams
    {
        void execute(JSONObject response, Context a, AfterSyncParams afterSync, AfterSyncError afterSyncError);
    }

    public interface AfterSyncParams
    {
        void execute(Object param);
    }

    private void sync(final Context a, final PostSyncContextParams postSync, final AfterSyncParams afterSyncHandler, final AfterSyncError afterSyncError){
        // GradesSync the grades
        if(mssv.isEmpty())
        {
            throw new RuntimeException("MSSV is not set, therefore can not sync Grade Data!");
        }
        // Now we're certain that mssv is not empty, we can proceed fetching data from the server!
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,100);
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

    private class AfterQuickSync implements PostSyncContextParams{
        @Override
        public void execute(JSONObject response, Context a, AfterSyncParams afterSync, AfterSyncError afterSyncError) {
            ArrayList<Grade> listOfGrades = new ArrayList<>();
            try
            {
                JSONArray jArray = response.optJSONArray("data");
                for (int i=0; i<jArray.length(); i++)
                {
                    JSONObject jObj = jArray.getJSONObject(i);
                    Grade g = new Grade(jObj.getString("MaMH"),jObj.getString("TenMH"),jObj.getString("Nhom"),jObj.getInt("SoTC"),jObj.getString("DiemKT"),jObj.getString("DiemThi"),jObj.getString("DiemTK"));
                    listOfGrades.add(g);
                }
                afterSync.execute(listOfGrades);
            } catch (Exception e)
            {
                afterSyncError.execute();
            }
        }
    }
}
