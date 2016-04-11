package hu.pe.thinhhoang.aaosync.database.timetable;

/**
 * Created by hoang on 1/31/2016.
 */
public class Subject {
    public String TenMH;
    public String Nhom;
    public int Thu;
    public int TietTu;
    public int TietDen;
    public String Phong;
    public String Tuan;

    public Subject(String mTenMH, String mNhom, int mThu, int mTietTu, int mTietDen, String mPhong, String mTuan)
    {
        TenMH=mTenMH;
        Nhom=mNhom;
        Thu=mThu;
        TietTu=mTietTu;
        TietDen=mTietDen;
        Phong=mPhong;
        Tuan=mTuan;
    }
}
