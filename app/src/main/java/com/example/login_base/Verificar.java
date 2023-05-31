package com.example.login_base;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class Verificar extends AppCompatActivity {
    private SharedPreferences preferences1;
    WebView webView;
    Button btnCerrar,btnVeri;
    private TextView año, saludo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar);
       servicios();

        año = findViewById(R.id.textViewAño);
        Calendar fecha = Calendar.getInstance();
        int añoV = fecha.get(Calendar.YEAR);
        año.setText(+añoV+"© DERECHOS RESERVADOS SSP/C4/CSI");


        saludo = findViewById(R.id.textViewSal);
        preferences1 = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        String sal = preferences1.getString("usuario", "");
        //token=token2;
        saludo.setText("Bienvenido, "+sal);

        btnCerrar=findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder verificaDial = new AlertDialog.Builder(Verificar.this);

                verificaDial.setTitle("Salir").
                        setMessage("¿Desea salir de la aplicación? ").
                        setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
                                preferences.edit().clear().commit();
                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = verificaDial.create();
                alertDialog.show();
            }
        });
        btnVeri=findViewById(R.id.btnVeri);
        btnVeri.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LeerActivity.class);
                startActivity(intent);
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.webView);
        String URL = "https://seguridad-privada.sspleon.gob.mx/api/home?api_token=aa2fd1628b7d5bbc64080a75a717a8c0a7546f9c1f3f8aaa6f52bdde4929f9a9#SIOSP-SP";
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(URL);
    }
    private void servicios(){

        if (!isMyServiceRunning(Ubicacion.class)){ //método que determina si el servicio ya está corriendo o no
            Intent localizacion = new Intent(Verificar.this, Ubicacion.class); //serv de tipo Intent
            startService(localizacion); //ctx de tipo Context
            Log.d("App", "Servicio de Localizacion Iniciado");
        } else {
            Log.d("App", "Servicio de Localizacion ya se esta ejecutando");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder verificaDial = new AlertDialog.Builder(Verificar.this);

        verificaDial.setTitle("Salir").
                setMessage("¿Desea salir de la aplicación? ").
                setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = verificaDial.create();
        alertDialog.show();
    }
}