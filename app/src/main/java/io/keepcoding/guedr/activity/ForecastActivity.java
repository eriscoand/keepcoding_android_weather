package io.keepcoding.guedr.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.keepcoding.guedr.R;
import io.keepcoding.guedr.fragment.CityListFragment;
import io.keepcoding.guedr.fragment.CityPagerFragment;
import io.keepcoding.guedr.model.Cities;
import io.keepcoding.guedr.model.City;

public class ForecastActivity extends AppCompatActivity implements CityListFragment.OnCitySelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        FragmentManager fm = getFragmentManager();

        if (findViewById(R.id.city_list_fragment) != null){
            if(fm.findFragmentById(R.id.city_list_fragment) == null){
                Cities cities = Cities.getInstance();
                CityListFragment fragment = CityListFragment.newInstance(cities.getCities());

                fm.beginTransaction().add(R.id.city_list_fragment, fragment).commit();
            }
        }

        if (findViewById(R.id.view_pager_fragment) != null){
            if(fm.findFragmentById(R.id.view_pager_fragment) == null){
                fm.beginTransaction().add(R.id.view_pager_fragment, CityPagerFragment.newInstance(0)).commit();
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(android.R.id.content), "Adding new City!", 3);
            }
        });

    }

    @Override
    public void onCitySelected(City city, int position){

        FragmentManager fm = getFragmentManager();
        CityPagerFragment cityPagerFragment = (CityPagerFragment) fm.findFragmentById(R.id.view_pager_fragment);

        if(cityPagerFragment != null){
            cityPagerFragment.moveToCity(position);
        }else{
            Intent intent = new Intent(this, CityPagerActivity.class);
            intent.putExtra(CityPagerActivity.EXTRA_CITY_INDEX,position);
            startActivity(intent);
        }

    }

}
