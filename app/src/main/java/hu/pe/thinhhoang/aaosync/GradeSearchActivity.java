package hu.pe.thinhhoang.aaosync;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;

public class GradeSearchActivity extends AppCompatActivity implements InteractionInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_search);

        Toolbar leToolbar = (Toolbar) findViewById(R.id.toolbar);
        leToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(leToolbar);

        Log.v("AAOSync-N","Recreating fragment...");

        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GradeSearchListFragment gradeSearchListFragment = new GradeSearchListFragment();
        fragmentTransaction.replace(R.id.viewPortGradeSearch, gradeSearchListFragment, "GRADE_SEARCH_LIST_FRAGMENT");
        fragmentTransaction.commit();

        // Connect the search action
        final EditText searchBox = (EditText) findViewById(R.id.search_grade);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Hide the soft-keyboard
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    String k = v.getText().toString();
                    // Do search
                    if (!k.isEmpty() && fragmentManager.findFragmentById(R.id.viewPortGradeSearch).getClass()==GradeSearchListFragment.class)
                    {
                        GradeSearchListFragment f = (GradeSearchListFragment) fragmentManager.findFragmentById(R.id.viewPortGradeSearch);
                        f.performSearch(v.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        /*fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = fragmentManager.findFragmentById(R.id.viewPortGradeSearch);
                if(f.getClass() == GradeSearchListFragment.class)
                {
                    GradeSearchListFragment ff = (GradeSearchListFragment) f;
                    ff.performSearch(searchBox.getText().toString());
                }
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String action, Object params) {
        if(action.equals("NAVIGATE_TO_GRADE_DETAILS"))
        {
            Grade g = (Grade) params;
            final FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            GradeDetailFragment gradeDetailFragment = GradeDetailFragment.newInstance(new Gson().toJson(g));
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(R.id.viewPortGradeSearch, gradeDetailFragment, "GRADE_DETAIL_FRAGMENT_JR");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
