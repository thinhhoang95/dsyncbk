package hu.pe.thinhhoang.aaosync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import hu.pe.thinhhoang.aaosync.service.SyncHelper;
import hu.pe.thinhhoang.aaosync.settings.Settings;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int rndCountries = Settings.getWhitestarLocation();
        if (SyncHelper.isNetworkAvailable(this))
        {
            TextView whitestar = (TextView) findViewById(R.id.whitestarVersion);
            whitestar.setText(whitestar.getText()+" tại "+Settings.countries[rndCountries]);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Thông tin");
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(),R.color.transparent ));
         collapsingToolbar.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary ));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                /*emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"aaosync@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Báo lỗi cho ứng dụng AAOSync");*/
                String uriText = "mailto:aaosync@gmail.com?subject=" + Uri.encode("Báo lỗi AAOSync") + "&body=";
                Uri uri = Uri.parse(uriText);
                emailIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(emailIntent, "Chọn một ứng dụng"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getBaseContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Connect facebook button
        ImageButton facebook = (ImageButton) findViewById(R.id.facebookButton);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                try{
                    getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1176586612370187"));
                } catch (Exception e)
                {
                    i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/AAOSyncMobile"));
                }
                startActivity( i );
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        if (itemId==android.R.id.home)
        {
            Log.i("AAOSyncNAV","Go home!");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
