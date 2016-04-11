package hu.pe.thinhhoang.aaosync.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hoang on 1/7/2016.
 */
public class AAOSyncDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=6;
    public static final String DATABASE_NAME = "AAOSync.db";
    private static AAOSyncDbHelper sInstance;
    private static final String SQL_CREATE_GRADES_ENTRIES = "CREATE TABLE IF NOT EXISTS grades (MaMH TEXT, TenMH TEXT, Nhom TEXT, SoTC INTEGER, DiemKT TEXT, DiemThi TEXT, DiemTK TEXT)";
    private static final String SQL_DELETE_GRADES_ENTRIES = "DROP TABLE IF EXISTS grades";
    private static final String SQL_CREATE_TIMETABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS timetable(TenMH TEXT, Nhom TEXT, Thu INTEGER, TietTu INTEGER, TietDen INTEGER, Phong TEXT, Tuan TEXT)";
    private static final String SQL_DELETE_TIMETABLE_ENTRIES = "DROP TABLE IF EXISTS timetable";
    private static final String SQL_CREATE_EXAMS_ENTRIES = "CREATE TABLE IF NOT EXISTS exams (MaMH TEXT, TenMH TEXT, Loai TEXT, Nhom TEXT, Official INTEGER, Ngay TEXT, Gio TEXT, Phong TEXT)";
    private static final String SQL_DELETE_EXAMS_ENTRIES = "DROP TABLE IF EXISTS exams";
    private static final String SQL_CREATE_EXAMS_NOTIFY_ENTRIES = "CREATE TABLE IF NOT EXISTS notify (MaMH TEXT, Loai TEXT, Nhom TEXT, Ngay TEXT, Gio TEXT, Phong TEXT)";
    private static final String SQL_DELETE_EXAMS_NOTIFY_ENTRIES = "DROP TABLE IF EXISTS notify";

    private AAOSyncDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) { // Prevents initialisation without getInstance
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables if necessary
        db.execSQL(SQL_CREATE_GRADES_ENTRIES);
        db.execSQL(SQL_CREATE_TIMETABLE_ENTRIES);
        db.execSQL(SQL_CREATE_EXAMS_ENTRIES);
        db.execSQL(SQL_CREATE_EXAMS_NOTIFY_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_GRADES_ENTRIES);
        db.execSQL(SQL_DELETE_TIMETABLE_ENTRIES);
        db.execSQL(SQL_DELETE_EXAMS_ENTRIES);
        db.execSQL(SQL_DELETE_EXAMS_NOTIFY_ENTRIES);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    // Singleton approach to prevent memory leak when accessing database
    // Use application context to make sure the received database is the same one throughout the application.
    public static synchronized AAOSyncDbHelper getInstance(Context context){
        if (sInstance==null){
            sInstance = new AAOSyncDbHelper(context.getApplicationContext(),DATABASE_NAME,null,DATABASE_VERSION);
        }
        return sInstance;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
