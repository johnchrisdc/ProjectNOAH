package xyz.jcdc.projectnoah;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import xyz.jcdc.projectnoah.adapter.DrawerAdapter;
import xyz.jcdc.projectnoah.chance_of_rain.Location;
import xyz.jcdc.projectnoah.contour.LatestContour;
import xyz.jcdc.projectnoah.doppler.Doppler;
import xyz.jcdc.projectnoah.fragment.WelcomeDialogFragment;
import xyz.jcdc.projectnoah.helper.Helper;
import xyz.jcdc.projectnoah.helper.MapHelper;
import xyz.jcdc.projectnoah.mtsat.Satellite;
import xyz.jcdc.projectnoah.objects.DrawerItem;
import xyz.jcdc.projectnoah.objects.Layer;
import xyz.jcdc.projectnoah.rain_forecast.RainForecast;
import xyz.jcdc.projectnoah.sensors.Station;
import xyz.jcdc.projectnoah.sensors.WeatherStation;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DrawerAdapter.OnDrawerItemClickedListener,
        GoogleMap.OnMarkerClickListener{

    private Context context;

    private Toolbar toolbar;

    private GoogleMap mMap;
    private LatLng PHILIPPINES;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private DrawerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LoadLocations loadLocations;
    private LoadLatestContours loadLatestContours;
    private LoadContour loadContour;

    private LoadDoppler loadDoppler;

    private LoadSatellite loadSatellite;

    private LoadWeatherForecast loadWeatherForecast;

    private ArrayList<LatestContour> latestContours;
    private ArrayList<Doppler> dopplers;
    private ArrayList<Satellite> satellites;
    private ArrayList<RainForecast> rainForecasts;

    private GroundOverlayOptions contourOverlay;
    private GroundOverlay contourGroundOverlay;

    private GroundOverlayOptions dopplerBaguioOverlay, dopplerSubicOverlay, dopplerTagaytayOverlay, dopplerCebuOverlay, dopplerHinatuanOverlay, dopplerTampakanOverlay, dopplerAparriOverlay, dopplerViracOverlay, dopplerBalerOverlay;
    private GroundOverlay dopplerBaguioGroundOverlay, dopplerSubicGroundOverlay, dopplerTagaytayGroundOverlay, dopplerCebuGroundOverlay, dopplerHinatuanGroundOverlay, dopplerTampakanGroundOverlay, dopplerAparriGroundOverlay, dopplerViracGroundOverlay, dopplerBalerGroundOverlay;

    private GroundOverlayOptions satelliteOverlay;
    private GroundOverlay satelliteGroundOverlay;

    private String current_contour_action, current_doppler_action, current_satellite_action, current_weather_forecast_action, current_sensor_action;

    private ArrayList<Layer> layers;

    private Marker marker;
    private Marker sensorRainGaugesMarker, sensorStreamGaugesMarker, sensorRainAndStreamGaugesMarker, sensorTideLevelsMarker, sensorWeatherStationsMarker;

    private HashMap<Marker, RainForecast> markerRainForecastHashMap;
    private HashMap<Marker, Station> markerRainGaugesHashMap, markerStreamGaugesStationHashMap, markerRainAndStreamGaugesStationHashMap, markerTideLevelsHashMap, markerWeatherStationsHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

        mRecyclerView.setHasFixedSize(true);

        ArrayList<DrawerItem> drawerItems = new ArrayList<>();

        drawerItems.add( new DrawerItem(R.drawable.weatherbtn, "Weather"));
        drawerItems.add( new DrawerItem(R.drawable.sensorbtn, "Sensors"));
        drawerItems.add( new DrawerItem(R.drawable.floodbtn, "Flood"));
        drawerItems.add( new DrawerItem(R.drawable.landslidebtn, "Landslides"));
        drawerItems.add( new DrawerItem(R.drawable.stormsurgebtn, "Storm Surge"));
        drawerItems.add( new DrawerItem(R.drawable.boundary, "Boundaries"));
        drawerItems.add( new DrawerItem(R.drawable.criticalbtn, "Critical Facilities"));
        drawerItems.add( new DrawerItem(R.drawable.ovindexbtn, "Dengue Monitoring"));

        layers = new ArrayList<Layer>();

        mAdapter = new DrawerAdapter(drawerItems, layers);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnDrawerItemClickedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadLocations = new LoadLocations();
        loadLocations.execute();

        //Load Hashmaps
        markerRainGaugesHashMap = new HashMap<>();
        markerTideLevelsHashMap = new HashMap<>();
        markerRainAndStreamGaugesStationHashMap = new HashMap<>();
        markerWeatherStationsHashMap = new HashMap<>();
        markerStreamGaugesStationHashMap= new HashMap<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PHILIPPINES = new LatLng(12.8797, 121.7740);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(PHILIPPINES).zoom(5).build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void showWelcomeDialogFragment(ArrayList<Location> locations){
        WelcomeDialogFragment welcomeDialogFragment = new WelcomeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("locations", locations);
        welcomeDialogFragment.setArguments(args);

        welcomeDialogFragment.show(getSupportFragmentManager(), "welcome_rotonda");
    }

    @Override
    public void onDrawerItemClicked(String category, String action) {
        drawer.closeDrawers();

        switch (category){
            case Constants.LAYER_WEATHER_CONTOUR:

                if(isContourLayerExists(action)){
                    if (contourGroundOverlay != null){
                        contourGroundOverlay.remove();
                    }

                    //This layer does not support multiple layers, so remove it if it exists
                    int x=0;
                    for (Layer l : mAdapter.getLayers()){
                        if(l.getCategory().equals(Constants.LAYER_WEATHER_CONTOUR)){
                            Log.d("MainActivity", "DELETING");
                            mAdapter.getLayers().remove(x);
                        }
                        x++;
                    }
                }else {
                    applyContour(action);

                    Layer layer = new Layer();
                    layer.setCategory(Constants.LAYER_WEATHER_CONTOUR);
                    layer.setAction(action);

                    //This layer does not support multiple layers, so remove it if it exists
                    for (int x = mAdapter.getLayers().size() - 1; x > -1; x--){
                        if(mAdapter.getLayers().get(x).getCategory().equals(Constants.LAYER_WEATHER_CONTOUR)){
                            mAdapter.getLayers().remove(x);
                        }
                    }

                    mAdapter.getLayers().add(layer);
                }

                for (Layer l : mAdapter.getLayers()){
                    Log.d("MainActivity", l.getAction());
                }

                break;

            case Constants.LAYER_WEATHER_DOPPLER:

                Log.d("MainActivity", "Doppler clicked");
                if (loadDoppler != null){
                    loadDoppler.cancel(true);
                }

                if (isDopplerLayerExists(action)){
                    for (int x = mAdapter.getLayers().size() - 1; x > -1; x--){
                        if(mAdapter.getLayers().get(x).getCategory().equals(Constants.LAYER_WEATHER_DOPPLER)){
                            if(mAdapter.getLayers().get(x).getAction().equals(action)){
                                removeDopplerFromMap(action);
                                mAdapter.getLayers().remove(x);
                                Log.d("MainActivity", "Doppler removed: ");
                            }
                        }
                    }
                }else {
                    Layer layer = new Layer();
                    layer.setAction(action);
                    layer.setCategory(category);

                    mAdapter.getLayers().add(layer);
                    current_doppler_action = action;

                    loadDoppler = new LoadDoppler();
                    loadDoppler.execute();
                }

                break;

            case Constants.LAYER_WEATHER_SATELLITE:

                if(isSatelliteLayerExists(action)){
                    if (satelliteGroundOverlay != null){
                        satelliteGroundOverlay.remove();
                    }

                    //This layer does not support multiple layers, so remove it if it exists
                    int x=0;
                    for (Layer l : mAdapter.getLayers()){
                        if(l.getCategory().equals(Constants.LAYER_WEATHER_SATELLITE)){
                            Log.d("MainActivity", "DELETING");
                            mAdapter.getLayers().remove(x);
                        }
                        x++;
                    }
                }else {
                    applySatellite(action);

                    Layer layer = new Layer();
                    layer.setCategory(Constants.LAYER_WEATHER_SATELLITE);
                    layer.setAction(action);

                    //This layer does not support multiple layers, so remove it if it exists
                    for (int x = mAdapter.getLayers().size() - 1; x > -1; x--){
                        if(mAdapter.getLayers().get(x).getCategory().equals(Constants.LAYER_WEATHER_SATELLITE)){
                            mAdapter.getLayers().remove(x);
                        }
                    }

                    mAdapter.getLayers().add(layer);
                }

                break;

            case Constants.LAYER_WEATHER_FORECAST:
                if(isWeatherForecastLayerExists(action)){
                    for (int x = mAdapter.getLayers().size() - 1; x > -1; x--){
                        if(mAdapter.getLayers().get(x).getCategory().equals(Constants.LAYER_WEATHER_FORECAST)){
                            if(mAdapter.getLayers().get(x).getAction().equals(action)){
                                removeDopplerFromMap(action);
                                mAdapter.getLayers().remove(x);
                                Log.d("MainActivity", "Weather Forecast removed: ");

                                //Remove all Markers



                                for (Map.Entry<Marker, RainForecast> marker : markerRainForecastHashMap.entrySet()){
                                    if (marker.getKey() != null)
                                        marker.getKey().remove();
                                }

                            }
                        }
                    }
                }else{
                    Layer layer = new Layer();
                    layer.setAction(action);
                    layer.setCategory(category);

                    mAdapter.getLayers().add(layer);
                    current_weather_forecast_action = action;

                    loadWeatherForecast = new LoadWeatherForecast();
                    loadWeatherForecast.execute();
                }

                break;

            case Constants.LAYER_SENSORS:

                if (isSensorLayerExists(action)){
                    for (int x = mAdapter.getLayers().size() - 1; x > -1; x--){
                        if(mAdapter.getLayers().get(x).getCategory().equals(Constants.LAYER_SENSORS)){
                            if(mAdapter.getLayers().get(x).getAction().equals(action)){
                                removeSensorMarkers(action);

                                mAdapter.getLayers().remove(x);
                                Log.d("MainActivity", "Sensor removed: ");
                            }
                        }
                    }
                }else{
                    Layer layer = new Layer();
                    layer.setAction(action);
                    layer.setCategory(category);

                    mAdapter.getLayers().add(layer);
                    current_sensor_action = action;

                    new GetWeatherStation().execute();
                }

                break;

        }

    }

    private void removeSensorMarkers(String action){
        switch (action){
            case Constants.ACTION_SENSORS_RAIN_GAUGE:
                for (Map.Entry<Marker, Station> station : markerRainGaugesHashMap.entrySet()){
                    if (station.getKey() != null)
                        station.getKey().remove();
                }
                break;

            case Constants.ACTION_SENSORS_RAIN_AND_STREAM_GAUGE:
                for (Map.Entry<Marker, Station> station : markerRainAndStreamGaugesStationHashMap.entrySet()){
                    if (station.getKey() != null)
                        station.getKey().remove();
                }

                break;

            case Constants.ACTION_SENSORS_STREAM_GAUGE:
                for (Map.Entry<Marker, Station> station : markerStreamGaugesStationHashMap.entrySet()){
                    if (station.getKey() != null)
                        station.getKey().remove();
                }

                break;

            case Constants.ACTION_SENSORS_TIDE_LEVELS:
                for (Map.Entry<Marker, Station> station : markerTideLevelsHashMap.entrySet()){
                    if (station.getKey() != null)
                        station.getKey().remove();
                }

                break;

            case Constants.ACTION_SENSORS_WEATHER:
                for (Map.Entry<Marker, Station> station : markerWeatherStationsHashMap.entrySet()){
                    if (station.getKey() != null)
                        station.getKey().remove();
                }

                break;

        }
    }

    private void removeDopplerFromMap(String action){
        switch (action){
            case Constants.ACTION_WEATHER_DOPPLER_APARRI:
                if (dopplerAparriGroundOverlay != null)
                    dopplerAparriGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_BAGUIO:
                if (dopplerBaguioGroundOverlay != null)
                    dopplerBaguioGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_BALER:
                if (dopplerBalerGroundOverlay != null)
                    dopplerBalerGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_CEBU:
                if (dopplerCebuGroundOverlay != null)
                    dopplerCebuGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_HINATAUAN:
                if (dopplerHinatuanGroundOverlay != null)
                    dopplerHinatuanGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_SUBIC:
                if (dopplerSubicGroundOverlay != null)
                    dopplerSubicGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_TAGAYTAY:
                if (dopplerTagaytayGroundOverlay != null)
                    dopplerTagaytayGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_TAMPAKAN:
                if (dopplerTampakanGroundOverlay != null)
                    dopplerTampakanGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_VIRAC:
                if (dopplerViracGroundOverlay != null)
                    dopplerViracGroundOverlay.remove();
                break;
        }
    }

    private boolean isSensorLayerExists(String action){

        for (Layer layer : layers){
            if (layer.getCategory().equals(Constants.LAYER_SENSORS)){
                if (layer.getAction().equals(action)){
                    Log.d("MainActivity" , "Sensor Action exists");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSatelliteLayerExists(String action){

        for (Layer layer : layers){
            if (layer.getCategory().equals(Constants.LAYER_WEATHER_SATELLITE)){
                if (layer.getAction().equals(action)){
                    Log.d("MainActivity" , "Satellite Action exists");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isWeatherForecastLayerExists(String action){

        for (Layer layer : layers){
            if (layer.getCategory().equals(Constants.LAYER_WEATHER_FORECAST)){
                if (layer.getAction().equals(action)){
                    Log.d("MainActivity" , "WEather Forecast Action exists");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isDopplerLayerExists(String action){

        for (Layer layer : layers){
            if (layer.getCategory().equals(Constants.LAYER_WEATHER_DOPPLER)){
                if (layer.getAction().equals(action)){
                    Log.d("MainActivity" , "Doppler Action exists");
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isContourLayerExists(String action){
        int x = 0;
        for (Layer l : layers){
            if(l.getCategory().equals(Constants.LAYER_WEATHER_CONTOUR)){
                if(l.getAction().equals(action)){
                    return true;
                }
            }
            x++;
        }
        return false;
    }

    private void applySatellite(String action){
        current_satellite_action = action;

        if(loadSatellite != null){
            loadSatellite.cancel(true);
        }

        loadSatellite = new LoadSatellite();
        loadSatellite.execute();
    }

    private void applyContour(String action){
        current_contour_action = action;

        if(loadLatestContours != null){
            loadLatestContours.cancel(true);
        }

        loadLatestContours = new LoadLatestContours();
        loadLatestContours.execute();
    }

    private class GetWeatherStation extends AsyncTask<Void, Void, ArrayList<WeatherStation>>{
        @Override
        protected ArrayList<WeatherStation> doInBackground(Void... voids) {
            try {
                return WeatherStation.getWeatherStation();
            }catch (IOException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherStation> weatherStations) {
            super.onPostExecute(weatherStations);

            if (weatherStations != null){
                for (WeatherStation weatherStation : weatherStations){
                    if (weatherStation.getVerbose_name().equals(current_sensor_action)){
                        new LoadWeatherStationMarker().execute(weatherStation);
                    }
                }

            }

        }
    }

    private class LoadWeatherStationMarker extends AsyncTask<WeatherStation, Void, Bitmap>{
        WeatherStation weatherStation;

        @Override
        protected Bitmap doInBackground(WeatherStation... weatherStations) {
            weatherStation = weatherStations[0];

            try{
                return Glide.
                        with(context).
                        load(Constants.WEATHER_STATIONS_MARKER + weatherStation.getIcon()).
                        asBitmap().
                        skipMemoryCache(true).
                        into(30, 30). // Width and height
                        get();

            }catch (ConcurrentModificationException e){

            }catch (InterruptedException e){

            }catch (ExecutionException e){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null){ //May produce bugs
                for (Station station : weatherStation.getStations()){

                    switch (current_sensor_action){
                        case Constants.ACTION_SENSORS_RAIN_GAUGE:
                            sensorRainGaugesMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(station.getLat(), station.getLng()))
                                    .title(station.getVerbose_name())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            markerRainGaugesHashMap.put(sensorRainGaugesMarker, station);

                            break;

                        case Constants.ACTION_SENSORS_RAIN_AND_STREAM_GAUGE:
                            sensorRainAndStreamGaugesMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(station.getLat(), station.getLng()))
                                    .title(station.getVerbose_name())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            markerRainAndStreamGaugesStationHashMap.put(sensorRainAndStreamGaugesMarker, station);

                            break;

                        case Constants.ACTION_SENSORS_STREAM_GAUGE:
                            sensorStreamGaugesMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(station.getLat(), station.getLng()))
                                    .title(station.getVerbose_name())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            markerStreamGaugesStationHashMap.put(sensorStreamGaugesMarker, station);

                            break;

                        case Constants.ACTION_SENSORS_TIDE_LEVELS:
                            sensorTideLevelsMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(station.getLat(), station.getLng()))
                                    .title(station.getVerbose_name())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            markerTideLevelsHashMap.put(sensorTideLevelsMarker, station);

                            break;

                        case Constants.ACTION_SENSORS_WEATHER:
                            sensorWeatherStationsMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(station.getLat(), station.getLng()))
                                    .title(station.getVerbose_name())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            markerWeatherStationsHashMap.put(sensorWeatherStationsMarker, station);

                            break;

                    }
                }
            }

        }
    }

    private class LoadWeatherForecast extends AsyncTask<Void, Void, ArrayList<RainForecast>>{

        @Override
        protected ArrayList<RainForecast> doInBackground(Void... voids) {
            try {
                return RainForecast.getRainForecasts();
            }catch (IOException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<RainForecast> rainForecasts) {
            super.onPostExecute(rainForecasts);

            if(rainForecasts != null){
                MainActivity.this.rainForecasts = rainForecasts;
                markerRainForecastHashMap = new HashMap<>();

                int x = 0;
                for (RainForecast rainForecast : rainForecasts){
                    new LoadMarker().execute(rainForecast);
                }
            }

        }
    }

    private class LoadMarker extends AsyncTask<RainForecast, Void, Bitmap>{
        RainForecast rainForecast;
        @Override
        protected Bitmap doInBackground(RainForecast... rainForecasts) {
            rainForecast = rainForecasts[0];

            try{
                return Glide.
                        with(context).
                        load(rainForecast.getIcon()).
                        asBitmap().
                        skipMemoryCache(true).
                        into(30, 30). // Width and height
                        get();

            }catch (ConcurrentModificationException e){

            }catch (InterruptedException e){

            }catch (ExecutionException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(rainForecast.getLat(), rainForecast.getLng()))
                        .title("Hello world")
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                markerRainForecastHashMap.put(marker, rainForecast);

                mMap.setOnMarkerClickListener(MainActivity.this);
            }

        }
    }

    private class LoadSatellite extends AsyncTask<Void, Void, ArrayList<Satellite>>{
        @Override
        protected ArrayList<Satellite> doInBackground(Void... voids) {
            try {
                return Satellite.getSatellites();
            }catch (IOException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Satellite> satellites) {
            super.onPostExecute(satellites);

            if (satellites != null){
                Satellite current_satellite = null;
                MainActivity.this.satellites = satellites;
                Log.d("MainActivity", "Satellites: " + satellites.toString());

                for (Satellite satellite : satellites){
                    if(satellite.getVerbose_name().equals(current_satellite_action)){
                        current_satellite = satellite;
                    }
                }

                if(current_satellite != null){
                    new LoadSatelliteContour().execute(current_satellite);
                }else{
                    Log.d("MainActivity", "Satellite is null");
                }

            }

        }
    }

    private class LoadSatelliteContour extends AsyncTask<Satellite, Void, Bitmap>{
        Satellite satellite;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (satelliteGroundOverlay != null){
                satelliteGroundOverlay.remove();
            }

        }

        @Override
        protected Bitmap doInBackground(Satellite... satellites) {
            satellite = satellites[0];

            String contour_url = satellite.getUrl();
            int width = satellite.getSize()[0];
            int height = satellite.getSize()[1];

            Log.d("Satellite","URL: " +  contour_url);

            try {
                return Glide.
                        with(context).
                        load(contour_url).
                        asBitmap().
                        skipMemoryCache(true).
                        into(width, height). // Width and height
                        get();
            }catch (InterruptedException e){

            }catch (ExecutionException e){

            }
            Log.d("MainActivity", "Error Satellite");
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null){
                LatLngBounds newarkBounds = new LatLngBounds(
                        new LatLng(satellite.getExtent()[1], satellite.getExtent()[0]),       // South west corner
                        new LatLng(satellite.getExtent()[3], satellite.getExtent()[2]));      // North east corner

                Log.d("MainActivity", "Applying satellite");
                satelliteOverlay = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .transparency(.5f)
                        .positionFromBounds(newarkBounds);

                satelliteGroundOverlay = mMap.addGroundOverlay(satelliteOverlay);
            }

        }
    }

    private class LoadDoppler extends AsyncTask<Void, Void, ArrayList<Doppler>>{
        @Override
        protected ArrayList<Doppler> doInBackground(Void... voids) {
            try {
                return Doppler.getDopplers();
            }catch (IOException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Doppler> dopplers) {
            super.onPostExecute(dopplers);

            if (dopplers != null){
                Doppler current_doppler = null;
                MainActivity.this.dopplers = dopplers;

                for (Doppler doppler : dopplers){
                    if (doppler.getVerbose_name().equalsIgnoreCase(current_doppler_action)){
                        current_doppler = doppler;
                    }
                }

                if (current_doppler != null){
                    new LoadDopplerContour().execute(current_doppler);
                }else{
                    Log.d("MainActivity", "Doppler is null");
                }

            }

        }
    }

    private class LoadDopplerContour extends AsyncTask<Doppler, Void, Bitmap>{
        Doppler doppler;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Doppler... params) {
            Log.d("MainActivity", "Loading Doppler");

            doppler = params[0];
            String contour_url = doppler.getUrl();
            int width = doppler.getSize()[0];
            int height = doppler.getSize()[1];

            Log.d("Dopller","URL: " +  contour_url);

            try {
                return Glide.
                        with(context).
                        load(contour_url).
                        asBitmap().
                        skipMemoryCache(true).
                        into(width, height). // Width and height
                        get();
            }catch (InterruptedException e){

            }catch (ExecutionException e){

            }
            Log.d("MainActivity", "Error Doppler");
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                LatLngBounds newarkBounds = new LatLngBounds(
                        new LatLng(doppler.getExtent()[1], doppler.getExtent()[0]),       // South west corner
                        new LatLng(doppler.getExtent()[3], doppler.getExtent()[2]));      // North east corner

                switch (current_doppler_action){
                    case Constants.ACTION_WEATHER_DOPPLER_APARRI:
                        Log.d("MainActivity", "Applying countour");
                        dopplerAparriOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerAparriGroundOverlay = mMap.addGroundOverlay(dopplerAparriOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_BAGUIO:
                        Log.d("MainActivity", "Applying countour");
                        dopplerBaguioOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerBaguioGroundOverlay = mMap.addGroundOverlay(dopplerBaguioOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_BALER:
                        Log.d("MainActivity", "Applying countour");
                        dopplerBalerOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerBalerGroundOverlay = mMap.addGroundOverlay(dopplerBalerOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_CEBU:
                        Log.d("MainActivity", "Applying countour");
                        dopplerCebuOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerCebuGroundOverlay = mMap.addGroundOverlay(dopplerCebuOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_HINATAUAN:
                        Log.d("MainActivity", "Applying countour");
                        dopplerHinatuanOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerHinatuanGroundOverlay = mMap.addGroundOverlay(dopplerHinatuanOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_SUBIC:
                        Log.d("MainActivity", "Applying countour");
                        dopplerSubicOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerSubicGroundOverlay = mMap.addGroundOverlay(dopplerSubicOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_TAGAYTAY:
                        Log.d("MainActivity", "Applying countour");
                        dopplerTagaytayOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerTagaytayGroundOverlay = mMap.addGroundOverlay(dopplerTagaytayOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_TAMPAKAN:
                        Log.d("MainActivity", "Applying countour");
                        dopplerTampakanOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerTampakanGroundOverlay = mMap.addGroundOverlay(dopplerTampakanOverlay);
                        break;

                    case Constants.ACTION_WEATHER_DOPPLER_VIRAC:
                        Log.d("MainActivity", "Applying countour");
                        dopplerViracOverlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .transparency(.5f)
                                .positionFromBounds(newarkBounds);

                        dopplerViracGroundOverlay = mMap.addGroundOverlay(dopplerViracOverlay);
                        break;
                }

                MapHelper.zoomToMap(doppler, mMap);

            }
        }
    }

    private class LoadLatestContours extends AsyncTask<Void, Void, ArrayList<LatestContour>>{
        @Override
        protected ArrayList<LatestContour> doInBackground(Void... voids) {
            try {
                return LatestContour.getLatestContours();
            }catch (IOException e){

            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<LatestContour> latestContours) {
            super.onPostExecute(latestContours);
            if (latestContours != null){
                Log.d("MainActivity", "Latest Contours loaded");
                MainActivity.this.latestContours = latestContours;

                LatestContour latestContour = null;

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_1)){
                    latestContour = MainActivity.this.latestContours.get(0);
                }

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_3)){
                    latestContour = MainActivity.this.latestContours.get(1);
                }

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_6)){
                    latestContour = MainActivity.this.latestContours.get(2);
                }

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_12)){
                    latestContour = MainActivity.this.latestContours.get(3);
                }

                /////
                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_24)){
                    latestContour = MainActivity.this.latestContours.get(4);
                }

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_TEMPERATURE)){
                    latestContour = MainActivity.this.latestContours.get(5);
                }

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_PRESSURE)){
                    latestContour = MainActivity.this.latestContours.get(6);
                }

                if(current_contour_action.equals(Constants.ACTION_WEATHER_CONTOUR_HUMIDITY)){
                    latestContour = MainActivity.this.latestContours.get(7);
                }

                Log.d("MainActivity", "DEBUG CONTOUR URL: " + latestContour.getUrl());

                if(latestContour != null){
                    new LoadContour().execute(latestContour);
                }else {
                    Log.d("MainActivity", "latestContour is null");
                }
            }
        }
    }

    private class LoadContour extends AsyncTask<LatestContour, Void, Bitmap>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(contourGroundOverlay != null){
                contourGroundOverlay.remove();
            }
        }

        @Override
        protected Bitmap doInBackground(LatestContour... params) {
            Log.d("MainActivity", "Loading contour");

            LatestContour latestContour = params[0];
            String contour_url = latestContour.getUrl();
            int width = latestContour.getSize()[0];
            int height = latestContour.getSize()[1];

            Log.d("Contour","URL: " +  contour_url);

            try {
                return Glide.
                        with(context).
                        load(contour_url).
                        asBitmap().
                        skipMemoryCache(true).
                        into(width, height). // Width and height
                        get();
            }catch (InterruptedException e){

            }catch (ExecutionException e){

            }
            Log.d("MainActivity", "Error countour");
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                LatLngBounds newarkBounds = new LatLngBounds(
                        new LatLng(3.89, 115.28),       // South west corner
                        new LatLng(21.528, 128.285));      // North east corner

                Log.d("MainActivity", "Applying countour");
                contourOverlay = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .transparency(.5f)
                        .positionFromBounds(newarkBounds);

                contourGroundOverlay = mMap.addGroundOverlay(contourOverlay);
            }
        }
    }

    private class LoadLocations extends AsyncTask<Void, Void, ArrayList<Location>> {
        @Override
        protected ArrayList<Location> doInBackground(Void... voids) {
            try {
                return Location.getLocations();
            }catch (IOException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Location> locations) {
            super.onPostExecute(locations);

            if (locations != null){
                showWelcomeDialogFragment(locations);
            }
        }
    }
}
