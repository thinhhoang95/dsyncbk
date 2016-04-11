package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.database.grades.GradesAdapter;
import hu.pe.thinhhoang.aaosync.database.grades.GradesHelper;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link GradeSearchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradeSearchListFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // private static final String ARG_PARAM1 = "keyword";
    // private static final String ARG_PARAM2 = "param2";
    private ListView mListView;
    private GradesAdapter gradesAdapter;
    private GradesHelper gradeMachine;

    // TODO: Rename and change types of parameters
    // private String mKeyword;
    // private String mParam2;

    private InteractionInterface mListener;

    public GradeSearchListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GradeSearchListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradeSearchListFragment newInstance() {
        GradeSearchListFragment fragment = new GradeSearchListFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, keyword);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mKeyword = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
        gradeMachine = new GradesHelper(getContext().getApplicationContext());
        gradesAdapter=new GradesAdapter(getContext(), new ArrayList<Grade>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade_search_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Connect the listview to the adapter
        mListView = (ListView) view.findViewById(R.id.list_grades);
        mListView.setAdapter(gradesAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Grade g = (Grade) mListView.getItemAtPosition(position);
                mListener.onFragmentInteraction("NAVIGATE_TO_GRADE_DETAILS",g);
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * PUBLIC INTERFACE FOR INTERACTION FROM OUTER RING (HOSTING ACTIVITY)
     */

    public void performSearch(String keyword)
    {
        gradesAdapter.clear();
        gradesAdapter.addAll(gradeMachine.findGrade(keyword));
        gradesAdapter.notifyDataSetChanged();
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
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
