package com.oc.eliott.go4lunch.Controller.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.oc.eliott.go4lunch.Controller.Fragments.MapViewFragment;
import com.oc.eliott.go4lunch.Controller.Fragments.RecyclerViewFragment;
import com.oc.eliott.go4lunch.Controller.Fragments.WorkmatesFragment;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.LocationSingleton;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar; // Use to set a new Toolbar
    private DrawerLayout drawerLayout; // Use to set a Navigation Drawer
    private TextView emailUser, nameUser; // These TextView will contain user's mail and name
    private ImageView profilImgUser; // This ImageView will contains user's profile photo
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1; // ID used to get back data from the placeautocomplete
    private int fragmentId = 1; // Use to know which fragment is currently display on the screen
    private BottomNavigationView bottomNavigationView; // Use to implement a bottom navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener; // Use to add a onClick on each item of the navigation bottom
    private Fragment fragment;
    private boolean mLocationPermissionGranted; // Set to true if the user accept to be located
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1; // ID to identify which permission is asked
    private Location mLastKnownLocation; // Contain the last known location of the user's phone
    private FusedLocationProviderClient mFusedLocationProviderClient; // Used to get the current place of the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the view we need from activity_main layout and activity_main_nav_header layout
        NavigationView navView = findViewById(R.id.activity_main_navigation_drawer);
        emailUser = navView.getHeaderView(0).findViewById(R.id.nav_header_email);
        nameUser = navView.getHeaderView(0).findViewById(R.id.nav_header_name_user);
        profilImgUser = navView.getHeaderView(0).findViewById(R.id.nav_header_profil_img);

        // Call the method that get the permission to locate the phone and the method that get the location of the phone
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.getLocationPermission();

        // Call the different method use in this activity
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.updateUIWhenCreating();
    }

    // Starting AuthentificationActivity if current user not log
    private void updateUIWhenCreating(){
        if(getCurrentUser() == null ){
            Intent intent = new Intent(this, AuthentificationActivity.class);
            startActivity(intent);
        }
    }

    // Configure the click on each item of the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_toolbar_item_search:
                try{
                    // Create and custom our placeAutocomplete
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                            .build();

                    Intent intent = new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .setBoundsBias(new LatLngBounds(LocationSingleton.getInstance().getLocation(), LocationSingleton.getInstance().getLocation()))
                            .build(this);

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e){
                    Log.e("GooglePlayError", e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e){
                    Log.e("GooglePlayError2", e.getMessage());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Get back the result of the placeAutocomplete and open the DetailRestaurantActivity for the user's choice
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Place place = PlaceAutocomplete.getPlace(this, data);
                Intent intent = new Intent(this, DetailRestaurantActivity.class);
                intent.putExtra("idRestaurant", place.getId());
                startActivity(intent);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    // Use to add a menu to the new toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // Use to setup the new toolbar
    private void configureToolbar(){
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // This method closed the NavigationDrawer when user click on back button
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Configure the click on each item of the navigation drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item_your_lunch :
                Intent intentLunch = new Intent(this, LunchActivity.class);
                startActivity(intentLunch);
                break;
            case R.id.item_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.item_logout:
                signOutUserFromFirebase();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Used to change the current view (fragment) when user click on each item of the bottom navigation view
    private void addListenerToBottomNavigationViewItem(){
        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_nav_map_view:
                        fragment = new MapViewFragment();
                        configureAndShowFragment(fragment);
                        fragmentId = 1;
                        return true;
                    case R.id.menu_nav_list_view:
                        fragment = new RecyclerViewFragment();
                        configureAndShowFragment(fragment);
                        fragmentId = 2;
                        return true;
                    case R.id.menu_nav_workmates:
                        fragment = new WorkmatesFragment();
                        configureAndShowFragment(fragment);
                        fragmentId = 3;
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    // Method that configure our fragments
    private void configureAndShowFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_activity_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // This method set a new icon on the toolbar which open the NavigationDrawer
    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // This method configure the NavigationView and add a listener on each item
    private void configureNavigationView(){
        NavigationView navigationView = findViewById(R.id.activity_main_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Method open an AlertBuilder to disconnect the user if he click on the positive button
    private void signOutUserFromFirebase(){
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.popup_message_confirmation_deconnection)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthUI.getInstance().signOut(getBaseContext());
                        Intent intent = new Intent(getBaseContext(), AuthentificationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    // When the user is connected we update our different view with the user's personal data
    protected void onResume(){
        super.onResume();

        if(isCurrentUserLogged()){
            RequestManager glide = Glide.with(this);
            glide.load(getCurrentUser().getPhotoUrl()).into(profilImgUser);
            nameUser.setText(getCurrentUser().getDisplayName());
            emailUser.setText(getCurrentUser().getEmail());
        }
    }

    // If user accept to locate his phone then we get the latitude and the longitude
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null) LocationSingleton.getInstance().setLat(mLastKnownLocation.getLatitude());
                            if(mLastKnownLocation != null) LocationSingleton.getInstance().setLng(mLastKnownLocation.getLongitude());

                            fragment = new MapViewFragment();
                            configureAndShowFragment(fragment);

                            // Add a listener on each item of the BottomNavigationView
                            bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
                            addListenerToBottomNavigationViewItem();
                            bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Ask to the user the permission to locate his phone
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Get the result choose by the user in the variable mLocationPermissionGranted
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
            }
        }
    }
}
