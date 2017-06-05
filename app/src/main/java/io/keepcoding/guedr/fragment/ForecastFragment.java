package io.keepcoding.guedr.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import io.keepcoding.guedr.activity.DetailForecastActivity;
import io.keepcoding.guedr.adapter.ForecastRecyclerViewAdapter;
import io.keepcoding.guedr.model.City;
import io.keepcoding.guedr.model.Forecast;
import io.keepcoding.guedr.R;
import io.keepcoding.guedr.activity.SettingsActivity;

public class ForecastFragment extends Fragment {

    public static final String PREFERENCE_SHOW_CELSIUS = "showCelsius";
    private static final String ARG_CITY = "showCity";
    private static final int REQUEST_UNITS = 1;
    private static final int LOADING_VIEW_INDEX = 0;
    private static final int FORECAST_VIEW_INDEX = 1;

    protected static String TAG = ForecastFragment.class.getCanonicalName();


    protected boolean mShowCelsius = true;
    private View mRoot;
    private City mCity;
    private RecyclerView mList;

    public static ForecastFragment newInstance(City city){
        ForecastFragment fragment = new ForecastFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_CITY,city);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mCity = (City) getArguments().getSerializable(ARG_CITY);
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_forecast, container, false);

        // Recuperamos el valor que habíamos guardado para mShowCelsius en disco
        mShowCelsius = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREFERENCE_SHOW_CELSIUS, true);

        mList = (RecyclerView) mRoot.findViewById(R.id.forecast_list);
        //mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.setLayoutManager(new GridLayoutManager(getActivity(),getResources().getInteger(R.integer.recycler_columns)));


        updateForecast();

        return mRoot;
    }

    private void updateForecast() {

        final LinkedList<Forecast> forecast = mCity.getForecast();

        final ViewSwitcher viewSwitcher = (ViewSwitcher) mRoot.findViewById(R.id.forecast_view_switcher);
        viewSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        viewSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);

        if(forecast == null){
            AsyncTask<City, Integer, LinkedList<Forecast>> weatherDownloader = new AsyncTask<City, Integer, LinkedList<Forecast>>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    viewSwitcher.setDisplayedChild(LOADING_VIEW_INDEX);
                }

                @Override
                protected LinkedList<Forecast> doInBackground(City... params) {
                    LinkedList<Forecast> forecast = downloadForecast(params[0]);
                    return forecast;
                };

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                }

                @Override
                protected void onPostExecute(LinkedList<Forecast> forecast) {
                    super.onPostExecute(forecast);

                    if(forecast != null){
                        mCity.setForecast(forecast);
                        updateForecast();
                        viewSwitcher.setDisplayedChild(FORECAST_VIEW_INDEX);
                    }else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(R.string.error_title);
                        alertDialog.setMessage(R.string.error_message);
                        alertDialog.setPositiveButton(R.string.error_reload, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateForecast();
                            }
                        });
                        alertDialog.show();
                    }


                }
            };
            weatherDownloader.execute(mCity);
            return;
        }

        ForecastRecyclerViewAdapter adapter = new ForecastRecyclerViewAdapter(forecast, mShowCelsius);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mList.getChildAdapterPosition(v);
                Forecast forecastDetail = forecast.get(position);
                Intent intent = new Intent(getActivity(), DetailForecastActivity.class);
                intent.putExtra(DetailForecastActivity.EXTRA_FORECAST, forecastDetail);
                intent.putExtra(DetailForecastActivity.EXTRA_SHOWCELSIUS, mShowCelsius);

                Bundle animationOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),v,getString(R.string.transition_to_detail)).toBundle();

                startActivity(intent, animationOptions);
            }
        });


        mList.setAdapter(adapter);
    }

    private LinkedList<Forecast> downloadForecast(City city){
        URL url = null;
        InputStream input = null;

        try{
            url = new URL(String.format("http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&lang=sp&units=metric&appid=a5de63d7f8b32c8ba1f09fa7481510b4",mCity.getName()));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();

            byte data[] = new byte[1024];
            int downloadBytes;
            input = con.getInputStream();
            StringBuilder sb = new StringBuilder();
            while((downloadBytes = input.read(data))!= -1){
                sb.append(new String(data,0,downloadBytes));
            }

            JSONObject jsonRoot = new JSONObject(sb.toString());
            JSONArray list = jsonRoot.getJSONArray("list");

            LinkedList<Forecast> forecasts = new LinkedList<>();

            for(int i = 0; i < list.length(); i++){

                JSONObject today = list.getJSONObject(i);
                float max = (float) today.getJSONObject("temp").getDouble("max");
                float min = (float) today.getJSONObject("temp").getDouble("min");
                float humidity = (float) today.getDouble("humidity");
                String description = today.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconString = today.getJSONArray("weather").getJSONObject(0).getString("icon");

                iconString = iconString.substring(0,iconString.length() - 1);
                int iconInt = Integer.parseInt(iconString);
                int iconResource = R.drawable.ico_01;
                switch(iconInt){
                    case 1: iconResource = R.drawable.ico_01; break;
                    case 2: iconResource = R.drawable.ico_02; break;
                    case 3: iconResource = R.drawable.ico_03; break;
                    case 4: iconResource = R.drawable.ico_04; break;
                    case 9: iconResource = R.drawable.ico_09; break;
                    case 10: iconResource = R.drawable.ico_10; break;
                    case 11: iconResource = R.drawable.ico_11; break;
                    case 13: iconResource = R.drawable.ico_13; break;
                    case 50: iconResource = R.drawable.ico_50; break;
                }
                forecasts.add(new Forecast(max,min,humidity,description,iconResource));
            }

            return forecasts;


        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean superReturn = super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_show_settings) {
            Log.v(TAG, "Se ha pulsado la opción de ajustes");

            // Creamos el intent explícito para abrir la pantalla de ajustes
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            // Pasamos datos a la siguiente actividad: las unidades
            if (mShowCelsius) {
                settingsIntent.putExtra(SettingsActivity.EXTRA_UNITS, R.id.celsius_rb);
            }
            else {
                settingsIntent.putExtra(SettingsActivity.EXTRA_UNITS, R.id.farenheit_rb);
            }

            // Esto lo usaríamos si la actividad destino no devolviera datos
            //startActivity(settingsIntent);

            // Esto lo usamos porque SettingsActivity devuelve datos que nos interesan
            startActivityForResult(settingsIntent, REQUEST_UNITS);

            return true;
        }

        return superReturn;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UNITS) {
            // Estamos volviendo de la pantalla de SettingsActivity
            // Miro cómo ha ido el resultado
            if (resultCode == Activity.RESULT_OK) {
                final boolean oldShowCelsius = mShowCelsius; // Por si acaso el usuario se arrepiente
                String snackBarText = null;

                // Todo ha ido bien, hago caso a los datos de entrada (la opción por defecto aquí es absurda... pero hay que rellenarla)
                int optionSelected = data.getIntExtra(SettingsActivity.EXTRA_UNITS, R.id.farenheit_rb);
                if (optionSelected == R.id.farenheit_rb) {
//                    Toast.makeText(this, "Se ha seleccionado Farenheit", Toast.LENGTH_LONG).show();
                    mShowCelsius = false;
                    snackBarText = getString(R.string.farenheit_selected);
                }
                else {
//                    Toast.makeText(this, "Se ha seleccionado Celsius", Toast.LENGTH_LONG).show();
                    snackBarText = getString(R.string.celsius_selected);
                    mShowCelsius = true;
                }

                if(getView() != null){
                    Snackbar.make(getView(), snackBarText, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Deshacemos los cambios
                                    mShowCelsius = oldShowCelsius;
                                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                                            .edit()
                                            .putBoolean(PREFERENCE_SHOW_CELSIUS, mShowCelsius)
                                            .apply();
                                    updateForecast();
                                }
                            })
                            .show();
                }

                // Persistimos las preferencias del usuario respecto a las unidades
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putBoolean(PREFERENCE_SHOW_CELSIUS, mShowCelsius);
//                editor.apply();
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putBoolean(PREFERENCE_SHOW_CELSIUS, mShowCelsius)
                        .apply();

                updateForecast();
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                // No hago nada porque el usuario ha cancelado la selección de unidades
            }

        }
    }
}

