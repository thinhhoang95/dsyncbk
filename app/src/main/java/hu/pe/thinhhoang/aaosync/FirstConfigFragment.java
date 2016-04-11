package hu.pe.thinhhoang.aaosync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hu.pe.thinhhoang.aaosync.settings.Settings;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {} interface
 * to handle interaction events.
 * Use the {@link FirstConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstConfigFragment extends Fragment {

    public FirstConfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FirstConfigFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstConfigFragment newInstance() {
        FirstConfigFragment fragment = new FirstConfigFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first_config, container, false);

        // Event handlers for UI Components

        final Button loginButton = (Button) v.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMSSV();
            }
        });

        EditText mssvEditText = (EditText) v.findViewById(R.id.mssvEditText);
        mssvEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_NEXT)
                {
                    saveMSSV();
                    return true;
                }
                return false;
            }
        });

        loadMSSV(v);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /* Generic methods */

    private void saveMSSV(){
        Settings.setContext(getContext());
        String mssv;
        EditText mssvEditText = (EditText) getActivity().findViewById(R.id.mssvEditText);
        mssv=mssvEditText.getText().toString();
        if(mssv.isEmpty())
        {
            Toast.makeText(getActivity(),"Vui lòng nhập MSSV hợp lệ vào ô.",Toast.LENGTH_SHORT).show();
        } else {
            Settings.setMSSV(mssv);
            Log.v("AAOSync","The MSSV "+mssv+" is registered");
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void loadMSSV(View v){
        Settings.setContext(getContext());
        String mssv = Settings.getMSSV();
        EditText mssvEditText = (EditText) v.findViewById(R.id.mssvEditText);
        if(!mssv.equals("undef")){
            mssvEditText.setText(mssv);
        }
    }

}
