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

    private SparseBooleanArray expandState = new SparseBooleanArray();

    private OnDrawerItemClickedListener onDrawerItemClickedListener;

    public void setOnDrawerItemClickedListener(OnDrawerItemClickedListener onDrawerItemClickedListener) {
        this.onDrawerItemClickedListener = onDrawerItemClickedListener;
    }

    public interface OnDrawerItemClickedListener{
        void onDrawerItemClicked(String action);
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

        TextView rowContour;
        LinearLayout subItemsContour;
        TextView itemContour_1, itemContour_3, itemContour_6, itemContour_12, itemContour_24
                , itemContour_temperature, itemContour_pressure, itemContour_humidity;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);

            subItems = (LinearLayout) itemView.findViewById(R.id.sub_items);
            item = (LinearLayout) itemView.findViewById(R.id.item);

            rowContour = (TextView) itemView.findViewById(R.id.rowContour);
            subItemsContour = (LinearLayout) itemView.findViewById(R.id.sub_items_contour);

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
                    }

                }
            });

            weatherViewHolder.rowContour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weatherViewHolder.subItemsContour.setVisibility(weatherViewHolder.subItemsContour.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            //Contour OnClicks
            weatherViewHolder.itemContour_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_1);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_3);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_6);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_12);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_24);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_temperature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_TEMPERATURE);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_pressure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_PRESSURE);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
                }
            });

            weatherViewHolder.itemContour_humidity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDrawerItemClickedListener.onDrawerItemClicked(Constants.ACTION_WEATHER_CONTOUR_HUMIDITY);
                    isContourLayerActive(weatherViewHolder.subItemsContour);
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
