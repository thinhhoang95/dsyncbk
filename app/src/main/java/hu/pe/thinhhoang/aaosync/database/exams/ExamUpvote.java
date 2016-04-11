package hu.pe.thinhhoang.aaosync.database.exams;

/**
 * Created by hoang on 2/10/2016.
 */
public class ExamUpvote {
    public String Ngay;
    public String Gio;
    public String Phong;
    public int Vote;
    public boolean Upvoted;
    public int Id;

    public ExamUpvote(String mNgay, String mGio, String mPhong, int mVote, boolean mUpvoted, int mId)
    {
        Ngay=mNgay;
        Gio=mGio;
        Phong=mPhong;
        Vote=mVote;
        Upvoted=mUpvoted;
        Id=mId;
    }

}
