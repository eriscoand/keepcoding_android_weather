package io.keepcoding.guedr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import io.keepcoding.guedr.R;
import io.keepcoding.guedr.model.Forecast;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.ForecastViewHolder>{

    private LinkedList<Forecast> mForecast;
    private boolean mShowCelcius;
    private View.OnClickListener mOnClickListener;

    public ForecastRecyclerViewAdapter(LinkedList<Forecast> forecast, boolean showCelcius){
        super();
        mForecast = forecast;
        mShowCelcius = showCelcius;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_forecast,parent,false);

        view.setOnClickListener(mOnClickListener);

        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        holder.bindForecast(mForecast.get(position), mShowCelcius);
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    protected class ForecastViewHolder extends RecyclerView.ViewHolder {

        private View mRoot;
        private final ImageView mForecastImage;
        private final TextView mMaxTempText;
        private final TextView mMinTempText;
        private final TextView mHumidityText;
        private final TextView mForecastDescriptionText;

        public ForecastViewHolder(View itemView) {
            super(itemView);

            mRoot = itemView;

            mForecastImage = (ImageView) itemView.findViewById(R.id.forecast_image);
            mMaxTempText = (TextView) itemView.findViewById(R.id.max_temp);
            mMinTempText = (TextView) itemView.findViewById(R.id.min_temp);
            mHumidityText = (TextView) itemView.findViewById(R.id.humidity);
            mForecastDescriptionText = (TextView) itemView.findViewById(R.id.forecast_description);
        }

        public void bindForecast(Forecast forecast, boolean showCelsius){

            // Calculamos las temperaturas en función de las unidades
            float maxTemp = 0;
            float minTemp = 0;
            String unitsToShow = null;
            if (showCelsius) {
                maxTemp = forecast.getMaxTemp(Forecast.CELSIUS);
                minTemp = forecast.getMinTemp(Forecast.CELSIUS);
                unitsToShow = "ºC";
            }
            else {
                maxTemp = forecast.getMaxTemp(Forecast.FARENHEIT);
                minTemp = forecast.getMinTemp(Forecast.FARENHEIT);
                unitsToShow = "F";
            }

            Context context = mRoot.getContext();

            // Actualizamos la vista con el modelo
            mForecastImage.setImageResource(forecast.getIcon());
            mMaxTempText.setText(context.getString(R.string.max_temp_format, maxTemp, unitsToShow));
            mMinTempText.setText(context.getString(R.string.min_temp_format, minTemp, unitsToShow));
            mHumidityText.setText(context.getString(R.string.humidity_format, forecast.getHumidity()));
            mForecastDescriptionText.setText(forecast.getDescription());

        }

}

}
