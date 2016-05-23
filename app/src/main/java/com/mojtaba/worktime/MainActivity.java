package com.mojtaba.worktime;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.mojtaba.worktime.datasource.DataSource;
import com.mojtaba.worktime.model.Place;

public class MainActivity extends ListActivity{

    private final String TAG = "Location Manager";

    private Intent service;
    private DataSource dataSource ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataSource = new DataSource(this);
        dataSource.open();
        ArrayAdapter<Place> adapter = new ArrayAdapter<Place>(this,
                android.R.layout.simple_list_item_1, dataSource.getAllPlaces());
        setListAdapter(adapter);
        service = new Intent(this,LocationService.class);
        startService(service);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_new_palce)
        {
            Intent intent = new Intent(this,NewPlaceActivity.class);
            startActivityForResult(intent, 1);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 1)
        {
            Log.i(TAG,"new place!");
            ArrayAdapter<Place> adapter = (ArrayAdapter<Place>) getListAdapter();
            Place place = data.getParcelableExtra("place");
            adapter.add(place);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onDestroy() {
        stopService(service);
        super.onDestroy();
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
