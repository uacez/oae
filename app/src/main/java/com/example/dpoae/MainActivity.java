package com.example.dpoae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int grantResults[];
    Spinner spinner;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.nav = findViewById(R.id.bottom_navigation);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);
        onRequestPermissionsResult(1,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.init(this);
        initView();
        initBottomNav();
    }

    public void initView() {
        if (Constants.CurrentFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.CurrentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.MeasureFragment).commit();
            Constants.nav.setSelectedItemId(Constants.nav.getMenu().getItem(0).getItemId());
        }
    }

    public void initBottomNav() {
        if (Constants.nav!=null) {
            Constants.nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
//                        case R.id.action_prepare:
//                            selectedFragment = Constants.PrepareFragment;
//                            break;
                        case R.id.action_measure:
                            selectedFragment = Constants.MeasureFragment;
                            break;
                        case R.id.action_settings:
                            selectedFragment = Constants.SettingsFragment;
                            break;
                        default:
                            selectedFragment = Constants.CurrentFragment;
                            break;
                    }
                    if (selectedFragment != null && !Constants.testInProgress) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    }
                    return true;
                }
            });
        }
    }
}