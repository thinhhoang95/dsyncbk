package hu.pe.thinhhoang.aaosync;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import hu.pe.thinhhoang.aaosync.database.exams.Exam;
import hu.pe.thinhhoang.aaosync.database.exams.ExamMonth;
import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.sync.ExamsSync;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;
import hu.pe.thinhhoang.aaosync.utils.VoidInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link ExamsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExamsFragment extends Fragment {

    RecyclerView mList;
    ExamsAdapter mAdapter;
    ExamsHelper examsHelper;
    SwipeRefreshLayout mSwiper;
    ExamsHelper.OFFICIAL_STATE_FLAG osf = ExamsHelper.OFFICIAL_STATE_FLAG.ALL;;
    ExamsSync examsSync = new ExamsSync();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    /*// TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    private InteractionInterface mListener;

    public ExamsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ExamsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExamsFragment newInstance(/*String param1, String param2*/) {
        ExamsFragment fragment = new ExamsFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exams, container, false);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwiper = (SwipeRefreshLayout) view.findViewById(R.id.swiperExams);
        mSwiper.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.amber);
        mSwiper.setDistanceToTriggerSync(240);
        mSwiper.setProgressViewOffset(false, -64, 64);
        mSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        mList = (RecyclerView) view.findViewById(R.id.exams_recyclerView);
        mList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        mList.setLayoutManager(mLayoutManager);

        examsHelper = new ExamsHelper(getContext().getApplicationContext());

        //Settings.setContext(getContext().getApplicationContext());
        //if(!Settings.getShowContributedExams()) osf = ExamsHelper.OFFICIAL_STATE_FLAG.OFFICIAL_ONLY;

        mAdapter = new ExamsAdapter(examsHelper.getExamsCategorisedByMonth(osf));
        mList.setAdapter(mAdapter);

        // Fab listener connecting
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_add_event);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddNewExamActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("AAOSync", "onStart called");
        refreshViewsHighPerformance();
        // Refresh the list if permitted (data older than 3 days)
        Settings.setContext(getContext().getApplicationContext());
        if(Settings.getExamsSynced())
        {
            doRefresh();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_exams, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_info)
        {
            showInformationDialog();
            return true;
        } /*else if (item.getItemId()==R.id.menu_launch_settings)
        {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }*/ else
        return super.onOptionsItemSelected(item);
    }

    private void showInformationDialog()
    {
        InfoFragment infoFragment = new InfoFragment();
        infoFragment.show(getActivity().getSupportFragmentManager(),"InformationDialog");
    }

    private void refreshViews()
    {
        if(!Settings.getShowContributedExams()) osf = ExamsHelper.OFFICIAL_STATE_FLAG.OFFICIAL_ONLY; else osf = ExamsHelper.OFFICIAL_STATE_FLAG.ALL;
        // Log.v("AAOSync", "Refresh views...");
        mAdapter = new ExamsAdapter(examsHelper.getExamsCategorisedByMonth(osf));
        mList.setAdapter(mAdapter);
    }

    private void refreshViewsHighPerformance()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!Settings.getShowContributedExams()) osf = ExamsHelper.OFFICIAL_STATE_FLAG.OFFICIAL_ONLY; else osf = ExamsHelper.OFFICIAL_STATE_FLAG.ALL;
                mAdapter.data.clear();
                mAdapter.data.addAll(examsHelper.getExamsCategorisedByMonth(osf));
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

//    private void setAdapterForRefreshViewsHighPerformance()
//    {
//        mList.setAdapter(mAdapter);
//    }

    private void doRefresh() {
        // if(!Settings.getShowContributedExams()) osf = ExamsHelper.OFFICIAL_STATE_FLAG.OFFICIAL_ONLY; else osf = ExamsHelper.OFFICIAL_STATE_FLAG.ALL;
        Settings.setExamsSynced();
        mSwiper.setRefreshing(true);
        examsSync.premierSync(getContext().getApplicationContext(), new ExamsSync.AfterSyncError() {
            @Override
            public void execute() {
                if(getContext()!=null)
                {
                    Toast.makeText(getContext(), "Không thể tải lịch thi", Toast.LENGTH_SHORT).show();
                    refreshViewsHighPerformance();
                }
                mSwiper.setRefreshing(false);
            }
        }, new ExamsSync.AfterSyncSuccess() {
            @Override
            public void execute() {
                if (getContext()!=null)
                {
                    Toast.makeText(getContext(), "Lịch thi cập nhật thành công", Toast.LENGTH_SHORT).show();
                    refreshViewsHighPerformance();
                }
                mSwiper.setRefreshing(false);
            }
        });
    }

    public static class InfoFragment extends DialogFragment
    {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exam_tutorial_dialog, container, false);
            getDialog().setTitle("Trung tâm lịch thi mới");
            return rootView;
        }
    }

    public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ViewHolder>
    {
        private ArrayList<ExamMonth> data;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView monthTextView;
            public ImageView monthDecor;
            public LinearLayout examsList;

            public ViewHolder(View itemView) {
                super(itemView);
                monthTextView = (TextView) itemView.findViewById(R.id.monthTextView);
                monthDecor = (ImageView) itemView.findViewById(R.id.monthDecor);
                examsList = (LinearLayout) itemView.findViewById(R.id.examsList);
            }
        }

        /*public class ExamViewHolder extends RecyclerView.ViewHolder
        {
            public TextView mTenMH;
            public TextView mPhong;
            public TextView mNgay;
            public View mIndicator;

            public ExamViewHolder(View itemView) {
                super(itemView);

                mTenMH = (TextView) itemView.findViewById(R.id.TenMH);
                mPhong = (TextView) itemView.findViewById(R.id.Phong);
                mNgay = (TextView) itemView.findViewById(R.id.Ngay);
                mIndicator = itemView.findViewById(R.id.OfficialIndicator);
            }
        }*/

        public ExamsAdapter(ArrayList<ExamMonth> d)
        {
            data = d;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_exam_month, parent, false));
        }

        public int getBackgroundResource(int m)
        {
            switch(m)
            {
                case 1:
                    return R.drawable.spring1;
                case 2:
                    return R.drawable.spring2;
                case 3:
                    return R.drawable.spring3;
                case 4:
                    return R.drawable.summer1;
                case 5:
                    return R.drawable.summer2;
                case 6:
                    return R.drawable.summer3;
                case 7:
                    return R.drawable.autumn1;
                case 8:
                    return R.drawable.autumn2;
                case 9:
                    return R.drawable.autumn3;
                case 10:
                    return R.drawable.winter1;
                case 11:
                    return R.drawable.winter2;
                case 12:
                    return R.drawable.winter3;
                default:
                    return R.drawable.spring1;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            int mois = data.get(position).getThang();
            holder.monthTextView.setText("THÁNG " + mois);
            /*((BitmapDrawable)holder.monthDecor.getDrawable()).getBitmap().recycle();
            holder.monthDecor.setImageResource(getBackgroundResource(mois));*/
            Picasso.with(getActivity().getApplicationContext()).load(getBackgroundResource(mois)).centerCrop().fit().into(holder.monthDecor);
            ArrayList<Exam> exams = data.get(position).Exams;

            holder.examsList.removeAllViews();

                // Add items to the list
                for (final Exam e : exams)
                {
                    View vx = LayoutInflater.from(holder.examsList.getContext()).inflate(R.layout.list_item_exam, holder.examsList, false);
                    TextView mTenMH = (TextView) vx.findViewById(R.id.TenMH);
                    TextView mPhong = (TextView) vx.findViewById(R.id.Phong);
                    TextView mNgay = (TextView) vx.findViewById(R.id.Ngay);
                    View mIndicator = vx.findViewById(R.id.OfficialIndicator);
                    mTenMH.setText(e.getTenMH()+(e.getLoai().equals(Exam.TypesOfExams.GIUAKY)?" (GK)":" (CK)"));
                    mPhong.setText(e.getSimplifedGio() + " - " + e.getPhong());
                    mNgay.setText(Integer.toString(e.getNgay2Digits()));
                    if(!e.getOfficial())
                    {
                        mIndicator.setBackgroundResource(R.color.indigo);
                        // Hook up listener!
                        // vx.setClickable(true);
                        vx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v("AAOSync", "Firing a new intent to view available options");
                                Intent i = new Intent(getActivity().getApplicationContext(),UpvoteActivity.class);
                                i.putExtra("TenMH",e.getTenMH());
                                i.putExtra("MaMH",e.getMaMH());
                                i.putExtra("Loai",e.getLoaiString());
                                i.putExtra("Nhom",e.getNhom());
                                getActivity().startActivity(i);

                            }
                        });
                    } else {
                        mIndicator.setBackgroundResource(R.color.green);
                        // vx.setClickable(false);
                        vx.setOnClickListener(null);
                    }
                    holder.examsList.addView(vx);
                }
                holder.examsList.requestLayout();
                holder.examsList.invalidate();

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

    }

}
