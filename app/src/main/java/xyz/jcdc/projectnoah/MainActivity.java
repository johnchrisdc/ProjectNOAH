package xyz.jcdc.projectnoah;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import xyz.jcdc.projectnoah.adapter.DrawerAdapter;
import xyz.jcdc.projectnoah.chance_of_rain.Location;
import xyz.jcdc.projectnoah.contour.LatestContour;
import xyz.jcdc.projectnoah.doppler.Doppler;
import xyz.jcdc.projectnoah.fragment.WelcomeDialogFragment;
import xyz.jcdc.projectnoah.helper.Helper;
import xyz.jcdc.projectnoah.helper.MapHelper;
import xyz.jcdc.projectnoah.objects.DrawerItem;
import xyz.jcdc.projectnoah.objects.Layer;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DrawerAdapter.OnDrawerItemClickedListener {

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

    private ArrayList<LatestContour> latestContours;
    private ArrayList<Doppler> dopplers;

    private GroundOverlayOptions contourOverlay;
    private GroundOverlay contourGroundOverlay;

    private GroundOverlayOptions dopplerBaguioOverlay, dopplerSubicOverlay, dopplerTagaytayOverlay, dopplerCebuOverlay, dopplerHinatuanOverlay, dopplerTampakanOverlay, dopplerAparriOverlay, dopplerViracOverlay, dopplerBalerOverlay;
    private GroundOverlay dopplerBaguioGroundOverlay, dopplerSubicGroundOverlay, dopplerTagaytayGroundOverlay, dopplerCebuGroundOverlay, dopplerHinatuanGroundOverlay, dopplerTampakanGroundOverlay, dopplerAparriGroundOverlay, dopplerViracGroundOverlay, dopplerBalerGroundOverlay;

    private String current_contour_action, current_doppler_action;

    private ArrayList<Layer> layers;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PHILIPPINES = new LatLng(12.8797, 121.7740);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(PHILIPPINES).zoom(5).build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

        if ( category.equals(Constants.LAYER_WEATHER_CONTOUR) ){
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
                int x=0;
                for (Layer l : layers){
                    if(l.getCategory().equals(Constants.LAYER_WEATHER_CONTOUR)){
                        layers.remove(x);
                    }
                    x++;
                }

                mAdapter.getLayers().add(layer);
            }

            for (Layer l : mAdapter.getLayers()){
                Log.d("MainActivity", l.getAction());
            }
        }else if (category.equals(Constants.LAYER_WEATHER_DOPPLER)){
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
        }

    }

    private void removeDopplerFromMap(String action){
        switch (action){
            case Constants.ACTION_WEATHER_DOPPLER_APARRI:
                dopplerAparriGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_BAGUIO:
                dopplerBaguioGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_BALER:
                dopplerBalerGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_CEBU:
                dopplerCebuGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_HINATAUAN:
                dopplerHinatuanGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_SUBIC:
                dopplerSubicGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_TAGAYTAY:
                dopplerTagaytayGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_TAMPAKAN:
                dopplerTampakanGroundOverlay.remove();
                break;

            case Constants.ACTION_WEATHER_DOPPLER_VIRAC:
                dopplerViracGroundOverlay.remove();
                break;
        }
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

    private void applyContour(String action){
        current_contour_action = action;

        if(loadLatestContours != null){
            loadLatestContours.cancel(true);
        }

        loadLatestContours = new LoadLatestContours();
        loadLatestContours.execute();
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
