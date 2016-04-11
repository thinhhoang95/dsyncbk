package hu.pe.thinhhoang.aaosync;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hu.pe.thinhhoang.aaosync.sync.GradesSync;
import hu.pe.thinhhoang.aaosync.utils.InteractionInterface;

public class DevActivity extends AppCompatActivity implements Button.OnClickListener, InteractionInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GradesFragment gradesFragment = new GradesFragment();
        fragmentTransaction.add(R.id.testFragment,gradesFragment,"GRADES_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        GradesSync syncer = new GradesSync();
        syncer.premierSync(this, new GradesSync.AfterSync() {
            @Override
            public void execute() {
                // Do nothing!
            }
        }, new GradesSync.AfterSyncError() {
            @Override
            public void execute() {
                // Do nothing!
            }
        });
    }

    @Override
    public void onFragmentInteraction(String action, Object params) {

    }
}
