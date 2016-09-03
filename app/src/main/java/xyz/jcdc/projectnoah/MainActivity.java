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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import xyz.jcdc.projectnoah.adapter.DrawerAdapter;
import xyz.jcdc.projectnoah.chance_of_rain.Location;
import xyz.jcdc.projectnoah.contour.LatestContour;
import xyz.jcdc.projectnoah.fragment.WelcomeDialogFragment;
import xyz.jcdc.projectnoah.helper.Helper;
import xyz.jcdc.projectnoah.objects.DrawerItem;

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

    private ArrayList<LatestContour> latestContours;

    private GroundOverlayOptions contourOverlay;
    private GroundOverlay contourGroundOverlay;

    private String current_contour_action;

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

        mAdapter = new DrawerAdapter(drawerItems);
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
    public void onDrawerItemClicked(String action) {
        Log.d("MainActivity", action);
        drawer.closeDrawers();

        if (action.equals(Constants.ACTION_WEATHER_CONTOUR_1) || action.equals(Constants.ACTION_WEATHER_CONTOUR_3) ||
                action.equals(Constants.ACTION_WEATHER_CONTOUR_6) || action.equals(Constants.ACTION_WEATHER_CONTOUR_12)){
            applyContour(action);
        }

    }

    private void applyContour(String action){
        current_contour_action = action;

        if(loadLatestContours != null){
            loadLatestContours.cancel(true);
        }

        loadLatestContours = new LoadLatestContours();
        loadLatestContours.execute();


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
