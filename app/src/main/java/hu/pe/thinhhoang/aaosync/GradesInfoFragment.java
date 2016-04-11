package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InteractionInterface} interface
 * to handle interaction events.
 * Use the {@link GradesInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradesInfoFragment extends Fragment implements InteractionInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private InteractionInterface mListener;

    public GradesInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GradesInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradesInfoFragment newInstance(String param1, String param2) {
        GradesInfoFragment fragment = new GradesInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grades_info, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        updateScreen();
    }

    private void updateScreen()
    {
        Log.v("AAOSync", "Starting fragment GradesInfoFragment");
        try
        {
            TextView mTongSoTC = (TextView) getView().findViewById(R.id.tongSoTC);
            TextView mTBTichLuy = (TextView) getView().findViewById(R.id.tbTichLuy);
            Settings.setContext(getContext().getApplicationContext());
            mTongSoTC.setText(Settings.getTotalCredits());
            mTBTichLuy.setText(Settings.getAverageScore());
        } catch (Exception e)
        {
            Log.v("AAOSync","Context is not available! Can not update Fragment!");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(String action, Object params) {
        if (action.equals("REFRESH_META_DATA_NOW"))
        {
            Log.v("AAOSync-E","Refreshing meta data...");
            updateScreen();
        }
    }
}
