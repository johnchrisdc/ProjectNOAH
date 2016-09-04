package xyz.jcdc.projectnoah.sensors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.projectnoah.Constants;
import xyz.jcdc.projectnoah.chance_of_rain.Location;

/**
 * Created by jcdc on 9/4/2016.
 */

public class WeatherStation implements Serializable {

    private int type_id;
    private String icon;
    private String verbose_name;

    private ArrayList<Station> stations;

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVerbose_name() {
        return verbose_name;
    }

    public void setVerbose_name(String verbose_name) {
        this.verbose_name = verbose_name;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public static WeatherStation getWeatherStation() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.WEATHER_STATIONS)
                .build();

        Response response = client.newCall(request).execute();

        if(response != null){
            try{
                JSONArray stationsArray = new JSONArray(response.body().string());

                for (int x=0; x<stationsArray.length(); x++){
                    JSONObject jsonObject = stationsArray.optJSONObject(x);

                    Gson gson = new Gson();
                    WeatherStation weatherStation = gson.fromJson(jsonObject.toString(), WeatherStation.class);
                    Log.d("WeatherStation", weatherStation.getVerbose_name());
                }

            }catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }else{
            Log.d("WeatherStation", "Response null");
        }

        return null;
    }

}
