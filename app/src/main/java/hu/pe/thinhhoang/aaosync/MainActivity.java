package hu.pe.thinhhoang.aaosync;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.database.grades.Grade;
import hu.pe.thinhhoang.aaosync.service.SyncHelper;
import hu.pe.thinhhoang.aaosync.service.SyncOrchestrator;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.sync.ExamsSync;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;
import com.crashlytics.android.Crashlytics;

import hu.pe.thinhhoang.aaosync.utils.Randomizer;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InteractionInterface {

    private NavigationView navigationView;
    private boolean WELCOME_ACTIVITY_STARTED_FLAG = false;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;
    public boolean APP_STARTUP_FLAG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // IMPORTANT TO DISPLAY A "BACK" BUTTON WHEN NAVIGATING AWAY FROM HOME SCREEN
        } catch (NullPointerException e) {
            Log.v("AAOSync-D","An error occurred while performing actions on the Action Bar");
            e.printStackTrace();
        }

        // getSupportActionBar().setElevation(0);

        Settings.setContext(getApplicationContext());

        Settings.setWhitestarLocation(Randomizer.randInt(1,Settings.countries.length-1));

        if(Settings.getMSSV().equals("undef"))
        {
            WELCOME_ACTIVITY_STARTED_FLAG=true;
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
        else {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            // Schedule the service to run...
            if(Settings.getAllowSync() && !Settings.getMSSV().equals("undef"))
            {
                // Notify upcoming exams
                ExamsSync examsSync = new ExamsSync();
                examsSync.IssueReminder(this);
                examsSync=null; // For garbage collection
                // Schedule the alarm
                SyncHelper.cancelAlarm(this.getApplicationContext());
                SyncHelper.scheduleAlarm(this.getApplicationContext());
            }

            // Load the grades fragment and perform sync if settings allow
            navigateToGrades(true);

            // Handle the Toolbar Home - Back action - IMPORTANT FOR NAVIGATION
            final View.OnClickListener originalToolbarListener = toggle.getToolbarNavigationClickListener();
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        toggle.setDrawerIndicatorEnabled(false);
                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    } else {
                        toggle.setDrawerIndicatorEnabled(true);
                        toggle.setToolbarNavigationClickListener(originalToolbarListener);
                    }
                }
            });

//            forceCrash();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: REMOVE THIS IN PRODUCTION!

//        Settings.setContext(this.getApplicationContext());
//        Settings.dumpLastSync();

        /*TextView studentIdent = (TextView) findViewById(R.id.studentIdent);
        studentIdent.setText(Settings.getMSSV().toLowerCase()+"@hcmut.edu.vn");*/

        if(!WELCOME_ACTIVITY_STARTED_FLAG)
        {
            View header=navigationView.getHeaderView(0);

            TextView studentIdent = (TextView) header.findViewById(R.id.studentIdent);
            studentIdent.setText(Settings.getMSSV() + "@hcmut.edu.vn");
        }

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = mDrawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_grades) {
            navigateToGrades(false);
        } else if (id==R.id.nav_timetable)
        {
            navigateToTimeTable();
        } else if (id==R.id.nav_exams)
        {
            navigateToExams();
        }
        else if (id==R.id.nav_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id==R.id.nav_about)
        {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = mDrawerLayout;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String action, Object params) {
        /* CHANGE FRAGMENTS BY REQUESTS FROM FRAGMENT */
        if(action.equals("REQUEST_FRAGMENT_CHANGE_TO_GRADE_DETAILS"))
        {
            navigateToGradeDetails((Grade) params);
        }
    }

    /*** THE FOLLOWING METHODS WILL SUPPORT FRAGMENT REPLACING AND CREATING
     * FROM NAVIGATION DRAWER AND FROM WITHIN THE FRAGMENT
     */

    private void navigateToGrades(boolean RefreshOnStart)
    {
        // TODO: Show the grades
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.mainViewPort);
        if (f!=null && !f.isDetached()) {
            // User navigates back to the home screen, the next back will quit the app
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GradesMasterFragment gradesFragment = new GradesMasterFragment();
        // if (RefreshOnStart) gradesFragment.FLAG_ALLOW_REFRESH=true; else gradesFragment.FLAG_ALLOW_REFRESH=false;

        fragmentTransaction.replace(R.id.mainViewPort, gradesFragment, "GRADES_MASTER_FRAGMENT");
        fragmentTransaction.commit();

        Log.v("AAOSync_N","Fragment replace committed (GRADES_MASTER_FRAGMENT)");

    }

    private void navigateToTimeTable()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.mainViewPort);
        if (f!=null && !f.isDetached()) {
            // User navigates back to the home screen, the next back will quit the app
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TimeTableFragment timeTableFragment = TimeTableFragment.newInstance();
        // if (RefreshOnStart) gradesFragment.FLAG_ALLOW_REFRESH=true; else gradesFragment.FLAG_ALLOW_REFRESH=false;

        fragmentTransaction.replace(R.id.mainViewPort, timeTableFragment, "TIME_TABLE_FRAGMENT");
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.v("AAOSync_N","Fragment replace committed (TIME_TABLE_FRAGMENT)");
    }

    private void navigateToExams()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.mainViewPort);
        if (f!=null && !f.isDetached()) {
            // User navigates back to the home screen, the next back will quit the app
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ExamsFragment examsFragment = ExamsFragment.newInstance();
        // if (RefreshOnStart) gradesFragment.FLAG_ALLOW_REFRESH=true; else gradesFragment.FLAG_ALLOW_REFRESH=false;

        fragmentTransaction.replace(R.id.mainViewPort, examsFragment, "EXAMS_FRAGMENT");
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.v("AAOSync_N","Fragment replace committed (EXAMS_FRAGMENT)");
    }

    private void navigateToGradeDetails(Grade g)
    {
        // TODO: Show the grade details
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Fragment f = fragmentManager.findFragmentById(R.id.mainViewPort);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GradeDetailFragment gradesFragment = GradeDetailFragment.newInstance(new Gson().toJson(g));
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.mainViewPort, gradesFragment, "GRADE_DETAILS_FRAGMENT");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }


}
