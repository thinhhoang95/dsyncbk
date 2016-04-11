package hu.pe.thinhhoang.aaosync.database.exams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import hu.pe.thinhhoang.aaosync.database.AAOSyncDbHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;

/**
 * Created by hoang on 2/6/2016.
 */
public class ExamsHelper {

    public enum OFFICIAL_STATE_FLAG { ALL, OFFICIAL_ONLY }; // Whether to pick up "official exams" or not!

    AAOSyncDbHelper helper;

    public ExamsHelper(Context context)
    {
        helper = AAOSyncDbHelper.getInstance(context);
    }

    public void putExam(String MaMH, String TenMH, String Loai, String Nhom, int Official, String Ngay, String Gio, String Phong)
    {
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("MaMH",MaMH);
        values.put("TenMH",TenMH);
        values.put("Loai",Loai);
        values.put("Nhom",Nhom);
        values.put("Official",Official);
        values.put("Ngay",Ngay);
        values.put("Gio",Gio);
        values.put("Phong",Phong);
        // values.put("Notified",0);

        db.insert("exams",null,values);
        // db.close();
    }

    public void putExam(Exam e)
    {
        putExam(e.getMaMH(),e.getTenMH(),e.getLoaiString(),e.getNhom(), !e.getOfficial()?0:1, e.getNgay(), e.getGio(), e.getPhong());
    }

    public void emptyTable()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM exams");
        db.close();
    }

    private ArrayList<Exam> getAllExam(OFFICIAL_STATE_FLAG osf)
    {
        ArrayList<Exam> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res;
        if (osf.equals(OFFICIAL_STATE_FLAG.ALL))
        {
            res = db.rawQuery("SELECT * FROM exams",null);
        } else {
            res = db.rawQuery("SELECT * FROM exams WHERE Official='1'",null);
        }
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            Exam e = new Exam();
            e.setMaMH(res.getString(res.getColumnIndex("MaMH")));
            e.setTenMH(res.getString(res.getColumnIndex("TenMH")));
            e.setLoai(res.getString(res.getColumnIndex("Loai")));
            e.setNhom(res.getString(res.getColumnIndex("Nhom")));
            e.setOfficial(!res.getString(res.getColumnIndex("Official")).equals("0"));
            e.setNgay(res.getString(res.getColumnIndex("Ngay")));
            e.setGio(res.getString(res.getColumnIndex("Gio")));
            e.setPhong(res.getString(res.getColumnIndex("Phong")));
            result.add(e);
            res.moveToNext();
        }
        res.close();
        db.close();
        return result;
    }

    /**
     * This method returns a list of Exams, categorised by month of year (1-12)
     * @return ArrayList<ExamMonth>
     */

    public ArrayList<ExamMonth> getExamsCategorisedByMonth(OFFICIAL_STATE_FLAG osf)
    {

        ArrayList<ExamMonth> result = new ArrayList<>();
        ArrayList<Exam> originalData = getAllExam(osf);
        boolean matched;
        for (Exam e : originalData)
        {
            if(!e.getNgay().isEmpty())
            {
                matched = false;
                // Look up the appropriate month-value in the result collection
                for (int i=0; i<result.size(); i++)
                {
                    ExamMonth em = result.get(i);
                    if (em.getThang()==e.getMonth())
                    {
                        matched=true;
                        em.Exams.add(e);
                        break;
                    }
                }
                if (!matched) {
                    // Not a single item in the <result> collection contains e.getMonth(), add a new row into the collection
                    ExamMonth em = new ExamMonth();
                    em.setThang(e.getMonth());
                    em.Exams.add(e);
                    result.add(em);
                }
            }
        }
        Collections.sort(result, new Comparator<ExamMonth>() {
            @Override
            public int compare(ExamMonth exam2, ExamMonth exam1) {
                return exam2.getThang() > exam1.getThang() ? 1 : -1;
            }
        });
        for(ExamMonth exm : result)
        {
            ArrayList<Exam> exs = exm.Exams;
            Collections.sort(exs, new Comparator<Exam>() {
                @Override
                public int compare(Exam exam2, Exam exam1) {
                    return exam2.getNgay2Digits()>exam1.getNgay2Digits()?1:-1;
                }
            });
        }
        return result;
    }

    public SimpleCursorAdapter getAvailableSubjects(Context context)
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT rowid _id, MaMH, TenMH, Nhom FROM exams WHERE Official='0' GROUP BY MaMH", null);
        SimpleCursorAdapter sca = new SimpleCursorAdapter(context, android.R.layout.simple_spinner_dropdown_item, c, new String[]{"TenMH"}, new int[]{android.R.id.text1}, 0);
        return sca;
    }

    public void updateExam(String MaMHSearch, String LoaiSearch, String NhomSearch, String NgayReplace, String GioReplace, String PhongReplace)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Ngay", NgayReplace); //These Fields should be your String values of actual column names
        cv.put("Gio",GioReplace);
        cv.put("Phong",PhongReplace);

        Log.v("AAOSync","Updating exam information: "+MaMHSearch+LoaiSearch+NhomSearch+NgayReplace+GioReplace+PhongReplace);
        db.updateWithOnConflict("exams", cv, "MaMH=? AND Loai=? AND Nhom=?", new String[]{MaMHSearch, LoaiSearch, NhomSearch}, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public ArrayList<Exam> examsToNotify(boolean officialOnly, int notifyDay)
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Exam> result = new ArrayList<>();
        Cursor res;
        Date now = new Date();

        long notifyRadius = notifyDay * 24 * 60 * 60 * 1000;
        Log.v("AAOSync", "Notify Radius: " + notifyRadius);

        if (!officialOnly) res = db.rawQuery("SELECT * FROM exams WHERE MaMH NOT IN (SELECT MaMH FROM notify WHERE MaMH=exams.MaMH AND Nhom=exams.Nhom AND Loai=exams.Loai AND Ngay=exams.Ngay AND Gio=exams.Gio AND Phong=exams.Phong)",null); else res = db.rawQuery("SELECT * FROM exams WHERE Official='1' AND MaMH NOT IN (SELECT MaMH FROM notify WHERE MaMH=exams.MaMH AND Nhom=exams.Nhom AND Loai=exams.Loai AND Ngay=exams.Ngay AND Gio=exams.Gio AND Phong=exams.Phong)", null);
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            Exam e = new Exam();
            e.setNgay(res.getString(res.getColumnIndex("Ngay")));
            e.setGio(res.getString(res.getColumnIndex("Gio")));
            if(e.getNgayGio().getTime()-now.getTime() > 0 && e.getNgayGio().getTime()-now.getTime()<notifyRadius)
            {
                // Eligible to notify user about this particular exam!
                e.setMaMH(res.getString(res.getColumnIndex("MaMH")));
                e.setTenMH(res.getString(res.getColumnIndex("TenMH")));
                e.setLoai(res.getString(res.getColumnIndex("Loai")));
                e.setNhom(res.getString(res.getColumnIndex("Nhom")));
                e.setOfficial(!res.getString(res.getColumnIndex("Official")).equals("0"));
                e.setPhong(res.getString(res.getColumnIndex("Phong")));
                result.add(e);
            }
            res.moveToNext();
        }
        res.close();
        db.close();
        return result;
    }

    public void putNotification(String mMaMH, String mLoai, String mNhom, String mNgay, String mGio, String mPhong)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("MaMH",mMaMH);
        values.put("Loai", mLoai);
        values.put("Nhom", mNhom);
        values.put("Ngay", mNgay);
        values.put("Gio", mGio);
        values.put("Phong", mPhong);

        db.insert("notify", null, values);

        db.close();
    }

    public void eraseAllExamsNotifiers()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DELETE FROM notify");

        db.close();
    }

}
