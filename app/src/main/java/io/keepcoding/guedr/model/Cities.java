package io.keepcoding.guedr.model;

import java.util.LinkedList;
import java.util.List;

import io.keepcoding.guedr.R;

public class Cities {

    private static Cities mInstance;

    private LinkedList<City> mCities;

    public static Cities getInstance(){
        if(mInstance == null){
            mInstance = new Cities();
        }
        return mInstance;
    }

    public Cities() {
        mCities = new LinkedList<>();
        mCities.add(new City("Canillo"));
        mCities.add(new City("Encamp"));
        mCities.add(new City("Ordino"));
        mCities.add(new City("La Massana"));
        mCities.add(new City("Andorra la Vella"));
        mCities.add(new City("Sant Julià de Lòria"));
        mCities.add(new City("Escaldes-Engordany"));
    }

    public City getCity(int index){
        return mCities.get(index);
    }

    public LinkedList<City> getCities(){
        return mCities;
    }

    public int getCount(){
        return mCities.size();
    }

}
