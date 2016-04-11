package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import hu.pe.thinhhoang.aaosync.database.timetable.Subject;
import hu.pe.thinhhoang.aaosync.database.timetable.SubjectHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.sync.TimetableSync;
import hu.pe.thinhhoang.aaosync.utils.ActiveClassChecker;
import hu.pe.thinhhoang.aaosync.utils.ClassIndicatorView;
import hu.pe.thinhhoang.aaosync.utils.HoraresLookup;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link TimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // private static final String ARG_PARAM1 = "param1";
    // private static final String ARG_PARAM2 = "param2";

    /*// TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    private RecyclerView mTimeTable;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private Date fdSemester;
    private SwipeRefreshLayout mSwiper;

    private InteractionInterface mListener;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment TimeTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeTableFragment newInstance(/*String param1, String param2*/) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTimeTable = (RecyclerView) view.findViewById(R.id.TimeTableDisplay);
        mTimeTable.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(view.getContext());
        mTimeTable.setLayoutManager(mLinearLayoutManager);
        /*fdSemester=Settings.getFDSemester();
        mAdapter=new TimetableAdapter();
        mTimeTable.setAdapter(mAdapter);*/
        final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        Settings.setContext(view.getContext().getApplicationContext());



        mSwiper=(SwipeRefreshLayout) view.findViewById(R.id.swiper);
        mSwiper.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.amber);
        mSwiper.setDistanceToTriggerSync(400);
        mSwiper.setProgressViewOffset(false, -64, 64);
        mSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        if(Settings.getTimeTableSynced())
        {
            doRefresh();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        refreshViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // inflater.inflate(R.menu.menu_timetable, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int itemId = item.getItemId();
        if (itemId==R.id.menu_refresh)
        {
            // TODO: Refresh the timetable!
            doRefresh();
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionInterface) {
            mListener = (InteractionInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void doRefresh()
    {
        mSwiper.setRefreshing(true);
        TimetableSync syncer = new TimetableSync();
        Settings.setContext(getActivity());
        Settings.setTimeTableSynced();
        /*final ProgressBar spinner = (ProgressBar) getView().findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);*/
        getView().findViewById(R.id.TimeTableDisplay).setVisibility(View.GONE);
        syncer.sync(getActivity(), new TimetableSync.AfterSyncError() {
            @Override
            public void execute() {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Sự cố tải thời khóa biểu", Toast.LENGTH_SHORT).show();
                    // spinner.setVisibility(View.GONE);
                    mSwiper.setRefreshing(false);
                    getView().findViewById(R.id.TimeTableDisplay).setVisibility(View.VISIBLE);
                    refreshViews();
                }
            }
        }, new TimetableSync.AfterSyncSuccess() {
            @Override
            public void execute() {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Thời khóa biểu cập nhật thành công", Toast.LENGTH_SHORT).show();
                    // spinner.setVisibility(View.GONE);
                    getView().findViewById(R.id.TimeTableDisplay).setVisibility(View.VISIBLE);
                    mSwiper.setRefreshing(false);
                    refreshViews();
                }
            }
        });
    }

    private void refreshViews()
    {
        fdSemester=Settings.getFDSemester();
        mAdapter=new TimetableAdapter();
        mTimeTable.setAdapter(mAdapter);
    }

    public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder>
    {
        private String[] lesJours = {"hai","ba","tư","năm","sáu"};
        private SubjectHelper subjectHelper;
        private LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView mThu;
            public LinearLayout mCacMonHoc;

            public ViewHolder(View itemView) {
                super(itemView);
                mThu=(TextView) itemView.findViewById(R.id.Thu);
                mCacMonHoc=(LinearLayout) itemView.findViewById(R.id.CacMonHoc);
            }
        }

        public TimetableAdapter() {
            super();
            subjectHelper = new SubjectHelper(getActivity());
            lp.setMargins((int) (getContext().getResources().getDisplayMetrics().density*16+0.5f),0,0,0);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_timetable, parent, false);
            // Format the view here, if necessary
            ViewHolder vh=new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Settings.setContext(getContext().getApplicationContext());
            String facil = Settings.getFacility();
            holder.mThu.setText("Thứ " + lesJours[position]);
            // Populate the linear layout "CacMonHoc" with appropriate data

//            Log.v("AAOSync", "Requesting data for day of week: " + position + " which has " + holder.mCacMonHoc.getChildCount() + " children");
            /*if(holder.mCacMonHoc.getChildCount()==0 || holder.mCacMonHoc.getChildCount()==1)
            {*/

            ArrayList<Subject> subjects = subjectHelper.getSubjectsByWeekDay(position + 2, fdSemester);
            if (subjects.size() == 0) {
                holder.mCacMonHoc.removeAllViews();
                TextView tv = new TextView(holder.mCacMonHoc.getContext());
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setText("Không có tiết học nào.");
                tv.setLayoutParams(lp);
                holder.mCacMonHoc.addView(tv);
                holder.mCacMonHoc.requestLayout();
                holder.mCacMonHoc.invalidate();
            } else {
                holder.mCacMonHoc.removeAllViews();
                for (Subject sj : subjects) {
                    View subjectView = LayoutInflater.from(holder.mCacMonHoc.getContext()).inflate(R.layout.list_item_timetable_subject, holder.mCacMonHoc, false);
                    TextView TenMHTV = (TextView) subjectView.findViewById(R.id.TenMH);
                    TextView GioBatDau = (TextView) subjectView.findViewById(R.id.GioBatDau);
                    TextView GioKetThuc = (TextView) subjectView.findViewById(R.id.GioKetThuc);
                    TextView Phong = (TextView) subjectView.findViewById(R.id.Phong);
                    ClassIndicatorView civ = (ClassIndicatorView) subjectView.findViewById(R.id.classIndicator);
                    TextView playingIcon = (TextView) subjectView.findViewById(R.id.playingIcon);
                    TenMHTV.setText(sj.TenMH);
                    if (facil.equals("CS1")) {
                        GioBatDau.setText(HoraresLookup.getEntryHorareF1(sj.TietTu));
                        GioKetThuc.setText(HoraresLookup.getExitHorareF1(sj.TietDen));
                    } else if (facil.equals("CS2")) {
                        GioBatDau.setText(HoraresLookup.getEntryHorareF2(sj.TietTu));
                        GioKetThuc.setText(HoraresLookup.getExitHorareF2(sj.TietDen));
                    }
                    ActiveClassChecker.ClassType ct = ActiveClassChecker.check(sj.Thu, GioBatDau.getText() + ":00", GioKetThuc.getText() + ":00");
                    if (ct == ActiveClassChecker.ClassType.ONGOING) {
                        playingIcon.setVisibility(View.VISIBLE);
                    } else if (ct == ActiveClassChecker.ClassType.ABOUTTO) {
                        playingIcon.setVisibility(View.VISIBLE);
                        playingIcon.setTextColor(ContextCompat.getColor(getContext(), R.color.amber));
                    } else {
                        playingIcon.setVisibility(View.INVISIBLE);
                    }
                    civ.setDrawingCacheEnabled(true);
                    civ.setmSoTiet(sj.TietDen - sj.TietTu + 1);
                    Phong.setText(sj.Phong);
                    holder.mCacMonHoc.addView(subjectView);
                }
                // Invalidate the view so everything is measured one more time
                holder.mCacMonHoc.requestLayout();
                holder.mCacMonHoc.invalidate();
            }
            /*}*/
        }

        @Override
        public int getItemCount() {
            return lesJours.length;
        }
    }
}
