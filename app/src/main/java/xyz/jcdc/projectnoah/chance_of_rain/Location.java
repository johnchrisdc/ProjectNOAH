package xyz.jcdc.projectnoah.chance_of_rain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.projectnoah.Constants;

/**
 * Created by jcdc on 9/2/2016.
 */

public class Location implements Serializable{

    private String last_update;
    private String source;
    private String location;
    private String lat;
    private String lng;
    private String icon;

    private ArrayList<Data> data;

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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
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

    public static ArrayList<Location> getLocations() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.FOUR_HOUR_FORCAST)
                .build();

        Response response = client.newCall(request).execute();

        if(response != null){
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Location>>(){}.getType();
            return gson.fromJson(response.body().string(), collectionType);
        }

        return null;
    }
}
