package com.mohit.odiacalendararchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohit.odiacalendararchive.adapter.ImageAdapter;
import com.mohit.odiacalendararchive.model.LoadImage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<LoadImage> loadImagesList;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    DatabaseReference databaseReference;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigation_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        recyclerView = findViewById(R.id.recycler_view);
        loadImagesList = new ArrayList<>();

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        Toast.makeText(MainActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share_link:
                        Intent shareIntent = new Intent(Intent.ACTION_VIEW);
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.debasish.myodiacalendar");
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, "Share Via"));
                        break;
                    case R.id.rate_us:
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.debasish.myodiacalendar");
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(rateIntent);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });

        getListFromFirebaseDatabase();

        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionOnFirst();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void checkConnectionOnFirst() {
        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences noInternet = getSharedPreferences(PREFS_NAME, 0);

        if (noInternet.getBoolean("first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            boolean isAvailable = isNetworkAvailable();
            if(isAvailable) {
                Log.d("CheckConnection", "Connected");
                // record the fact that the app has been started with internet at least once
                noInternet.edit().putBoolean("first_time", false).apply();
            } else {
                Log.d("CheckConnection", "Not Connected");
                Toast.makeText(this, "App requires internet for the first time", Toast.LENGTH_LONG).show();
            }

        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    private void getListFromFirebaseDatabase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //Access firebase offline
        databaseReference = FirebaseDatabase.getInstance().getReference("calendar");
        databaseReference.keepSynced(true); //Even if there is no active listener, download and store the data.
        Query query = databaseReference.orderByChild("year").startAt(2021).endAt(2021);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    LoadImage loadImage = postSnapshot.getValue(LoadImage.class);
                    loadImagesList.add(loadImage);
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView() {
        imageAdapter = new ImageAdapter(this, loadImagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(imageAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }
}