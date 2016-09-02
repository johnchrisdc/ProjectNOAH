package xyz.jcdc.projectnoah.contour;

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
import xyz.jcdc.projectnoah.chance_of_rain.Location;

/**
 * Created by jcdc on 9/2/2016.
 */

public class LatestContour implements Serializable {

    private String url;
    private String verbose_name;
    private double[] extent;
    private int[] size;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVerbose_name() {
        return verbose_name;
    }

    public void setVerbose_name(String verbose_name) {
        this.verbose_name = verbose_name;
    }

    public double[] getExtent() {
        return extent;
    }

    public void setExtent(double[] extent) {
        this.extent = extent;
    }

    public int[] getSize() {
        return size;
    }

    public void setSize(int[] size) {
        this.size = size;
    }

    public static ArrayList<LatestContour> getLatestContours() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.LATEST_CONTOUR)
                .build();

        Response response = client.newCall(request).execute();

        if(response != null){
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<LatestContour>>(){}.getType();
            return gson.fromJson(response.body().string(), collectionType);
        }

        return null;
    }
}
