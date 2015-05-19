package nu.here.is.teampinez.weatherwarning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GUIActivity extends Activity {

    TextView theTextView;
    MyCurrentLocationListener gps;
    NotificationMain notificationMain = new NotificationMain();

    /*
    Variables below this point are used to store coordinates.
     */
    double locLat;
    double locLon;
    int locLatInt;
    int locLonInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreenactivity);
        gps = new MyCurrentLocationListener(GUIActivity.this);
        if(gps.canGetLocation()){
            updateCoordinates();
            convertCoordsToInt();
        }
        //ImageButton ourButton = (ImageButton) findViewById(R.id.imgBtnInformation);
        /*ourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gps.canGetLocation()) {
                    updateCoordinates();
                    convertCoordsToInt();
                    theTextView.setText("Your location is\nLat: " + locLatInt + "\nLong: " + locLonInt);
                }
            }
        });*/


        theTextView = (TextView) findViewById(R.id.textHeader);
        
        notificationMain.displayNotification();
        if (notificationMain.mBuilder.build() == null) {
            Toast.makeText(this, "its null", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * These methods facilitate changes between activities
     **/

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void openFavorites(View view) {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void openStationView(View view) {
        Intent intent = new Intent(this, StationActivity.class);
        startActivity(intent);
    }

    public void openDriveView(View view) {
        Intent intent = new Intent(this, DriveActivity.class);
        startActivity(intent);
    }

    /**
     Coordinates methods here below.
     **/

    public void updateCoordinates() {

        /**
         * This pulls coordinates from the GPS Locator
         */

        locLat = gps.getLatitude();
        locLon = gps.getLongitude();
    }

    public void convertCoordsToInt() {

        /**
         * This converts coordinates into a format accepted by the parser.
         */

        locLatInt = (int) locLat * 100000;
        locLonInt = (int) locLon * 100000;
    }


}
