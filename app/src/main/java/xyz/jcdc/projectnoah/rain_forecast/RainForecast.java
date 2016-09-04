package xyz.jcdc.projectnoah.rain_forecast;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.projectnoah.Constants;
import xyz.jcdc.projectnoah.mtsat.Satellite;

/**
 * Created by jcdc on 9/4/2016.
 */

public class RainForecast {

    private String last_update;
    private String source;
    private String location;
    private double lat;
    private double lng;
    private ArrayList<Data> data;
    private String icon;

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static ArrayList<RainForecast> getRainForecasts() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.WEATHER_OUTLOOK_RAIN_FORECAST)
                .build();

        Response response = client.newCall(request).execute();

        if(response != null){

            try {
                JSONObject resultObj = new JSONObject(response.body().string());
                if (resultObj.optBoolean("success") == true) {
                    JSONArray dataArray = resultObj.optJSONArray("data");

                    ArrayList<RainForecast> rainForecasts = new ArrayList<>();

                    for (int x=0; x<dataArray.length(); x++){
                        JSONObject jsonObject = dataArray.optJSONObject(x);
                        Gson gson = new Gson();

                        RainForecast rainForecast = gson.fromJson(jsonObject.toString(), RainForecast.class);
                        rainForecasts.add(rainForecast);
                    }

                    return rainForecasts;
                }


            }catch (JSONException e){

            }
        }

        return null;
    }
}
