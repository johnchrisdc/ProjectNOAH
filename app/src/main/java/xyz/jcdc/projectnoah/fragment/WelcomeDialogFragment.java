package xyz.jcdc.projectnoah.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.projectnoah.R;
import xyz.jcdc.projectnoah.chance_of_rain.Data;
import xyz.jcdc.projectnoah.chance_of_rain.Location;
import xyz.jcdc.projectnoah.chance_of_rain.adapter.ChanceOfRainAdapter;
import xyz.jcdc.projectnoah.helper.DateHelper;

/**
 * Created by jcdc on 9/1/2016.
 */

public class WelcomeDialogFragment extends DialogFragment implements View.OnClickListener {

    private Context context;

    private ArrayList<Location> locations;

    private RecyclerView dataRecyclerView;
    private ChanceOfRainAdapter chanceOfRainAdapter;

    private Button close;

    private AppCompatAutoCompleteTextView location;
    private TextView location_textview;
    private TextView date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();

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

            setLocations4hour("Manila, Metro Manila");
        }else {
            getDialog().dismiss();
        }

        ArrayList<String> location_string = new ArrayList<>();

        for(Location loc : locations){
            location_string.add(loc.getLocation());
        }

        location_textview = (TextView) view.findViewById(R.id.location);
        date = (TextView) view.findViewById(R.id.date);


        DateFormat dateFormat = new SimpleDateFormat("MMM dd, EEEE");
        Date datex = new Date();

        date.setText(dateFormat.format(datex));
        DateHelper.loadDateToTextView(date);

        location = (AppCompatAutoCompleteTextView) view.findViewById(R.id.location_autocomplete);
        String[] locationsArray = location_string.toArray(new String[location_string.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, locationsArray);

        location.setAdapter(adapter);

        location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                location_textview.setText(adapterView.getItemAtPosition(i).toString());
                setLocations4hour(adapterView.getItemAtPosition(i).toString());

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(location.getWindowToken(), 0);
            }
        });

        return view;
    }

    private void setLocations4hour(String location_){
        for(Location location : locations){
            if(location.getLocation().equals(location_)){
                chanceOfRainAdapter.setDatas(location.getData());
                chanceOfRainAdapter.notifyDataSetChanged();
            }
        }
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
