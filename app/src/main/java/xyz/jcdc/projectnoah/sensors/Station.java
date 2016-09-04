package xyz.jcdc.projectnoah.sensors;

import java.io.Serializable;

/**
 * Created by jcdc on 9/4/2016.
 */

public class Station implements Serializable{

    private double lat;
    private double lng;
    private String url;
    private String verbose_name;
    private int station_id;

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

    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
    }
}
