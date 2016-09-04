package xyz.jcdc.projectnoah.adapter;

/**
 * Created by jcdc on 8/9/2016.
 */

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.jcdc.projectnoah.Constants;
import xyz.jcdc.projectnoah.R;
import xyz.jcdc.projectnoah.objects.DrawerItem;
import xyz.jcdc.projectnoah.objects.Layer;

public class DrawerAdapter  extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private ArrayList<DrawerItem> drawerItems;
    private ArrayList<Layer> layers;

    private int selectedItem = -1;

    private final int VIEW_ITEM = 0;
    private final int VIEW_HEADER = 1;
    private final int VIEW_WEATHER = 2;
    private final int VIEW_SENSORS = 3;


    private SparseBooleanArray expandState = new SparseBooleanArray();

    private OnDrawerItemClickedListener onDrawerItemClickedListener;

    public void setOnDrawerItemClickedListener(OnDrawerItemClickedListener onDrawerItemClickedListener) {
        this.onDrawerItemClickedListener = onDrawerItemClickedListener;
    }

    public interface OnDrawerItemClickedListener{
        void onDrawerItemClicked(String category, String action);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class ItemViewHolder extends ViewHolder{
        TextView textView;
        ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
        }
    }

    public class WeatherViewHolder extends ViewHolder{
        TextView textView;
        ImageView imageView;

        LinearLayout subItems;
        LinearLayout item;

        TextView rowContour, rowDoppler, rowSatellite, rowWeatherOutlook;
        LinearLayout subItemsContour, subItemsDoppler, subItemsSatellite, subItemsWeatherOutlook;

        TextView itemContour_1, itemContour_3, itemContour_6, itemContour_12, itemContour_24
                , itemContour_temperature, itemContour_pressure, itemContour_humidity;

        TextView itemDopplerBaguio, itemDopplerSubic, itemDopplerTagaytay, itemDopplerCebu, itemDopplerHinatuan
                , itemDopplerTampakan, itemDopplerAparri, itemDopplerVirac, itemDopplerBaler;

        TextView itemSatelitteHimawari, itemSatelitteGSMAP1, itemSatelitteGSMAP3, itemSatelitteGSMAP6, itemSatelitteGSMAP12;

        TextView itemWeatherForecast_4;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);

            subItems = (LinearLayout) itemView.findViewById(R.id.sub_items);
            item = (LinearLayout) itemView.findViewById(R.id.item);

            rowContour = (TextView) itemView.findViewById(R.id.rowContour);
            subItemsContour = (LinearLayout) itemView.findViewById(R.id.sub_items_contour);

            rowDoppler = (TextView) itemView.findViewById(R.id.rowDoppler);
            subItemsDoppler = (LinearLayout) itemView.findViewById(R.id.sub_items_doppler);

            rowSatellite = (TextView) itemView.findViewById(R.id.rowSatellite);
            subItemsSatellite = (LinearLayout) itemView.findViewById(R.id.sub_items_satellite);

            rowWeatherOutlook = (TextView) itemView.findViewById(R.id.rowWeatherOutlook);
            subItemsWeatherOutlook = (LinearLayout) itemView.findViewById(R.id.sub_items_weather_outlook);

            itemContour_1 = (TextView) itemView.findViewById(R.id.rowContour1);
            itemContour_3 = (TextView) itemView.findViewById(R.id.rowContour3);
            itemContour_6 = (TextView) itemView.findViewById(R.id.rowContour6);
            itemContour_12 = (TextView) itemView.findViewById(R.id.rowContour12);
            itemContour_24 = (TextView) itemView.findViewById(R.id.rowContour24);
            itemContour_temperature = (TextView) itemView.findViewById(R.id.rowContourTemperature);
            itemContour_pressure = (TextView) itemView.findViewById(R.id.rowContourPressure);
            itemContour_humidity = (TextView) itemView.findViewById(R.id.rowContourHumidity);

            itemContour_1.setTag(Constants.ACTION_WEATHER_CONTOUR_1);
            itemContour_3.setTag(Constants.ACTION_WEATHER_CONTOUR_3);
            itemContour_6.setTag(Constants.ACTION_WEATHER_CONTOUR_6);
            itemContour_12.setTag(Constants.ACTION_WEATHER_CONTOUR_12);
            itemContour_24.setTag(Constants.ACTION_WEATHER_CONTOUR_24);
            itemContour_temperature.setTag(Constants.ACTION_WEATHER_CONTOUR_TEMPERATURE);
            itemContour_pressure.setTag(Constants.ACTION_WEATHER_CONTOUR_PRESSURE);
            itemContour_humidity.setTag(Constants.ACTION_WEATHER_CONTOUR_HUMIDITY);

            itemDopplerAparri = (TextView) itemView.findViewById(R.id.rowDopplerAparri);
            itemDopplerBaguio = (TextView) itemView.findViewById(R.id.rowDopplerBaguio);
            itemDopplerBaler = (TextView) itemView.findViewById(R.id.rowDopplerBaler);
            itemDopplerCebu = (TextView) itemView.findViewById(R.id.rowDopplerCebu);
            itemDopplerHinatuan = (TextView) itemView.findViewById(R.id.rowDopplerHinatuan);
            itemDopplerSubic = (TextView) itemView.findViewById(R.id.rowDopplerSubic);
            itemDopplerTagaytay = (TextView) itemView.findViewById(R.id.rowDopplerTagaytay);
            itemDopplerTampakan = (TextView) itemView.findViewById(R.id.rowDopplerTampakan);
            itemDopplerVirac = (TextView) itemView.findViewById(R.id.rowDopplerVirac);

            itemDopplerAparri.setTag(Constants.ACTION_WEATHER_DOPPLER_APARRI);
            itemDopplerBaguio.setTag(Constants.ACTION_WEATHER_DOPPLER_BAGUIO);
            itemDopplerBaler.setTag(Constants.ACTION_WEATHER_DOPPLER_BALER);
            itemDopplerCebu.setTag(Constants.ACTION_WEATHER_DOPPLER_CEBU);
            itemDopplerHinatuan.setTag(Constants.ACTION_WEATHER_DOPPLER_HINATAUAN);
            itemDopplerSubic.setTag(Constants.ACTION_WEATHER_DOPPLER_SUBIC);
            itemDopplerTagaytay.setTag(Constants.ACTION_WEATHER_DOPPLER_TAGAYTAY);
            itemDopplerTampakan.setTag(Constants.ACTION_WEATHER_DOPPLER_TAMPAKAN);
            itemDopplerVirac.setTag(Constants.ACTION_WEATHER_DOPPLER_VIRAC);

            itemSatelitteHimawari = (TextView) itemView.findViewById(R.id.rowSatelliteHimawari);
            itemSatelitteGSMAP1 = (TextView) itemView.findViewById(R.id.rowSatelliteGSMAP1);
            itemSatelitteGSMAP3 = (TextView) itemView.findViewById(R.id.rowSatelliteGSMAP3);
            itemSatelitteGSMAP6 = (TextView) itemView.findViewById(R.id.rowSatelliteGSMAP6);
            itemSatelitteGSMAP12 = (TextView) itemView.findViewById(R.id.rowSatelliteGSMAP12);

            itemSatelitteHimawari.setTag(Constants.ACTION_WEATHER_SATELLITE_HIMAWARI);
            itemSatelitteGSMAP1.setTag(Constants.ACTION_WEATHER_SATELLITE_GSMAP_1);
            itemSatelitteGSMAP3.setTag(Constants.ACTION_WEATHER_SATELLITE_GSMAP_3);
            itemSatelitteGSMAP6.setTag(Constants.ACTION_WEATHER_SATELLITE_GSMAP_6);
            itemSatelitteGSMAP12.setTag(Constants.ACTION_WEATHER_SATELLITE_GSMAP_12);

            itemWeatherForecast_4 = (TextView) itemView.findViewById(R.id.rowWeatherRainForecast_4);

            itemWeatherForecast_4.setTag(Constants.ACTION_WEATHER_FORECAST_4);
        }
    }

    public class SensorsViewHolder extends ViewHolder {
        TextView textView;
        ImageView imageView;

        LinearLayout subItems;
        LinearLayout item;

        TextView rowRainGauges, rowStreamGauges, rowRainAndStreamGauges, rowWeatherStations, rowTideLevels;

        public SensorsViewHolder(View v) {
            super(v);
            textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);

            subItems = (LinearLayout) itemView.findViewById(R.id.sub_items);
            item = (LinearLayout) itemView.findViewById(R.id.item);

            rowRainGauges = (TextView) itemView.findViewById(R.id.rowRainGauges);
            rowStreamGauges = (TextView) itemView.findViewById(R.id.rowStreamGauges);
            rowRainAndStreamGauges = (TextView) itemView.findViewById(R.id.rowRainAndStreamGauges);
            rowTideLevels = (TextView) itemView.findViewById(R.id.rowTideLevels);
            rowWeatherStations = (TextView) itemView.findViewById(R.id.rowWeatherStations);

            rowRainGauges.setTag(Constants.ACTION_SENSORS_RAIN_GAUGE);
            rowStreamGauges.setTag(Constants.ACTION_SENSORS_STREAM_GAUGE);
            rowRainAndStreamGauges.setTag(Constants.ACTION_SENSORS_RAIN_AND_STREAM_GAUGE);
            rowTideLevels.setTag(Constants.ACTION_SENSORS_TIDE_LEVELS);
            rowWeatherStations.setTag(Constants.ACTION_SENSORS_WEATHER);
        }
    }

    public class HeaderViewHolder extends ViewHolder{
        TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
        }
    }

    public DrawerAdapter(ArrayList<DrawerItem> drawerItems, ArrayList<Layer> layers){
        this.drawerItems = drawerItems;
        this.layers = layers;

        for (int i = 0; i < drawerItems.size(); i++) {
            expandState.append(i, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return VIEW_HEADER;

        if(position == 1)
            return VIEW_WEATHER;

        if(position == 2)
            return VIEW_SENSORS;

        return VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return drawerItems.size() + 1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case VIEW_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer,parent,false); //Inflating the layout
                return new ItemViewHolder(v);

            case VIEW_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_drawer,parent,false); //Inflating the layout
                return new HeaderViewHolder(v);

            case VIEW_WEATHER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_weather,parent,false); //Inflating the layout
                return new WeatherViewHolder(v);

            case VIEW_SENSORS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sensors,parent,false); //Inflating the layout
                return new SensorsViewHolder(v);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if(holder.getItemViewType() == VIEW_ITEM){
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            DrawerItem drawerItem = drawerItems.get(position - 1);

            itemViewHolder.textView.setText(drawerItem.getTitle());
            itemViewHolder.imageView.setImageResource(drawerItem.getIcon());

        }else if(holder.getItemViewType() == VIEW_WEATHER){
            final WeatherViewHolder weatherViewHolder = (WeatherViewHolder) holder;
            DrawerItem drawerItem = drawerItems.get(position - 1);

            weatherViewHolder.textView.setText(drawerItem.getTitle());
            weatherViewHolder.imageView.setImageResource(drawerItem.getIcon());

            weatherViewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weatherViewHolder.subItems.setVisibility(weatherViewHolder.subItems.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                    if(weatherViewHolder.subItems.getVisibility() == View.GONE){
                        weatherViewHolder.subItemsContour.setVisibility(View.GONE);
                        weatherViewHolder.subItemsDoppler.setVisibility(View.GONE);
                        weatherViewHolder.subItemsSatellite.setVisibility(View.GONE);
                        weatherViewHolder.subItemsWeatherOutlook.setVisibility(View.GONE);
                    }

                }
            });

            weatherViewHolder.rowContour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weatherViewHolder.subItemsContour.setVisibility(weatherViewHolder.subItemsContour.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            //Contour OnClicks
            weatherViewHolder.itemContour_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_1);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_3);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_6);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_12);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_24);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_temperature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_TEMPERATURE);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_pressure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_PRESSURE);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_humidity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_CONTOUR, Constants.ACTION_WEATHER_CONTOUR_HUMIDITY);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });


            weatherViewHolder.rowDoppler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weatherViewHolder.subItemsDoppler.setVisibility(weatherViewHolder.subItemsDoppler.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            //Doppler onClicks
            weatherViewHolder.itemDopplerAparri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_APARRI);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerVirac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_VIRAC);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerTampakan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_TAMPAKAN);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerTagaytay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_TAGAYTAY);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerSubic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_SUBIC);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerBaguio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_BAGUIO);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerBaler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_BALER);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerCebu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_CEBU);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.itemDopplerHinatuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_DOPPLER, Constants.ACTION_WEATHER_DOPPLER_HINATAUAN);
                    isDopplerLayerActive(weatherViewHolder.subItemsDoppler);
                }
            });

            weatherViewHolder.rowSatellite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weatherViewHolder.subItemsSatellite.setVisibility(weatherViewHolder.subItemsSatellite.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            //SatelliteOnClick

            weatherViewHolder.itemSatelitteHimawari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_SATELLITE, Constants.ACTION_WEATHER_SATELLITE_HIMAWARI);
                    isSatelliteLayerActive(weatherViewHolder.subItemsSatellite);
                }
            });

            weatherViewHolder.itemSatelitteGSMAP1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_SATELLITE, Constants.ACTION_WEATHER_SATELLITE_GSMAP_1);
                    isSatelliteLayerActive(weatherViewHolder.subItemsSatellite);
                }
            });

            weatherViewHolder.itemSatelitteGSMAP3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_SATELLITE, Constants.ACTION_WEATHER_SATELLITE_GSMAP_3);
                    isSatelliteLayerActive(weatherViewHolder.subItemsSatellite);
                }
            });

            weatherViewHolder.itemSatelitteGSMAP6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_SATELLITE, Constants.ACTION_WEATHER_SATELLITE_GSMAP_6);
                    isSatelliteLayerActive(weatherViewHolder.subItemsSatellite);
                }
            });

            weatherViewHolder.itemSatelitteGSMAP12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_SATELLITE, Constants.ACTION_WEATHER_SATELLITE_GSMAP_12);
                    isSatelliteLayerActive(weatherViewHolder.subItemsSatellite);
                }
            });


            weatherViewHolder.rowWeatherOutlook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weatherViewHolder.subItemsWeatherOutlook.setVisibility(weatherViewHolder.subItemsWeatherOutlook.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            //Weather Forecast onClick
            weatherViewHolder.itemWeatherForecast_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_WEATHER_FORECAST, Constants.ACTION_WEATHER_FORECAST_4);
                    isWeatherForecastActive(weatherViewHolder.subItemsWeatherOutlook);
                }
            });

        }else if(holder.getItemViewType() == VIEW_SENSORS) {
            final SensorsViewHolder sensorsViewHolder = (SensorsViewHolder) holder;
            DrawerItem drawerItem = drawerItems.get(position - 1);

            sensorsViewHolder.textView.setText(drawerItem.getTitle());
            sensorsViewHolder.imageView.setImageResource(drawerItem.getIcon());

            sensorsViewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sensorsViewHolder.subItems.setVisibility(sensorsViewHolder.subItems.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                }
            });

            //Sensor onClicks
            sensorsViewHolder.rowRainGauges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_SENSORS, Constants.ACTION_SENSORS_RAIN_GAUGE);

                }
            });

            sensorsViewHolder.rowWeatherStations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_SENSORS, Constants.ACTION_SENSORS_WEATHER);

                }
            });

            sensorsViewHolder.rowTideLevels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_SENSORS, Constants.ACTION_SENSORS_TIDE_LEVELS);

                }
            });

            sensorsViewHolder.rowRainAndStreamGauges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_SENSORS, Constants.ACTION_SENSORS_RAIN_AND_STREAM_GAUGE);

                }
            });

            sensorsViewHolder.rowStreamGauges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.LAYER_SENSORS, Constants.ACTION_SENSORS_STREAM_GAUGE);

                }
            });

        }else if(holder.getItemViewType() == VIEW_HEADER){
            final HeaderViewHolder addProfileViewHolder = (HeaderViewHolder) holder;

        }


    }

    private void isContourLayerActive(LinearLayout parent){
        for (int x=0; x<parent.getChildCount(); x++){
            TextView v = (TextView) parent.getChildAt(x);
            v.setTextColor(Color.BLACK);
            for (Layer layer : layers){
                Log.d("DrawerAdapter", layer.getAction());
                if(layer.getAction().equals(v.getTag())){
                    v.setTextColor(Color.RED);
                }
            }
        }
    }

    private void isDopplerLayerActive(LinearLayout parent){
        for (int x=0; x<parent.getChildCount(); x++){
            TextView v = (TextView) parent.getChildAt(x);
            v.setTextColor(Color.BLACK);
            for (Layer layer : layers){
                Log.d("DrawerAdapter", layer.getAction());
                if(layer.getAction().equals(v.getTag())){
                    v.setTextColor(Color.RED);
                }
            }
        }
    }

    private void isSatelliteLayerActive(LinearLayout parent){
        for (int x=0; x<parent.getChildCount(); x++){
            TextView v = (TextView) parent.getChildAt(x);
            v.setTextColor(Color.BLACK);
            for (Layer layer : layers){
                Log.d("DrawerAdapter", layer.getAction());
                if(layer.getAction().equals(v.getTag())){
                    v.setTextColor(Color.RED);
                }
            }
        }
    }

    private void isWeatherForecastActive(LinearLayout parent){
        for (int x=0; x<parent.getChildCount(); x++){
            TextView v = (TextView) parent.getChildAt(x);
            v.setTextColor(Color.BLACK);
            for (Layer layer : layers){
                Log.d("DrawerAdapter", layer.getAction());
                if(layer.getAction().equals(v.getTag())){
                    v.setTextColor(Color.RED);
                }
            }
        }
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
