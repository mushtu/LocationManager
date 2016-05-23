package com.mojtaba.worktime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mojtaba.worktime.datasource.DataSource;
import com.mojtaba.worktime.model.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by mojtaba on 6/3/15.
 */
public class NewPlaceActivity extends FragmentActivity {

    private DataSource dataSource ;

    private GoogleMap mMap;
    private BroadcastReceiver receiver;
    private Marker marker ;
    private EditText txtName;
    private EditText txtDescription;
    private Location location ;
    private Geocoder geocoder;
    private List<Address> addresses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_place);
        setUpMapIfNeeded();
        dataSource = new DataSource(this);
        txtName = (EditText)findViewById(R.id.txtName);
        txtDescription = (EditText)findViewById(R.id.txtDescription);
        geocoder = new Geocoder(this, Locale.getDefault());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                location= intent.getParcelableExtra(LocationService.LOCATION_MESSAGE);
                if(mMap != null)
                {
                    LatLng newPos = new LatLng(location.getLatitude(),location.getLongitude()) ;
                    MarkerOptions options = new MarkerOptions().position(newPos).draggable(true) ;
                    if(marker == null)
                        marker = mMap.addMarker(options);
                    else marker.setPosition(newPos);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(LocationService.LOCATION_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newplace, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.save_place)
        {
            if(txtName.getText().toString().length() < 1 || location == null)
                return true;
            String name ="";
            String description ="" ;
            String googleAddress = "";
            name = txtName.getText().toString();
            description = txtDescription.getText().toString();
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addresses != null && addresses.size() > 0)
            {
                googleAddress = addresses.get(0).getAddressLine(0);
            }
            Place place = new Place();
            place = dataSource.createPlace(name, description,
                    googleAddress,location.getLatitude(), location.getLongitude());
            Intent result = new Intent();
            result.putExtra("place",place);
            setResult(RESULT_OK,result);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            return;
        }
        // Initialize map options. For example:
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    @Override
    protected void onResume() {
        if(!dataSource.isOpen())
            dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(dataSource.isOpen())
            dataSource.close();
        super.onPause();
    }
}