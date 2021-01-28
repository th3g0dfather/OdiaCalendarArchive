package com.debasish.odiacalendararchive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.debasish.odiacalendararchive.adapter.ImageAdapter;
import com.debasish.odiacalendararchive.adapter.LoadImage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        setUpRecyclerView();

        databaseReference = FirebaseDatabase.getInstance().getReference("calendar");
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void setUpRecyclerView() {
        loadImagesList = new ArrayList<>();
        imageAdapter = new ImageAdapter(loadImagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setOnFlingListener(null);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }
}