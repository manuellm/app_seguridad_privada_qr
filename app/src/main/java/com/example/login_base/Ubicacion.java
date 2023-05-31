package com.example.login_base;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class Ubicacion extends Service {
    private SharedPreferences preferences1, preferences;
    private SharedPreferences.Editor editor;
    CountDownTimer countDownTimer;
    private String direccion,
            URL= "https://seguridad-privada.sspleon.gob.mx/api/ubicacion";
  TimerTask task;
    Timer timer;

    public void onCreate() {
        super.onCreate();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Sin permisos");
        } else {
            locationStart();
        }
        /*countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                guardarUbicacion(URL);
                start();
            }
        };*/
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
          guardarUbicacion(URL);
            }
        };
        preferences1 = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        preferences = getSharedPreferences("datosDispositivo", Context.MODE_PRIVATE);
}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        editor = preferences.edit();
      timer.schedule(task, 10,120000);
      //  countDownTimer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onStop()");
       task.cancel();
        //countDownTimer.cancel();
        Toast.makeText(this, "Servicio de Localización detenido...", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Servicio de Localización stopped...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizar Local = new Localizar();
        Local.setValidar(this);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
       // System.out.println("Localización agregada");
        direccion="";
    }

    public class Localizar implements LocationListener {
        Ubicacion localizacion;

        public Ubicacion getValidar() {
            return localizacion;
        }
        public void setValidar(Ubicacion localizacion) {
            this.localizacion = localizacion;
        }
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String sLatitud = String.valueOf(loc.getLatitude());
            String sLongitud = String.valueOf(loc.getLongitude());
            editor.putString("Latitud", sLatitud);
            editor.putString("Longitud", sLongitud);
            editor.commit();
            this.localizacion.setLocation(loc);
          //  System.out.println("Latitud: "+myPreferences.getString("Latitud","0")+"\nLongitud: "+myPreferences.getString("Longitud","0")+"\nDireccion: "+direccion);
           // System.out.println("Latitud: "+sLatitud+"\nLongitud: "+sLongitud+"\nDireccion: "+direccion);
        }
        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion=DirCalle.getAddressLine(0);
                    editor.putString("Direccion", DirCalle.getAddressLine(0));
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void guardarUbicacion(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject objResultado = null;
                try {
                    objResultado = new JSONObject(response);
                    System.out.println("Registro No. "+objResultado.getJSONObject("ubicacion").optString("id"));
                    if (objResultado==null){
                      //  guardarUbicacionElementoLite();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Sin conexión al servidor.\nPor favor inténtelo mas tarde.");
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("aplicacion", "Verificacion_QR");
                params.put("imei", preferences.getString("imei", ""));
                params.put("telefono", preferences.getString("num", "52"));
                params.put("usuario", preferences1.getString("usuario",""));
                params.put("latitude", preferences.getString("Latitud","0"));
                params.put("longitude", preferences.getString("Longitud", "0"));
                params.put("api_token", preferences1.getString("api_token", ""));
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
