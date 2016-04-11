package hu.pe.thinhhoang.aaosync.database.exams;

import java.util.ArrayList;

/**
 * Created by hoang on 2/6/2016.
 */
public class ExamMonth {
    private int Thang;
    public ArrayList<Exam> Exams = new ArrayList<>();

    public void setThang(int thang)
    {
        Thang = thang;
    }

    public int getThang()
    {
        return Thang;
    }
}
