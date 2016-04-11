package hu.pe.thinhhoang.aaosync;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import hu.pe.thinhhoang.aaosync.database.exams.ExamUpvote;
import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.utils.ActiveClassChecker;
import hu.pe.thinhhoang.aaosync.utils.DateToCalendar;
import hu.pe.thinhhoang.aaosync.utils.VoidInterface;

public class UpvoteActivity extends AppCompatActivity implements VoidInterface {

    private String TenMH, MaMH, Nhom, Loai, Ngay, Gio, Phong;
    private boolean REFRESHING_FLAG = false;
    UpvotesAdapter upvotesAdapter;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    ExamsHelper examsHelper;

    UpvoteNetworkHandler networkClient = new UpvoteNetworkHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upvote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        examsHelper = new ExamsHelper(this.getApplicationContext());

        // Load the parameters
        if(savedInstanceState==null)
        {
            Bundle extras = getIntent().getExtras();
            TenMH=extras.getString("TenMH").toUpperCase();
            MaMH=extras.getString("MaMH");
            Nhom=extras.getString("Nhom");
            Loai=extras.getString("Loai");
            Ngay=extras.getString("Ngay");
            Gio=extras.getString("Gio");
            Phong=extras.getString("Phong");

        }

        // Populates the controls
        TextView mTenMH = (TextView) findViewById(R.id.TenMH);
        TextView mMaMH = (TextView) findViewById(R.id.MaMH);
        TextView mLoai = (TextView) findViewById(R.id.Loai);
        TextView mNhom = (TextView) findViewById(R.id.Nhom);
        recyclerView = (RecyclerView) findViewById(R.id.listAvailExams);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mTenMH.setText(TenMH);
        mMaMH.setText(MaMH);
        // Log.v("AAOSync","Loai: "+Loai);
        mLoai.setText(Loai.equals("GiuaKy")?"GK":"CK");
        mNhom.setText(Nhom);

        // Populates the listview
        doRefresh();
    }

    @Override
    public void callback(Object param) {
        REFRESHING_FLAG=false;
        setRefreshing(false);
        if(param==null)
        {
            // TODO: Handle the error!
            Toast.makeText(this, "Không thể tải dữ liệu lịch thi", Toast.LENGTH_SHORT).show();
        } else
        {
            ArrayList<ExamUpvote> upvotes = (ArrayList<ExamUpvote>) param;
            // Toast.makeText(this, "Có "+upvotes.size()+" lịch thi tìm thấy", Toast.LENGTH_SHORT).show();
            upvotesAdapter = new UpvotesAdapter(upvotes);
            recyclerView.setAdapter(upvotesAdapter);
        }

    }

    public void doRefresh()
    {
        if(!REFRESHING_FLAG)
        {
            setRefreshing(true);
            REFRESHING_FLAG=true;
            networkClient.get(MaMH, Loai, Nhom, this);
        }
    }

    public void setRefreshing(boolean v)
    {
        ProgressBar pbar = (ProgressBar) findViewById(R.id.progress);
        if (v) pbar.setVisibility(View.VISIBLE); else pbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_upvote, menu);
        return true;
        // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Handle the menu click
        int id = item.getItemId();
        if (id==android.R.id.home)
        {
            finish();
            return true;
        } else if (id==R.id.menu_refresh)
        {
            doRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class UpvoteNetworkHandler
    {
        public final static String URL_UPVOTE_BASE = "http://thinhhoang.pe.hu/whitestar/exams_operations.php?action=list";
        public final static String URL_UPVOTE_ACT_BASE = "http://thinhhoang.pe.hu/whitestar/exams_operations.php?action=upvote";
        private ArrayList<ExamUpvote> examUpvotes;

        public void get(String MaMH, String Loai, String Nhom, final VoidInterface vi)
        {
            examUpvotes = new ArrayList<>();
            Settings.setContext(UpvoteActivity.this.getApplicationContext());
            String suffix = "&MaMH="+MaMH+"&Loai="+Loai+"&Nhom="+ Uri.encode(Nhom)+"&mssv="+Settings.getMSSV();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(4, 200);
            client.get(URL_UPVOTE_BASE + suffix, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    vi.callback(null);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    try{
                        JSONArray j = new JSONArray(s);
                        for(int k=0; k<j.length(); k++)
                        {
                            JSONObject o = j.getJSONObject(k);
                            examUpvotes.add(new ExamUpvote(
                               o.getString("Ngay"),
                               o.getString("Gio"),
                               o.getString("Phong"),
                               o.getInt("Vote"),
                               o.getInt("Upvoted")==0?false:true,
                               o.getInt("id")
                            ));
                        }
                        vi.callback(examUpvotes);
                    } catch (Exception e)
                    {
                        vi.callback(null);
                        e.printStackTrace();
                    }
                }
            });
        }

        public void send(final ExamUpvote examUpvote)
        {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(4, 200);
            Settings.setContext(UpvoteActivity.this);
            String mssv=Settings.getMSSV();
            String suffix="&mssv="+mssv+"&id="+examUpvote.Id;
            if(!Settings.getShowUpvotedExamByDefault()) suffix+="&donotlog=1";
            if(Settings.getShowUpvotedExamByDefault()) examsHelper.updateExam(MaMH, Loai, Nhom, examUpvote.Ngay, examUpvote.Gio, examUpvote.Phong);
            client.get(URL_UPVOTE_ACT_BASE + suffix, new TextHttpResponseHandler(){
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "Lỗi kết nối lịch thi", Toast.LENGTH_SHORT).show();
                    if(Settings.getShowUpvotedExamByDefault()) examsHelper.updateExam(MaMH, Loai, Nhom, Ngay, Gio, Phong); // Revert the changes
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if(!s.isEmpty())
                    {
                        Log.v("AAOSync", "Success +1: RESPONSE IS " + s);
                        Toast.makeText(getApplicationContext(), "Lỗi kết nối lịch thi", Toast.LENGTH_SHORT).show();
                        if(Settings.getShowUpvotedExamByDefault()) examsHelper.updateExam(MaMH, Loai, Nhom, Ngay, Gio, Phong); // Revert the changes
                    }
                }
            });
        }
    }

    public class UpvotesAdapter extends RecyclerView.Adapter<UpvotesAdapter.ViewHolder>
    {
        ArrayList<ExamUpvote> data = new ArrayList<>();
        private SimpleDateFormat sdf = new SimpleDateFormat(Settings.STANDARD_DATE_FORMAT, Locale.FRANCE);
        private SimpleDateFormat sdfReadable = new SimpleDateFormat("HH:mm dd/MM", Locale.FRANCE);

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            public TextView mInfo;
            public Button mVoteButton;
            public VoidInterface mListener;

            public ViewHolder(View itemView) {
                super(itemView);
                mInfo = (TextView) itemView.findViewById(R.id.info);
                mVoteButton = (Button) itemView.findViewById(R.id.upvoteButton);
                mVoteButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(mListener!=null)
                {
                    mListener.callback(getLayoutPosition());
                }
            }
        }

        public UpvotesAdapter(ArrayList<ExamUpvote> mData) {
            super();
            data=mData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_upvote, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ExamUpvote e = data.get(position);
            try {
                String output = sdfReadable.format(sdf.parse(e.Ngay + " " + e.Gio)) + " tại "+e.Phong;
                holder.mInfo.setText(output);
                holder.mVoteButton.setText("+"+e.Vote);
                if(!e.Upvoted)
                {
                    holder.mVoteButton.setEnabled(true);
                    holder.mListener = new VoidInterface() {
                        @Override
                        public void callback(Object param) {
                            Log.v("AAOSync", "Perform a network call to upvote!");
                            holder.mVoteButton.setEnabled(false);
                            networkClient.send(e);
                        }
                    };
                } else {
                    holder.mVoteButton.setEnabled(false);
                    holder.mListener = null;
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
