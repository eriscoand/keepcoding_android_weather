package io.keepcoding.guedr.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;

import io.keepcoding.guedr.R;
import io.keepcoding.guedr.model.City;

public class CityListFragment extends Fragment {

    private static final String ARG_CITIES = "showCities";

    protected LinkedList<City> mCities;

    protected OnCitySelectedListener mOnCitySelectedListener;

    public static CityListFragment newInstance(LinkedList<City> mCities) {
        CityListFragment fragment = new CityListFragment();
        Bundle arguments = new Bundle();

        arguments.putSerializable(ARG_CITIES,mCities);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCities = (LinkedList<City>) getArguments().getSerializable(ARG_CITIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_city_list, container, false);

        ListView list = (ListView) root.findViewById(R.id.city_list);
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(getActivity(), android.R.layout.simple_list_item_1,mCities);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mOnCitySelectedListener != null){
                    City selectedCity = mCities.get(position);
                    mOnCitySelectedListener.onCitySelected(selectedCity, position);
                }
            }
        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof OnCitySelectedListener){
            mOnCitySelectedListener = (OnCitySelectedListener) getActivity();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnCitySelectedListener = null;
    }

    public interface OnCitySelectedListener {
        void onCitySelected(City city, int position);

    }

}
