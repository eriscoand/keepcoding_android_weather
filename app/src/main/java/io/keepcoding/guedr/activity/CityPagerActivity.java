package io.keepcoding.guedr.activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.keepcoding.guedr.R;
import io.keepcoding.guedr.fragment.CityPagerFragment;

public class CityPagerActivity extends AppCompatActivity {

    public static final String EXTRA_CITY_INDEX = "EXTRA_CITY_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_pager);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        int cityIndex = getIntent().getIntExtra(EXTRA_CITY_INDEX, 0);

        FragmentManager fm = getFragmentManager();
        if(fm.findFragmentById(R.id.view_pager_fragment) == null){
            CityPagerFragment fragment = CityPagerFragment.newInstance(cityIndex);
            fm.beginTransaction().add(R.id.view_pager_fragment, fragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean superValue = super.onOptionsItemSelected(item);

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return superValue;
    }
}
