package xyz.jcdc.projectnoah.doppler;

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
import xyz.jcdc.projectnoah.contour.LatestContour;

/**
 * Created by jcdc on 9/3/2016.
 */

public class Doppler implements Serializable {

    private String url;
    private String gif_url;
    private String verbose_name;
    private Double[] extent;
    private int[] size;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGif_url() {
        return gif_url;
    }

    public void setGif_url(String gif_url) {
        this.gif_url = gif_url;
    }

    public String getVerbose_name() {
        return verbose_name;
    }

    public void setVerbose_name(String verbose_name) {
        this.verbose_name = verbose_name;
    }

    public Double[] getExtent() {
        return extent;
    }

    public void setExtent(Double[] extent) {
        this.extent = extent;
    }

    public int[] getSize() {
        return size;
    }

    public void setSize(int[] size) {
        this.size = size;
    }

    public static ArrayList<Doppler> getDopplers() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.DOPPLER)
                .build();

        Response response = client.newCall(request).execute();

        if(response != null){
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Doppler>>(){}.getType();
            return gson.fromJson(response.body().string(), collectionType);
        }

        return null;
    }

}
