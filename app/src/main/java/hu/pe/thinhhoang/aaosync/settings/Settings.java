package hu.pe.thinhhoang.aaosync.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hoang on 1/7/2016.
 * This is a STATIC class
 * How to use: first call setContext to set to current Application Context!!! getApplicationContext()
 * Then proceed on setMSSV, getMSSV...
 */
public class Settings {

    public static final String[] countries = {"", "Ashburn, Mỹ", "Stavanger, Na Uy", "Moscow, Nga", "Paris, Pháp", "Odense, Đan Mạch"};

    private static SharedPreferences sharedPreferences;
    public static String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void setContext(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static void setMSSV(String mssv){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MSSV",mssv);
        editor.apply();
    }

    public static String getMSSV(){
        return sharedPreferences.getString("MSSV","undef");
    }

    public static void setGradeCount(int count){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("gradeCount",count);
        editor.apply();
    }

    public static int getGradeCount()
    {
        return sharedPreferences.getInt("gradeCount",0);
    }

    public static void setLastSync()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // PLEASE EDIT THIS FOR PRODUCTION RELEASE!
        editor.putString("lastSync", new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.FRANCE).format(new Date()));
//        editor.putString("lastSyncLog", getLastSync() + ", " + new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.FRANCE).format(new Date()));
        editor.apply();
    }

    public static String getLastSync()
    {
        return sharedPreferences.getString("lastSync","N/A");
    }

    public static void dumpLastSync()
    {
        Log.v("AAOSync-L", sharedPreferences.getString("lastSyncLog","N/A"));
    }

    public static boolean getAllowSync()
    {
        return sharedPreferences.getBoolean("enable_service", true);
    }

    public static void setAllowSync(Boolean v)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("enable_service", v);
        editor.apply();
    }

    public static int getSyncDuration()
    {
        // Log.v("AAOSync", "Returning " + sharedPreferences.getString("sync_duration", "30"));
        return Integer.parseInt(sharedPreferences.getString("sync_duration", "30"));
    }

    public static void setSyncAtStartup(boolean value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sync_at_startup", value);
        editor.apply();
    }

    public static boolean getSyncAtStartup()
    {
        return sharedPreferences.getBoolean("sync_at_startup", true);
    }

    public static void setTotalCredits(String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("total_credits", value);
        editor.apply();
    }

    public static String getTotalCredits()
    {
        return sharedPreferences.getString("total_credits", "0");
    }

    public static void setAverageScore(String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("average_score", value);
        editor.apply();
    }

    public static String getAverageScore()
    {
        return sharedPreferences.getString("average_score","0.00");
    }

    public static void setFDSemester(String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FD_semester", value);
        editor.apply();
    }

    public static Date getFDSemester()
    {
        String fdString = sharedPreferences.getString("FD_semester", "1980-01-01");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
        Date d;
        try
        {
            d = format.parse(fdString);
        } catch (Exception e)
        {
            d=null;
        }
        return d;
    }

    public static void setTimeTableSynced()
    {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.FRANCE);
        String value=dt1.format(d);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("timetable_synced", value);
        editor.apply();
    }

    public static void resetTimeTableSynced()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("timetable_synced", "1980-01-01 00:00:00");
        editor.apply();
    }

    public static boolean getTimeTableSynced()
    {
        String dateString = sharedPreferences.getString("timetable_synced", "1980-01-01 00:00:00");
        Log.v("AAOSync", "Last TimeTableSync: " + dateString);
        SimpleDateFormat dt1 = new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.FRANCE);
        try {
            Date dLastSync = dt1.parse(dateString);
            Date dNow = new Date();
            long diffTime = dNow.getTime()-dLastSync.getTime();
            if(diffTime>3*24*60*60*1000)
            {
                // Older than 3 days, refresh please!
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            // Never happens anyway!
            return true;
        }
    }

    public static void setExamsSynced()
    {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.FRANCE);
        String value=dt1.format(d);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("exams_synced", value);
        editor.apply();
    }

    public static void setExamsSyncedInvalidate()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("exams_synced", "1980-01-01 00:00:00");
        editor.apply();
    }

    public static boolean getExamsSynced()
    {
        String dateString = sharedPreferences.getString("exams_synced", "1980-01-01 00:00:00");
        Log.v("AAOSync", "Last TimeTableSync: " + dateString);
        SimpleDateFormat dt1 = new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.FRANCE);
        try {
            Date dLastSync = dt1.parse(dateString);
            Date dNow = new Date();
            long diffTime = dNow.getTime()-dLastSync.getTime();
            if(diffTime>3*24*60*60*1000)
            {
                // Older than 3 days, refresh please!
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            // Never happens anyway!
            return true;
        }
    }

    public static void setFacility(String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("facility", value);
        editor.apply();
    }

    public static String getFacility()
    {
        return sharedPreferences.getString("facility","CS1");
    }

    public static void setShowContributedExams(boolean v)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("show_contributed_exams", v);
        editor.apply();
    }

    public static boolean getShowContributedExams()
    {
        return sharedPreferences.getBoolean("show_contributed_exams", true);
    }

    public static int getNotifyMeBefore()
    {
        return Integer.parseInt(sharedPreferences.getString("notify_me_before", "3"));
    }

    public static boolean getShowUpvotedExamByDefault()
    {
        return sharedPreferences.getBoolean("display_upvoted_exam", false);
    }
    public static boolean getShowCreatedExamByDefault()
    {
        return sharedPreferences.getBoolean("display_created_exam", true);
    }

    public static int getWhitestarLocation()
    {
        return sharedPreferences.getInt("whitestar_location", 0);
    }

    public static void setWhitestarLocation(int i)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("whitestar_location", i);
        editor.apply();
    }
}
