package io.keepcoding.guedr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    protected static String TAG = MainActivity.class.getCanonicalName();

    protected Button changeToStone;
    protected Button changeToDonkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vamos a obtener una referencia al ImageView del offline image
        final ImageView offlineImage = (ImageView) findViewById(R.id.offline_weather_image);

        changeToStone = (Button) findViewById(R.id.change_stone_system);

        // Esto es muy raro que lo hagamos, pero te puede ayudar a entender
        // qué son las clases anónimas en realidad
        changeToStone.setOnClickListener(new StoneButtonListener(offlineImage));

        changeToDonkey = (Button) findViewById(R.id.change_donkey_system);

        // Esto es lo que más probablemente termines haciendo en tu código
        changeToDonkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Me han pedido burro");
                offlineImage.setImageResource(R.drawable.offline_weather2);
            }
        });


        Log.v(TAG, "Hola Amundio, he pasado por onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, "Nos han llamado a onSaveInstanceState");
        outState.putString("clave", "valor");
    }


}

class StoneButtonListener implements View.OnClickListener {

    private final ImageView offlineImage;

    public StoneButtonListener(ImageView offlineImage) {
        this.offlineImage = offlineImage;
    }

    @Override
    public void onClick(View v) {
        Log.v("Lo que sea", "Me han pedido piedra");
        offlineImage.setImageResource(R.drawable.offline_weather);
    }
}
