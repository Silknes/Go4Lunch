package com.oc.eliott.go4lunch.Controller.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.auth.AuthUI;
import com.oc.eliott.go4lunch.R;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar; // Use to set a new Toolbar
    private DrawerLayout drawerLayout; // Use to set a Navigation Drawer
    private TextView emailUser, nameUser; // These TextView will contain user's mail and name
    private ImageView profilImgUser; // This ImageView will contains user's profile photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the view we need from activity_main layout and activity_main_nav_header layout
        NavigationView navView = findViewById(R.id.activity_main_navigation_drawer);
        emailUser = navView.getHeaderView(0).findViewById(R.id.nav_header_email);
        nameUser = navView.getHeaderView(0).findViewById(R.id.nav_header_name_user);
        profilImgUser = navView.getHeaderView(0).findViewById(R.id.nav_header_profil_img);

        // Call the different method use in this activity
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.updateUIWhenCreating();
    }

    // Starting AuthentificationActivity if current user not log
    private void updateUIWhenCreating(){
        if(isCurrentUserLogged()){

        }
        else{
            Intent intent = new Intent(this, AuthentificationActivity.class);
            startActivity(intent);
        }
    }

    // Configure the click on each item of the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_toolbar_item_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                Toast.makeText(this, "Your Lunch", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(this)
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
}
