package xyz.jcdc.projectnoah.helper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import xyz.jcdc.projectnoah.doppler.Doppler;

/**
 * Created by jcdc on 9/4/2016.
 */

public class MapHelper {

    public static void zoomToMap(Doppler doppler, GoogleMap map){
        double lat1 = doppler.getExtent()[1];
        double lat2 = doppler.getExtent()[3];

        double lon1 = doppler.getExtent()[0];
        double lon2 = doppler.getExtent()[2];

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        LatLng dopplerLatLng = new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(dopplerLatLng).zoom(7).build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
