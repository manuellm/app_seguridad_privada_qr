package com.example.login_base;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ValidarActivity extends AppCompatActivity {
    private TelephonyManager manager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    TextView imei, num;
    String wsimei, wsnum;
    ProgressBar progressBar;
    private TextView año;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar);
        imei = (TextView) findViewById(R.id.imei2); imei.setVisibility(View.INVISIBLE);
        num = (TextView) findViewById(R.id.num2); num.setVisibility(View.INVISIBLE);
        año = findViewById(R.id.textViewAño);
        Calendar fecha = Calendar.getInstance();
        int añoV = fecha.get(Calendar.YEAR);
        año.setText(+añoV+"© DERECHOS RESERVADOS SSP/C4/CSI");

        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        preferences=getSharedPreferences("datosDispositivo", Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(ValidarActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ValidarActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1000);
            } else {
                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                String imeil = Settings.Secure.getString(
                        ValidarActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String numero = manager.getLine1Number();
                imei.setText(imeil);
                num.setText(numero);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1000);
            } else {
                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (manager == null) {
                    Toast.makeText(this, "Dispositivo sin IMEI", Toast.LENGTH_LONG).show();
                } else {
                    String imeil = manager.getImei();
                    String numero = manager.getLine1Number();
                    imei.setText(imeil);
                    num.setText(numero);
                }
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wsimei=imei.getText().toString();
                wsnum=num.getText().toString();
                editor.putString("Imei", wsimei);
                editor.putString("Num", wsnum);
                editor.commit();
               // validacion("https://seguridad-privada.sspleon.gob.mx/api/imei");
                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent2);
                finish();
            }
        }, 2000);

    }

    private void validacion(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject objResultado = null;
                try {
                    objResultado = new JSONObject(response);
                    String msj = objResultado.get("message").toString();
                    if (!msj.contains("IMEI VALIDO")) {
                        Intent intent = new Intent(getApplicationContext(),PrincipalActivity.class);
                        startActivity(intent);
                        Enviar2();
                    } else {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
                        boolean sesion = preferences.getBoolean("session", false);
                        if (sesion) {
                            Intent intent2 = new Intent(getApplicationContext(),Verificar.class);
                            startActivity(intent2);
                            finish();
                        } else {
                            Intent intent3 = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent3);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    Toast.makeText(LoginActivity.this, "conexion Invalida", Toast.LENGTH_LONG).show();
                ///////////dialogo1
                AlertDialog.Builder verificaDial=new AlertDialog.Builder(ValidarActivity.this);
                //constructor
                verificaDial.setTitle(" Error al conectar ").
                        setMessage("Revisa tu conexion Wifi o datos moviles ").
                        setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ValidarActivity.this, Verificar.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog=verificaDial.create();
                alertDialog.show();
                ///////////////
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("imei", wsimei);
                params.put("telefono", wsnum);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void Enviar2() {
        Intent i = new Intent (this, PrincipalActivity.class);
        i.putExtra("pImei", imei.getText().toString());
        startActivity(i);
    }
}