package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link GradeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradeDetailFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "grade";

    // TODO: Rename and change types of parameters
    private Grade mGrade;

    private InteractionInterface mListener;

    public GradeDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GradeDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradeDetailFragment newInstance(String param1) {
        GradeDetailFragment fragment = new GradeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String jGrade = getArguments().getString(ARG_PARAM1);
            mGrade = new Gson().fromJson(jGrade,Grade.class);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.menu_grade_detail_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTenMH = (TextView) view.findViewById(R.id.TenMH);
        TextView mInfoMH = (TextView) view.findViewById(R.id.InfoMH);
        TextView mDiemKT = (TextView) view.findViewById(R.id.DiemKT);
        TextView mDiemThi = (TextView) view.findViewById(R.id.DiemThi);
        TextView mDiemTK = (TextView) view.findViewById(R.id.DiemTK);

        RelativeLayout mMetaBackground = (RelativeLayout) view.findViewById(R.id.metaBackground);

        mTenMH.setText(mGrade.TenMH.toUpperCase());
        mInfoMH.setText("Mã MH: "+mGrade.MaMH+" | Nhóm: "+mGrade.Nhom+" | Số TC: "+mGrade.SoTC.toString());
        mDiemKT.setText(mGrade.DiemKT);
        mDiemThi.setText(mGrade.DiemThi);
        mDiemTK.setText(mGrade.DiemTK);

        try
        {
            float tk = Float.parseFloat(mGrade.DiemTK);
            if(tk>=5f || mGrade.DiemThi.equals("DT"))
            {
                // Log.v("AAOSync","Parsed float: "+tk);
                mMetaBackground.setBackgroundResource(R.color.darkGreen);
            } else {
                mMetaBackground.setBackgroundResource(R.color.darkRed);
            }
        }
        catch (Exception e) {
            if (mGrade.DiemTK.equals("CH"))
            {
                mMetaBackground.setBackgroundResource(R.color.darkAmber);
            } else {
                mMetaBackground.setBackgroundResource(R.color.darkRed);
            }
        }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    } */
}
