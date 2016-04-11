package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.sync.GradesSync;
import hu.pe.thinhhoang.aaosync.sync.QuickViewGradesSync;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link GradeQuickSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradeQuickSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    /*// TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    private InteractionInterface mListener;
    private ArrayList<Grade> gradesArr = new ArrayList<>();
    private SimpleGradesAdapter simpleGradesAdapter;

    public GradeQuickSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GradeQuickSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradeQuickSearchFragment newInstance() {
        GradeQuickSearchFragment fragment = new GradeQuickSearchFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        simpleGradesAdapter = new SimpleGradesAdapter(gradesArr);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade_quick_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.quickMSSVList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mlayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setAdapter(simpleGradesAdapter);
        final EditText mssvET = (EditText) getView().findViewById(R.id.mssvField);
        mssvET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_NEXT)
                {
                    refreshGrade();
                    return true;
                }
                return false;
            }
        });
        mssvET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mssvET.setText("");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionInterface) {
            mListener = (InteractionInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void refreshGrade()
    {
        Log.v("AAOSync", "Refreshing grades for quick view");
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        final EditText mssvET = (EditText) getView().findViewById(R.id.mssvField);
        final RelativeLayout mssvFL = (RelativeLayout) getView().findViewById(R.id.mssvBackground);
        mssvFL.setBackgroundResource(R.color.nonInvasiveGray);
        String mssv = mssvET.getText().toString();
        if(!mssv.isEmpty())
        {
            showLoadingCircle();
            QuickViewGradesSync qvs = new QuickViewGradesSync();
            qvs.quickSync(mssv, getContext(), new QuickViewGradesSync.AfterSyncParams() {
                @Override
                public void execute(Object param) {
                    simpleGradesAdapter.arrayGrades.clear();
                    simpleGradesAdapter.arrayGrades.addAll((ArrayList<Grade>)param);
                    simpleGradesAdapter.notifyDataSetChanged();
                    hideLoadingCircle();
                    // Log.v("AAOSync", "RecyclerView notified. Total of "+gradesArr.size());
                }
            }, new GradesSync.AfterSyncError() {
                @Override
                public void execute() {
                    hideLoadingCircle();
                    mssvET.setText("Lỗi! Hãy kiểm tra MSSV và thử lại.");
                    mssvFL.setBackgroundResource(R.color.partialRed);
                }
            });
        }
    }

    private void showLoadingCircle()
    {
        getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.quickMSSVList).setVisibility(View.GONE);
    }

    private void hideLoadingCircle()
    {
        getView().findViewById(R.id.progress).setVisibility(View.GONE);
        getView().findViewById(R.id.quickMSSVList).setVisibility(View.VISIBLE);
    }

    public class SimpleGradesAdapter extends RecyclerView.Adapter<SimpleGradesAdapter.ViewHolder>
    {
        private ArrayList<Grade> arrayGrades;
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView mTenMH;
            public TextView mSoTC;
            public TextView mDiemKT;
            public TextView mDiemThi;
            public TextView mDiemTK;
            public ImageView mDoneMark;
            public ViewHolder(View itemView) {
                super(itemView);
                mTenMH = (TextView) itemView.findViewById(R.id.TenMH);
                mSoTC = (TextView) itemView.findViewById(R.id.SoTC);
                mDiemKT = (TextView) itemView.findViewById(R.id.DiemKT);
                mDiemThi = (TextView) itemView.findViewById(R.id.DiemThi);
                mDiemTK = (TextView) itemView.findViewById(R.id.DiemTK);
                mDoneMark = (ImageView) itemView.findViewById(R.id.doneMark);

            }
            public void bindGrade(Grade grade)
            {
                mTenMH.setText(grade.TenMH);
                mSoTC.setText(grade.SoTC.toString()+" tín chỉ/ĐVHP");
                if (grade.DiemKT.endsWith("0") && grade.DiemKT.length()>=4) grade.DiemKT=grade.DiemKT.substring(0,grade.DiemKT.length()-1);
                if(grade.DiemKT.equals("10.") || grade.DiemKT.equals("10.0")) grade.DiemKT="10";
                grade.DiemKT=convertDash(grade.DiemKT);
                mDiemKT.setText(grade.DiemKT);
                if (grade.DiemThi.endsWith("0") && grade.DiemThi.length()>=4) grade.DiemThi=grade.DiemThi.substring(0,grade.DiemThi.length()-1);
                if (grade.DiemThi.equals("10.") || grade.DiemThi.equals("10.0")) grade.DiemThi="10";
                grade.DiemThi=convertDash(grade.DiemThi);
                mDiemThi.setText(grade.DiemThi);
                if (grade.DiemTK.endsWith("0") && grade.DiemTK.length()>=4) grade.DiemTK=grade.DiemTK.substring(0,grade.DiemTK.length()-1);
                if(grade.DiemTK.equals("10.") || grade.DiemTK.equals("10.0")) grade.DiemTK="10";
                grade.DiemTK=convertDash(grade.DiemTK);
                mDiemTK.setText(grade.DiemTK);
                // Try to parse DiemTK
                try
                {
                    float tk = Float.parseFloat(grade.DiemTK);
                    if(tk>=5f || grade.DiemThi.equals("DT"))
                    {
                        // Log.v("AAOSync","Parsed float: "+tk);
                        mDoneMark.setVisibility(View.VISIBLE);
                        mDiemTK.setBackgroundResource(R.color.green);
                    } else {
                        mDiemTK.setBackgroundResource(R.color.red);
                        mDoneMark.setVisibility(View.INVISIBLE);
                    }
                }
                catch (Exception e) {
                    if (grade.DiemTK.equals("CH"))
                    {
                        mDiemTK.setBackgroundResource(R.color.amber);
                    } else {
                        mDiemTK.setBackgroundResource(R.color.red);
                    }
                    mDoneMark.setVisibility(View.INVISIBLE);
                }
                // Initialize the view here!
            }
        }

        // Constructor
        public SimpleGradesAdapter(ArrayList<Grade> data)
        {
            this.arrayGrades = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_grade,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Grade g = arrayGrades.get(position);
            holder.bindGrade(g);
        }

        @Override
        public int getItemCount() {
            return arrayGrades.size();
        }

        private String convertDash(String input)
        {
            if(input.equals("---")) return ""; else return input;
        }
    }
}
