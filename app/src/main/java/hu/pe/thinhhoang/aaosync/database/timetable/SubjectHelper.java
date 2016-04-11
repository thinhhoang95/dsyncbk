package hu.pe.thinhhoang.aaosync.database.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import hu.pe.thinhhoang.aaosync.database.AAOSyncDbHelper;
import hu.pe.thinhhoang.aaosync.database.grades.Grade;

/**
 * Created by hoang on 1/31/2016.
 */
public class SubjectHelper {
    private AAOSyncDbHelper helper;

    public SubjectHelper(Context context)
    {
        helper=AAOSyncDbHelper.getInstance(context);
    }

    public void putSubject(String mTenMH, String mNhom, int mThu, int mTietTu, int mTietDen, String mPhong, String mTuan)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("TenMH",mTenMH);
        values.put("Nhom",mNhom);
        values.put("Thu",mThu);
        values.put("TietTu",mTietTu);
        values.put("TietDen",mTietDen);
        values.put("Phong",mPhong);
        values.put("Tuan",mTuan);

        db.insert("timetable", null, values);

        // db.close();
    }

    public void putSubject(Subject s)
    {
        putSubject(s.TenMH, s.Nhom, s.Thu, s.TietTu, s.TietDen, s.Phong, s.Tuan);
    }

    public void emptyTable()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM timetable");
    }

    public ArrayList<Subject> getAllSubjects()
    {
        ArrayList<Subject> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM grades",null);
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            result.add(new Subject(
                    res.getString(res.getColumnIndex("TenMH")),
                    res.getString(res.getColumnIndex("Nhom")),
                    res.getInt(res.getColumnIndex("Thu")),
                    res.getInt(res.getColumnIndex("TietTu")),
                    res.getInt(res.getColumnIndex("TietDen")),
                    res.getString(res.getColumnIndex("Phong")),
                    res.getString(res.getColumnIndex("Tuan"))
            ));
            res.moveToNext();
        }
        res.close();
        return result;
    }


    /***
     * Returns an ArrayList with subjects, given the day of the week.
     * @param Thu 2 accords Monday, 8 accords Sunday, though there will never be any subjects on Sunday
     * @return ArrayList of Subject
     */
    public ArrayList<Subject> getSubjectsByWeekDay(int Thu, Date fd)
    {
        Date now = new Date();
        long diff=now.getTime()-fd.getTime();
        long diffDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
        long diffWeeks = diffDays/7 + 1;
        Log.v("AAOSync","This is week "+diffWeeks);
        ArrayList<Subject> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM timetable WHERE Thu='"+Integer.toString(Thu)+"'", null);
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            String week = res.getString(res.getColumnIndex("Tuan"));
            if (diffWeeks<=week.length())
            {
                if(week.charAt((int)diffWeeks-1)!='_')
                {
                    result.add(new Subject(
                            res.getString(res.getColumnIndex("TenMH")),
                            res.getString(res.getColumnIndex("Nhom")),
                            res.getInt(res.getColumnIndex("Thu")),
                            res.getInt(res.getColumnIndex("TietTu")),
                            res.getInt(res.getColumnIndex("TietDen")),
                            res.getString(res.getColumnIndex("Phong")),
                            res.getString(res.getColumnIndex("Tuan"))
                    ));
                }
            }
            res.moveToNext();
        }
        res.close();
        return result;
    }
    
}
