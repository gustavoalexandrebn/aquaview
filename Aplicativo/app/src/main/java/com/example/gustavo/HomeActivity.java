package com.example.gustavo;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HomeActivity  extends AppCompatActivity {
    public String volume;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        String usuario = getIntent().getStringExtra("cpf");

        //TextView tView1 = findViewById(R.id.tview1);

        //postVolume(usuario);

        //tView1.setText("Bem vindo "+login+"!");
    }

    protected void onStart(){
        super.onStart();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView1);
        NavController navController = Navigation.findNavController(this,  R.id.fragmentContainerView2);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment2);
        topLevelDestinations.add(R.id.perfilFragment2);

        AppBarConfiguration abc = new AppBarConfiguration.Builder(R.id.homeFragment2, R.id.perfilFragment2).build();

        NavigationUI.setupActionBarWithNavController(this, navController, abc);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void postVolume(String cpf) {
        String url = "http://192.168.18.6:8080/get_volume_medium_from_user";
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setVolume(response);

                //TextView tView2 = findViewById(R.id.teste2);
                //tView2.setText("Sua caixa d'agua possui litros: "+getVolume());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cpf", cpf);

                return params;
            }
        };
        queue.add(request);
    }




    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
