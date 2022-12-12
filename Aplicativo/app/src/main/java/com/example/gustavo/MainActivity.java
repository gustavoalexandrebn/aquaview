package com.example.gustavo;


import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private WorkRequest notificationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView tLogin = (TextView) findViewById(R.id.tLogin);
                TextView tSenha = (TextView) findViewById(R.id.tSenha);

                String sLogin = tLogin.getText().toString();
                String sSenha = tSenha.getText().toString();

                Login(sLogin, sSenha);
            }
        });
    }


    private void Login(String cpf, String senha) {
        String url = "http://192.168.18.6:8080/login";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson g = new Gson();
                Morador morador = g.fromJson(response, Morador.class);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("usuario", morador.getUsuario());
                editor.putString("endereco", morador.getEndereco());
                editor.putString("telefone", morador.getTelefone());
                editor.putString("cpf", cpf);
                editor.putString("email",morador.getEmail());
                editor.apply();

                if (morador.getStatus().equals("LA")){

                    Data.Builder data = new Data.Builder();
                    data.putString("CPF", cpf);

                    PeriodicWorkRequest notificationRequest = new PeriodicWorkRequest.Builder(NotificationClass.class, 10,
                            TimeUnit.SECONDS).setInputData(data.build()).build();

                    //WorkManager.getInstance(this).enqueue(notificationRequest);
                    WorkManager.getInstance().enqueue(notificationRequest);

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else if(morador.getStatus().equals("SI")){
                    alert("Senha Incorreta");
                }else{
                    alert("Usuário não cadastrado");
                    alert(morador.getStatus());
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cpf", cpf);
                params.put("senha", senha);

                return params;
            }
        };
        queue.add(request);
    }

    private void alert(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}