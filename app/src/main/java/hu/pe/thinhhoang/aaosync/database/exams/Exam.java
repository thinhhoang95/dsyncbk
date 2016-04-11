package hu.pe.thinhhoang.aaosync.database.exams;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.utils.ActiveClassChecker;
import hu.pe.thinhhoang.aaosync.utils.DateToCalendar;

/**
 * Created by hoang on 2/6/2016.
 */
public class Exam {
    private String MaMH;
    private String TenMH;
    private String Loai;
    private String Nhom;
    private int Official;
    private String Ngay;
    private String Gio;
    private String Phong;
    private int Notified;

    public enum TypesOfExams{GIUAKY, CUOIKY};

    public void setMaMH(String mMaMH)
    {
        MaMH = mMaMH;
    }

    public String getMaMH()
    {
        return MaMH;
    }

    public void setTenMH(String mTenMH)
    {
        TenMH = mTenMH;
    }

    public String getTenMH()
    {
        return TenMH;
    }

    public void setLoai(TypesOfExams mLoai)
    {
        if(mLoai==TypesOfExams.GIUAKY)
        {
            Loai="GiuaKy";
        } else
        {
            Loai="CuoiKy";
        }
    }

    public void setLoai(String mLoai)
    {
        Loai=mLoai;
    }

    public TypesOfExams getLoai()
    {
        if(Loai.equals("GiuaKy"))
        {
            return TypesOfExams.GIUAKY;
        } else {
            return TypesOfExams.CUOIKY;
        }
    }

    public String getLoaiString()
    {
        return Loai;
    }

    public void setNhom(String mNhom)
    {
        Nhom = mNhom;
    }

    public String getNhom()
    {
        return Nhom;
    }

    public boolean getOfficial()
    {
        if (Official==0) return false; else return true;
    }

    public void setOfficial(boolean mOfficial)
    {
        if (mOfficial) Official=1; else Official=0;
    }

    public void setNgay(String mNgay)
    {
        Ngay = mNgay;
    }

    public void setGio(String mGio)
    {
        Gio = mGio;
    }

    public Date getNgayGio()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Settings.STANDARD_DATE_FORMAT, Locale.FRANCE);
        String mNgayGio = Ngay + " " + Gio;
        Date NgayGio = new Date();
        try
        {
            NgayGio = sdf.parse("1980-01-01 00:00:00");
        } catch (Exception ex)
        {
            // Never happens anyway!
        }

        try
        {
            NgayGio = sdf.parse(mNgayGio);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return NgayGio;
    }

    public int getMonth()
    {
        Calendar c = DateToCalendar.convert(getNgayGio());
        return c.get(Calendar.MONTH)+1;
    }

    public String getNgay()
    {
        return Ngay;
    }

    public int getNgay2Digits(){
        Calendar c = DateToCalendar.convert(getNgayGio());
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public String getGio()
    {
        return Gio;
    }

    public String getSimplifedGio()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(getNgayGio());
    }

    public String getSimplifedDateReading()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM");
        return sdf.format(getNgayGio());
    }

    public void setPhong(String mPhong)
    {
        Phong=mPhong;
    }

    public String getPhong()
    {
        return Phong;
    }

    public void setNotified(int v)
    {
        Notified=v;
    }

    public int getNotified()
    {
        return Notified;
    }

}
