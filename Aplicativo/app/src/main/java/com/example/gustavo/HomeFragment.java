package com.example.gustavo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MyAcitivity";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onStart(){
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cpf = preferences.getString("cpf", "");

        coleteVolumeAtual(cpf);


        GregorianCalendar calendar = new GregorianCalendar();
        int dia = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        int mes = calendar.get(GregorianCalendar.MONTH) + 1;
        int ano = calendar.get(GregorianCalendar.YEAR);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dia+"/"+mes+"/"+ano);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeMillis = new Timestamp(date.getTime()).getTime();
        long timeSecs = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

        coleteDadoDiario(cpf, Long.toString(timeSecs));

        Button btViewMoreHome = (Button) getActivity().findViewById(R.id.btViewMoreHome);

        btViewMoreHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DataActivity.class);
                startActivity(intent);
            }
        });

    }

    private void coleteVolumeAtual(String cpf) {
        String url = "http://192.168.18.6:8080/colete_volume_atual";


        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                Gson g = new Gson();
                Caixa caixa = g.fromJson(response, Caixa.class);
                TextView tvConsumoAtual = getActivity().findViewById(R.id.tvConsumoAtual);
                ImageView imaview = getActivity().findViewById(R.id.imageViewCaixa);

                Log.v(TAG, "index=" + caixa.getVolumeatual());
                Log.v(TAG, "index=" + caixa.getCapacidade());

                float porc_volume = (caixa.getVolumeatual() / caixa.getCapacidade()) * 100;

                if (porc_volume < 10) {
                    imaview.setImageResource(R.mipmap.bar0);
                }else if(porc_volume < 20){
                    imaview.setImageResource(R.mipmap.bar10);
                }else if(porc_volume < 30){
                    imaview.setImageResource(R.mipmap.bar20);
                }else if(porc_volume < 40){
                    imaview.setImageResource(R.mipmap.bar30);
                }else if(porc_volume < 50){
                    imaview.setImageResource(R.mipmap.bar40);
                }else if(porc_volume < 60){
                    imaview.setImageResource(R.mipmap.bar50);
                }else if(porc_volume < 70){
                    imaview.setImageResource(R.mipmap.bar60);
                }else if(porc_volume < 80){
                    imaview.setImageResource(R.mipmap.bar70);
                }else if(porc_volume < 90){
                    imaview.setImageResource(R.mipmap.bar80);
                }else if(porc_volume < 100){
                    imaview.setImageResource(R.mipmap.bar90);
                }else if(porc_volume == 100){
                    imaview.setImageResource(R.mipmap.bar100);
                }

                DecimalFormat df = new DecimalFormat("0.00");
                DecimalFormat d1 = new DecimalFormat("0");
                tvConsumoAtual.setText(df.format(porc_volume) + "%\n"+d1.format(caixa.getVolumeatual()) +"/"+d1.format(caixa.getCapacidade())+"L");
                //tvConsumoAtual.setText("TESTE");
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

    private void coleteDadoDiario(String cpf, String timestamp) {
        String url = "http://192.168.18.6:8080/colete_dado_diario";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson g = new Gson();
                Informacoes informacoes = g.fromJson(response, Informacoes.class);

                TextView tvConsumoDadoHome = getActivity().findViewById(R.id.tvConsumoDadoHome);
                TextView tvEntradaDadoHome = getActivity().findViewById(R.id.tvEntradaDadoHome);
                TextView tvUltimaLeituraHome = getActivity().findViewById(R.id.tvUltimaLeituraDadoHome);

                DecimalFormat df = new DecimalFormat("0.00");

                tvConsumoDadoHome.setText("" + df.format(informacoes.getConsumo())+" Litros");
                tvEntradaDadoHome.setText("" + df.format(informacoes.getEntrada())+" Litros");
                tvUltimaLeituraHome.setText("" + informacoes.getData());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cpf", cpf);
                params.put("timestamp", timestamp);

                return params;
            }
        };
        queue.add(request);
    }


}