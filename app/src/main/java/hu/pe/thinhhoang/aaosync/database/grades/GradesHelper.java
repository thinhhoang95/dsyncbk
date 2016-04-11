package hu.pe.thinhhoang.aaosync.database.grades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import hu.pe.thinhhoang.aaosync.database.AAOSyncDbHelper;

/**
 * Created by hoang on 1/7/2016.
 */
public class GradesHelper {
    AAOSyncDbHelper helper;

    public GradesHelper(Context context)
    {
        helper = AAOSyncDbHelper.getInstance(context);
    }

    public void putGrade(String MaMH, String TenMH, String Nhom, int SoTC, String DiemKT, String DiemThi, String DiemTK)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("MaMH",MaMH);
        values.put("TenMH",TenMH);
        values.put("Nhom",Nhom);
        values.put("SoTC",SoTC);
        values.put("DiemKT",DiemKT);
        values.put("DiemThi",DiemThi);
        values.put("DiemTK",DiemTK);

        db.insert("grades",null,values);
        // db.close();
    }

    public void emptyTable()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM grades");
        db.close();
    }

    // FOR DEVELOPMENT PURPOSE ONLY, MAKE SURE REMOVED IN PRODUCTION
    public void dumpGrades()
    {
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM grades", null);
        if(cursor.moveToFirst())
        {
            do {
                Log.d("AAOSync",cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public ArrayList<Grade> getAllGrades()
    {
        ArrayList<Grade> result = new ArrayList<Grade>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM grades",null);
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            result.add(new Grade(res.getString(res.getColumnIndex("MaMH")),res.getString(res.getColumnIndex("TenMH")),res.getString(res.getColumnIndex("Nhom")),res.getInt(res.getColumnIndex("SoTC")),res.getString(res.getColumnIndex("DiemKT")),res.getString(res.getColumnIndex("DiemThi")),res.getString(res.getColumnIndex("DiemTK"))));
            res.moveToNext();
        }
        res.close();
        db.close();
        return result;
    }

    public ArrayList<Grade> findGrade(String keyword)
    {
        ArrayList<Grade> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.query("grades", new String[]{"MaMH","TenMH","Nhom","SoTC","DiemKT","DiemThi","DiemTK"},"TenMH LIKE ?", new String[]{"%"+keyword+"%"}, null, null, null);
        Log.v("AAOSync DBTask","Found: "+res.getCount()+" records");
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            result.add(new Grade(res.getString(res.getColumnIndex("MaMH")),res.getString(res.getColumnIndex("TenMH")),res.getString(res.getColumnIndex("Nhom")),res.getInt(res.getColumnIndex("SoTC")),res.getString(res.getColumnIndex("DiemKT")),res.getString(res.getColumnIndex("DiemThi")),res.getString(res.getColumnIndex("DiemTK"))));
            res.moveToNext();
        }
        res.close();
        db.close();
        return result;
    }

    // TODO: override getAllGrades but with pagination - NO PLEASE
}
