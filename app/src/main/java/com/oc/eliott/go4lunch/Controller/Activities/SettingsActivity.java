package com.oc.eliott.go4lunch.Controller.Activities;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.oc.eliott.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {
    private Switch enableNotification; // Used to add a switch
    private LinearLayout settingsContainer; // LinearLayout that contain the switch
    private SharedPreferences sharedPreferences; // Used to get our shared preferences
    private SharedPreferences.Editor editor; // Used to get data from the preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bind all the view
        enableNotification = findViewById(R.id.settings_activity_switch);
        settingsContainer = findViewById(R.id.settings_activity_container);

        // Get our shared preferences
        sharedPreferences = getApplicationContext().getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Update the state of the switch depending on the value get from the preferences
        if(getSwitchState()) enableNotification.setChecked(true);
        else enableNotification.setChecked(false);

        updateSwitchState();
    }

    // Method that save the state of the switch in a boolean and display a snackbar to warn the user
    private void updateSwitchState(){
        enableNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchState) {
                editor.putBoolean("switchState", switchState).commit();
                if(getSwitchState()) Snackbar.make(settingsContainer, R.string.notification_is_enabled, Snackbar.LENGTH_LONG).show();
                else Snackbar.make(settingsContainer, R.string.notification_is_disabled, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Return true of false depending on the state of the switch
    private boolean getSwitchState(){
        return sharedPreferences.getBoolean("switchState", false);
    }
}
