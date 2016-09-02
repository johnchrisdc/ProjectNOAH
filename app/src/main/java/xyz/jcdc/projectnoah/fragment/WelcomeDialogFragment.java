package xyz.jcdc.projectnoah.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.projectnoah.R;
import xyz.jcdc.projectnoah.chance_of_rain.Data;
import xyz.jcdc.projectnoah.chance_of_rain.Location;
import xyz.jcdc.projectnoah.chance_of_rain.adapter.ChanceOfRainAdapter;

/**
 * Created by jcdc on 9/1/2016.
 */

public class WelcomeDialogFragment extends DialogFragment implements View.OnClickListener {

    private ArrayList<Location> locations;

    private RecyclerView dataRecyclerView;
    private ChanceOfRainAdapter chanceOfRainAdapter;

    private Button close;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_welcome_rotonda, container);
        Bundle args = getArguments();

        close = (Button) view.findViewById(R.id.close);
        close.setOnClickListener(this);

        dataRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        chanceOfRainAdapter = new ChanceOfRainAdapter(new ArrayList<Data>());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        dataRecyclerView.setLayoutManager(linearLayoutManager);
        dataRecyclerView.setAdapter(chanceOfRainAdapter);

        if(args.getSerializable("locations") != null){
            locations = (ArrayList<Location>) args.getSerializable("locations");

            for(Location location : locations){
                if(location.getLocation().equals("Manila, Metro Manila")){
                    chanceOfRainAdapter.setDatas(location.getData());
                    chanceOfRainAdapter.notifyDataSetChanged();
                }
            }
        }else {
            getDialog().dismiss();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                getDialog().dismiss();
                break;
        }
    }
}
