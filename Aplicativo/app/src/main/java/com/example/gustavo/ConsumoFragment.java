package com.example.gustavo;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsumoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsumoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int hoje = 0;
    private int semana = 0;
    private int mes = 0;
    private int ano = 0;
    private int refEscala;

    public ConsumoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsumoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsumoFragment newInstance(String param1, String param2) {
        ConsumoFragment fragment = new ConsumoFragment();
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
        return inflater.inflate(R.layout.fragment_consumo, container, false);
    }

    public void onStart() {
        super.onStart();

        TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
        tvDataData.setText(coleteDataDiaMesAno());

        // Gerar gráfico

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cpf = preferences.getString("cpf", "");

        coleteConsumoPorData(cpf, coleteDataDiaMesAno(), atualizeDia(1), "dia");

        // Trocar de informações
        Button btDiaData = getActivity().findViewById(R.id.btDiaData);
        Button btSemanaData = getActivity().findViewById(R.id.btSemanaData);
        Button btMesData = getActivity().findViewById(R.id.btMesData);
        Button btAnoData = getActivity().findViewById(R.id.btAnoData);

        btDiaData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                btDiaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffa500")));
                btDiaData.setTextColor(Color.BLACK);

                btSemanaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btSemanaData.setTextColor(Color.WHITE);

                btMesData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btMesData.setTextColor(Color.WHITE);

                btAnoData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btAnoData.setTextColor(Color.WHITE);

                setRefEscala(0);
                setHoje(0);
                TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                tvDataData.setText(coleteDataDiaMesAno());
                coleteConsumoPorData(cpf, coleteDataDiaMesAno(), atualizeDia(1), "dia");
            }
        });


        btSemanaData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //btSemanaData.setBackgroundTintMode();

                ///android:backgroundTint="#ffa500"
                //android:textColor="@color/black"

                btDiaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btDiaData.setTextColor(Color.WHITE);

                btSemanaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffa500")));
                btSemanaData.setTextColor(Color.BLACK);

                btMesData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btMesData.setTextColor(Color.WHITE);

                btAnoData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btAnoData.setTextColor(Color.WHITE);


                setRefEscala(1);
                GregorianCalendar calendar = new GregorianCalendar();

                if (calendar.get(GregorianCalendar.DAY_OF_WEEK)==0){
                    setSemana(7);
                }else{
                    setSemana(calendar.get(GregorianCalendar.DAY_OF_WEEK)-1);
                }

                TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                tvDataData.setText(atualizeSemana((getSemana()-1)-getSemana()) + " - " + atualizeSemana(7-getSemana()));
                coleteConsumoPorData(cpf, atualizeDia((getSemana()-1)-getSemana()), atualizeDia(7-getSemana()+1), "semana");
            }
        });

        btMesData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                btDiaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btDiaData.setTextColor(Color.WHITE);

                btSemanaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btSemanaData.setTextColor(Color.WHITE);

                btMesData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffa500")));
                btMesData.setTextColor(Color.BLACK);

                btAnoData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btAnoData.setTextColor(Color.WHITE);

                setRefEscala(2);
                setMes(0);
                TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                int mesInt = coleteMes();
                tvDataData.setText(intToStringMes(mesInt));
                coleteConsumoPorData(cpf, atualizeMesRecebaAno(getMes()), atualizeMesRecebaAno(getMes()+1), "mes");
            }
        });

        btAnoData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                btDiaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btDiaData.setTextColor(Color.WHITE);

                btSemanaData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btSemanaData.setTextColor(Color.WHITE);

                btMesData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7742db")));
                btMesData.setTextColor(Color.WHITE);

                btAnoData.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffa500")));
                btAnoData.setTextColor(Color.BLACK);

                setRefEscala(3);
                setAno(0);
                TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                tvDataData.setText(coleteAno() + "");
                coleteConsumoPorData(cpf, atualizeAnoImage(getAno()), atualizeAnoImage(getAno()+1), "ano");
            }
        });

        //Trocar de dia
        TextView tvDiminuirData = getActivity().findViewById(R.id.tvDiminuirData);
        TextView tvAumentarData = getActivity().findViewById(R.id.tvAumentarData);

        tvDiminuirData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRefEscala() == 0) {
                    setHoje(getHoje() - 1);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(atualizeDia(getHoje()));
                    coleteConsumoPorData(cpf, atualizeDia(getHoje()), atualizeDia(getHoje()+1), "dia");
                } else if (getRefEscala() == 1) {
                    setSemana(getSemana() - 7);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(atualizeSemana(getSemana() - 7) + " - " + atualizeSemana(getSemana()));
                    coleteConsumoPorData(cpf, atualizeDia(getSemana() - 7), atualizeDia(getSemana()+1), "semana");
                } else if (getRefEscala() == 2) {
                    setMes(getMes() - 1);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(intToStringMes(atualizeMes(getMes())));
                    coleteConsumoPorData(cpf, atualizeMesRecebaAno(getMes()), atualizeMesRecebaAno(getMes()+1), "mes");
                } else if (getRefEscala() == 3) {
                    setAno(getAno() - 1);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(atualizeAno(getAno()) + "");
                    coleteConsumoPorData(cpf, atualizeAnoImage(getAno()), atualizeAnoImage(getAno()+1), "ano");
                }
            }
        });

        tvAumentarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRefEscala() == 0) {
                    setHoje(getHoje() + 1);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(atualizeDia(getHoje()));
                    coleteConsumoPorData(cpf, atualizeDia(getHoje()), atualizeDia(getHoje()+1), "dia");
                } else if (getRefEscala() == 1) {
                    setSemana(getSemana() + 7);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(atualizeSemana(getSemana() + 7) + " - " + atualizeSemana(getSemana()));
                    coleteConsumoPorData(cpf, atualizeDia(getSemana() - 7), atualizeDia(getSemana()+1), "semana");
                } else if (getRefEscala() == 2) {
                    setMes(getMes() + 1);
                    int anoAtual = atualizeMesAno(getMes());
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(intToStringMes(atualizeMes(getMes())));
                    coleteConsumoPorData(cpf, atualizeMesRecebaAno(getMes()), atualizeMesRecebaAno(getMes()+1), "mes");
                } else if (getRefEscala() == 3) {
                    setAno(getAno() + 1);
                    TextView tvDataData = getActivity().findViewById(R.id.tvDataData);
                    tvDataData.setText(atualizeAno(getAno()) + "");
                    coleteConsumoPorData(cpf, atualizeAnoImage(getAno()), atualizeAnoImage(getAno()+1), "ano");
                }
            }
        });
    }

    public String coleteDataDiaMesAno() {
        GregorianCalendar calendar = new GregorianCalendar();
        int dia = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        int mes = calendar.get(GregorianCalendar.MONTH) + 1;
        int ano = calendar.get(GregorianCalendar.YEAR);

        if (dia < 10 && mes >= 10) {
            return "0" + dia + "/" + mes + "/" + ano;
        } else if (dia < 10 && mes < 10) {
            return "0" + dia + "/0" + mes + "/" + ano;
        } else {
            return dia + "/" + mes + "/" + ano;
        }
    }

    public String coleteSemana() {
        GregorianCalendar calendar = new GregorianCalendar();
        int dia = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        int mes = calendar.get(GregorianCalendar.MONTH) + 1;

        if (dia < 10 && mes >= 10) {
            return "0" + dia + "/" + mes;
        } else if (dia < 10 && mes < 10) {
            return "0" + dia + "/0" + mes;
        } else {
            return dia + "/" + mes;
        }
    }

    public String atualizeAnoImage(int anos) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, anos);
        calendar.get(GregorianCalendar.YEAR);

        return "01/01/"+calendar.get(GregorianCalendar.YEAR);
    }

    public int coleteAno() {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.get(GregorianCalendar.YEAR);
    }

    public int coleteMes() {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.get(GregorianCalendar.MONTH);
    }

    public String intToStringMesTexto(int mes) {
        String nomeMes = "";

        switch (mes) {
            case 0:
                nomeMes = "01/01";
                break;
            case 1:
                nomeMes = "Fev";
                break;
            case 2:
                nomeMes = "Mar";
                break;
            case 3:
                nomeMes = "Abr";
                break;
            case 4:
                nomeMes = "Mai";
                break;
            case 5:
                nomeMes = "Jun";
                break;
            case 6:
                nomeMes = "Jul";
                break;
            case 7:
                nomeMes = "Ago";
                break;
            case 8:
                nomeMes = "Set";
                break;
            case 9:
                nomeMes = "Out";
                break;
            case 10:
                nomeMes = "Nov";
                break;
            case 11:
                nomeMes = "Dez";
                break;
            default:
                break;
        }

        return nomeMes;
    }


    public String intToStringMes(int mes) {
        String nomeMes = "";

        switch (mes) {
            case 0:
                nomeMes = "Jan";
                break;
            case 1:
                nomeMes = "Fev";
                break;
            case 2:
                nomeMes = "Mar";
                break;
            case 3:
                nomeMes = "Abr";
                break;
            case 4:
                nomeMes = "Mai";
                break;
            case 5:
                nomeMes = "Jun";
                break;
            case 6:
                nomeMes = "Jul";
                break;
            case 7:
                nomeMes = "Ago";
                break;
            case 8:
                nomeMes = "Set";
                break;
            case 9:
                nomeMes = "Out";
                break;
            case 10:
                nomeMes = "Nov";
                break;
            case 11:
                nomeMes = "Dez";
                break;
            default:
                break;
        }

        return nomeMes;
    }

    public int atualizeAno(int anos) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, anos);

        return calendar.get(GregorianCalendar.YEAR);
    }
    public String atualizeMesRecebaAno(int mes) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, mes);

        String data = "";

        if ((calendar.get(GregorianCalendar.MONTH)+1)<9){
            data = "01/0"+(calendar.get(GregorianCalendar.MONTH)+1)+"/"+calendar.get(GregorianCalendar.YEAR);
        }else{
            data = "01/"+(calendar.get(GregorianCalendar.MONTH)+1)+"/"+calendar.get(GregorianCalendar.YEAR);
        }

        return data;
    }

    public int atualizeMes(int mes) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, mes);

        return calendar.get(GregorianCalendar.MONTH);
    }

    public int atualizeMesAno(int mes) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, mes);

        return calendar.get(GregorianCalendar.YEAR);
    }


    public String atualizeSemana(int dias) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, dias);

        int dia = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        int mes = calendar.get(GregorianCalendar.MONTH) + 1;

        if (dia < 10 && mes >= 10) {
            return "0" + dia + "/" + mes;
        } else if (dia < 10 && mes < 10) {
            return "0" + dia + "/0" + mes;
        } else {
            return dia + "/" + mes;
        }

    }

    public String atualizeDia(int dias) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, dias);

        int dia = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        int mes = calendar.get(GregorianCalendar.MONTH) + 1;
        int ano = calendar.get(GregorianCalendar.YEAR);

        if (dia < 10 && mes >= 10) {
            return "0" + dia + "/" + mes + "/" + ano;
        } else if (dia < 10 && mes < 10) {
            return "0" + dia + "/0" + mes + "/" + ano;
        } else {
            return dia + "/" + mes + "/" + ano;
        }

    }

    private void coleteConsumoPorData(String cpf, String dataInicial, String dataFinal, String tipoData) {
        String url = "http://192.168.18.6:8080/colete_consumo_entre_datas";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        System.out.println(dataInicial + "" + dataFinal);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson g = new Gson();
                Informacoes info = g.fromJson(response, Informacoes.class);

                //ImageView ivConsumoData = getActivity().findViewById(R.id.ivConsumoData);

                System.out.println(info);
                TextView tvConsumoTotal = getActivity().findViewById(R.id.tvConsumoTotal);
                TextView tvConsumoMedio = getActivity().findViewById(R.id.tvConsumoMedio);
                DecimalFormat df = new DecimalFormat("0.00");
                tvConsumoTotal.setText("Consumo Total\n"+df.format(info.getConsumo())+" Litros");
                if (info.getConsumomedio() == 0){
                    tvConsumoMedio.setText("Consumo Médio\n-");
                }else{
                    tvConsumoMedio.setText("Consumo Médio\n"+df.format(info.getConsumomedio())+" Litros");
                }


                coleteImagem(info.getUlrfig());
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
                params.put("datainicial", dataInicial);
                params.put("datafinal", dataFinal);
                params.put("tipodedata", tipoData);

                return params;
            }
        };
        queue.add(request);
    }

    private void coleteImagem(String url_imagem) {
        String url = "http://192.168.18.6:8080/get-files/"+url_imagem;
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        ImageRequest imageRequest = new ImageRequest(
                url, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                        // Do something with response
                        ImageView ivConsumoData = getActivity().findViewById(R.id.ivConsumoData);
                        int currentBitmapWidth = response.getWidth();
                        int currentBitmapHeight = response.getHeight();

                        int ivWidth = ivConsumoData.getWidth();
                        int ivHeight = ivConsumoData.getHeight();
                        int newWidth = ivWidth;

                        int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

                        Bitmap newbitMap = Bitmap.createScaledBitmap(response, newWidth, newHeight, true);

                        ivConsumoData.setImageBitmap(newbitMap);
                    }
                },
                640, // Image width
                480, // Image height
                ImageView.ScaleType.CENTER_INSIDE, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                    }
                }
        );

        queue.add(imageRequest);
    }

    public int getHoje() {
        return hoje;
    }

    public void setHoje(int hoje) {
        this.hoje = hoje;
    }


    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getRefEscala() {
        return refEscala;
    }

    public void setRefEscala(int refEscala) {
        this.refEscala = refEscala;
    }
}