package xyz.jcdc.projectnoah.chance_of_rain.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import xyz.jcdc.projectnoah.R;
import xyz.jcdc.projectnoah.chance_of_rain.Data;
import xyz.jcdc.projectnoah.chance_of_rain.Location;

/**
 * Created by jcdc on 9/2/2016.
 */

public class ChanceOfRainAdapter  extends RecyclerView.Adapter<ChanceOfRainAdapter.ViewHolder> {

    private ArrayList<Data> datas;

    private final int VIEW_DATA = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class DataViewHolder extends ViewHolder{
        TextView percent_chance_of_rain;
        TextView chance_of_rain;
        ImageView icon;
        TextView time;

        public DataViewHolder(View itemView) {
            super(itemView);
            percent_chance_of_rain = (TextView) itemView.findViewById(R.id.percent_chance_of_rain);
            chance_of_rain = (TextView) itemView.findViewById(R.id.chance_of_rain);
            time = (TextView) itemView.findViewById(R.id.time);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    public ChanceOfRainAdapter(ArrayList<Data> datas){
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_DATA;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chance_of_rain,parent,false); //Inflating the layout
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final DataViewHolder dataViewHolder = (DataViewHolder) holder;

        Data data = datas.get(position);

        dataViewHolder.percent_chance_of_rain.setText(String.valueOf(data.getPercent_chance_of_rain()) + "%");
        dataViewHolder.chance_of_rain.setText(data.getChance_of_rain());
        dataViewHolder.time.setText(data.getTime());

        Glide
                .with(dataViewHolder.itemView.getContext())
                .load(data.getIcon())
                .centerCrop()
                .into(dataViewHolder.icon);
    }

    public ArrayList<Data> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<Data> datas) {
        this.datas = datas;
    }
}
