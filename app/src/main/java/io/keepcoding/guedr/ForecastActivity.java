package io.keepcoding.guedr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastActivity extends AppCompatActivity {
    protected static String TAG = ForecastActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Creamos el modelo de pega
        Forecast forecast = new Forecast(25, 10, 35, "Soleado con alguna nube", R.drawable.ico_01);
        setForecast(forecast);
    }

    private void setForecast(Forecast forecast) {
        // Accedemos a las vistas de la interfaz
        ImageView forecastImage = (ImageView) findViewById(R.id.forecast_image);
        TextView maxTemp = (TextView) findViewById(R.id.max_temp);
        TextView minTemp = (TextView) findViewById(R.id.min_temp);
        TextView humidity = (TextView) findViewById(R.id.humidity);
        TextView forecastDescription = (TextView) findViewById(R.id.forecast_description);

        // Actualizamos la vista con el modelo
        forecastImage.setImageResource(forecast.getIcon());
        maxTemp.setText(getString(R.string.max_temp_format, forecast.getMaxTemp()));
        minTemp.setText(getString(R.string.min_temp_format, forecast.getMinTemp()));
        humidity.setText(getString(R.string.humidity_format, forecast.getHumidity()));
        forecastDescription.setText(forecast.getDescription());
    }


}
