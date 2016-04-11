package hu.pe.thinhhoang.aaosync.database.grades;

/**
 * Created by hoang on 1/8/2016.
 */
public class Grade {
    public String MaMH;
    public String TenMH;
    public String Nhom;
    public Integer SoTC;
    public String DiemKT;
    public String DiemThi;
    public String DiemTK;
    public Grade(String MaMH, String TenMH, String Nhom, int SoTC, String DiemKT, String DiemThi, String DiemTK)
    {
        this.MaMH = MaMH;
        this.TenMH = TenMH;
        this.Nhom = Nhom;
        this.SoTC = SoTC;
        this.DiemKT = DiemKT;
        this.DiemThi = DiemThi;
        this.DiemTK = DiemTK;
    }
}
