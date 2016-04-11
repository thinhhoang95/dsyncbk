package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link GradesMasterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradesMasterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /* private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    // TODO: Rename and change types of parameters
    /*private String mParam1;
    private String mParam2;*/

    private InteractionInterface mListener;
    private GradesTabAdapter mTabAdapter;
    GradesFragment gradesFragment = new GradesFragment();
    GradesInfoFragment gradesInfoFragment = new GradesInfoFragment();
    GradeQuickSearchFragment gradeQuickSearchFragment = new GradeQuickSearchFragment();


    public GradesMasterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GradesMasterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradesMasterFragment newInstance() {
        GradesMasterFragment fragment = new GradesMasterFragment();
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
        gradesFragment.metaInfoHandler = gradesInfoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grades_master, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // gradesFragment = new GradesFragment();
        // gradesInfoFragment = new GradesInfoFragment();

        mTabAdapter = new GradesTabAdapter(getChildFragmentManager()); // Must be reinitialised every time to make sure the menu is properly created
        // if (FLAG_ALLOW_REFRESH) gradesFragment.FLAG_ALLOW_REFRESH=true; else gradesFragment.FLAG_ALLOW_REFRESH=false;
        ViewPager mPager = (ViewPager) getView().findViewById(R.id.pager);
        mPager.setAdapter(mTabAdapter);
        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        /*int[] coordinates = new int[2];
        mPager.getLocationInWindow(coordinates);

        Log.v("AAOSync", "mPager Coordinates: " + coordinates[0] + "; " + coordinates[1]);*/
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

    private class GradesTabAdapter extends FragmentStatePagerAdapter
    {
        public GradesTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return gradesFragment;
                case 1:
                    return gradesInfoFragment;
                case 2:
                    return gradeQuickSearchFragment;
                default:
                    return null;
            }
            // return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position)
            {
                case 0:
                    return "ĐIỂM THI";
                case 1:
                    return "CHI TIẾT";
                case 2:
                    return "XEM NHANH";
                default:
                    return null;
            }
        }
    }
}
