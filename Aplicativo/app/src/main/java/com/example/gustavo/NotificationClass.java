package com.example.gustavo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class NotificationClass extends Worker {
    public NotificationClass(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }


    @NonNull
    @Override
    public Result doWork() {
        String CPF = getInputData().getString("CPF");
        coleteVolumeAtual(CPF);
        return Result.success();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Água";
            String description = "Aguawe";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            Context ctx = getApplicationContext();
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notificationIntent(){
        Context ctx = getApplicationContext();

        Intent intent = new Intent(ctx, HomeActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "1")
                .setSmallIcon(R.mipmap.data)
                .setContentTitle("Water Tank")
                .setContentText("O nível do reservatório está abaixo de 10%!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        createNotificationChannel();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(25, builder.build());
    }



    private void coleteVolumeAtual(String cpf) {
        String url = "http://192.168.18.6:8080/colete_volume_atual";
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());


        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                Gson g = new Gson();
                Caixa caixa = g.fromJson(response, Caixa.class);
                float porc_volume = (caixa.getVolumeatual() / caixa.getCapacidade()) * 100;
                //System.out.println(porc_volume);
                if (porc_volume < 10) {
                    notificationIntent();
                }
            }
        }, error -> ContextCompat.getMainExecutor(getApplicationContext()).execute(() -> Toast.makeText(getApplicationContext(),
                "Fail to get response = " + error, Toast.LENGTH_SHORT).show())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cpf", cpf);

                return params;
            }
        };
        queue.add(request);
    }
}
