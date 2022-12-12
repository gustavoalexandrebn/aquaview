package com.example.gustavo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link perfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class perfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public perfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment perfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static perfilFragment newInstance(String param1, String param2) {
        perfilFragment fragment = new perfilFragment();
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
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    public void onStart(){
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String usuario = preferences.getString("usuario", "");
        String endereco = preferences.getString("endereco", "");
        String telefone = preferences.getString("telefone", "");
        String cpf = preferences.getString("cpf", "");
        String email = preferences.getString("email", "");


        EditText etUsuarioPf = getActivity().findViewById(R.id.etUsuarioPf);
        EditText etEnderecoPf = getActivity().findViewById(R.id.etEnderecoPf);
        EditText etTelefonePf =  getActivity().findViewById(R.id.etTelefonePf);
        EditText etCPFPf =  getActivity().findViewById(R.id.etCPFPf);
        EditText etEmailPf = getActivity().findViewById(R.id.etEmailPf);

        etUsuarioPf.setText(usuario);
        etEnderecoPf.setText(endereco);
        etTelefonePf.setText(telefone);
        etCPFPf.setText(cpf);
        etEmailPf.setText(email);

        etUsuarioPf.setEnabled(false);
        etEnderecoPf.setEnabled(false);
        etTelefonePf.setEnabled(false);
        etCPFPf.setEnabled(false);
        etEmailPf.setEnabled(false);

        Button btSairAppPf = (Button) getActivity().findViewById(R.id.btSairAppPf);

        btSairAppPf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button btSolicitarAguaPf = (Button) getActivity().findViewById(R.id.btSolicitarAguarPf);

        btSolicitarAguaPf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                coleteVolumeAtual(cpf);
                btSolicitarAguaPf.setClickable(false);
                btSolicitarAguaPf.setText("Pedido realizado!");
            }
        });
    }

    private void coleteVolumeAtual(String cpf) {
        String url = "http://192.168.18.6:8080/colete_volume_atual";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson g = new Gson();
                Caixa caixa = g.fromJson(response, Caixa.class);

                String volume = String.valueOf(caixa.getCapacidade() - caixa.getVolumeatual());
                solicitarAgua(cpf, volume);
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

    private void solicitarAgua(String cpf, String volumeEmFalta) {
        String url = "http://192.168.18.6:8080/solicitar_agua";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TextView tView2 = findViewById(R.id.teste2);
                //tView2.setText("Sua caixa d'agua possui litros: "+getVolume());
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
                params.put("volumeemfalta", volumeEmFalta);

                return params;
            }
        };
        queue.add(request);
    }

    private void alert(String s){
        Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}