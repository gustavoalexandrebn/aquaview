package com.example.gustavo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
    }

    protected void onStart(){
        super.onStart();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bnViewData);
        NavController navController = Navigation.findNavController(this,  R.id.fcvData);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.consumoFragment);
        topLevelDestinations.add(R.id.entradaFragment);

        AppBarConfiguration abc = new AppBarConfiguration.Builder(R.id.consumoFragment, R.id.entradaFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, abc);

    }

    public void aumentarData(View view) {
    }

    public void diminuirData(View view) {
    }
}