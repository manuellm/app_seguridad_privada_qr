package com.example.login_base;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LeerActivity extends AppCompatActivity {
    private SharedPreferences preferences1;
    WebView webView;
    Button btnCerrar,btnVerHuella,btnPersona;
    TextView etCodigo;
    String codigo;
    TextView token3;
    TextView nombre;
    TextView apMaterno;
    TextView apPaterno, folio;
    WebView ver;
    String token;
    String url;
    String url2;
    private TextView año;
    private Button WebViewQR;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer);

        preferences1 = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        String token2 = preferences1.getString("api_token", "");
        token=token2;
        año = findViewById(R.id.textViewAño);
        Calendar fecha = Calendar.getInstance();
        int añoV = fecha.get(Calendar.YEAR);
        año.setText(+añoV+"© DERECHOS RESERVADOS SSP/C4/CSI");

        btnCerrar=findViewById(R.id.btnCerrar);
        btnPersona=findViewById(R.id.btnPersona);
        etCodigo= findViewById(R.id.etCodigo);etCodigo.setVisibility(View.INVISIBLE);
        nombre=findViewById(R.id.nombre);nombre.setVisibility(View.INVISIBLE);
        apPaterno=findViewById(R.id.apep);apPaterno.setVisibility(View.INVISIBLE);
        apMaterno=findViewById(R.id.apm);apMaterno.setVisibility(View.INVISIBLE);
        folio=findViewById(R.id.fol);folio.setVisibility(View.INVISIBLE);
        ver = (WebView) findViewById(R.id.wvQR);

        queue = Volley.newRequestQueue(this);

        btnPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRed();
            }
        });
        btnCerrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder verificaDial = new AlertDialog.Builder(LeerActivity.this);
                verificaDial.setTitle("Salir").
                        setMessage("¿Desea salir del apartado? ").
                        setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = verificaDial.create();
                alertDialog.show();
            }
        });
    }

    private void verificarRed(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            escanear();
        } else {
            AlertDialog.Builder verificaDial = new AlertDialog.Builder(LeerActivity.this);

            verificaDial.setTitle("Sin conexión a internet").
                    setMessage("Verifica tu conexión a internet.\n(Datos móviles o Wifi)").
                    setPositiveButton("Aceptar", null);
            AlertDialog alertDialog = verificaDial.create();
            alertDialog.show();
        }
    }

    public void escanear(){
        IntentIntegrator intent= new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("COLOCA EL GAFETE DEBAJO DE LA CAMARA");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setCaptureActivity(Vertical.class);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if(result !=null){
            if (result.getContents()== null){
                AlertDialog.Builder escanerDial=new AlertDialog.Builder(LeerActivity.this);
                //constructor
                escanerDial.setTitle("CANCELAR ").
                        setMessage("Has cancelado el escaneo").
                        setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alertDialog=escanerDial.create();
                alertDialog.show();
            }else{
                etCodigo.setText(result.getContents().toString());
                codigo= etCodigo.getText().toString();

                String u1 = getString(R.string.u1);
                String u2 = getString(R.string.u2);
                url= u1+codigo+u2;
                validaQr(""+url);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void validaQr(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(String response) {
                JSONObject objN = null;
                JSONObject objAM = null;
                JSONObject objAP = null;
                JSONObject objFo = null;
                try {
                    objN = new JSONObject(response);
                    objAM = new JSONObject(response);
                    objAP = new JSONObject(response);
                    objFo = new JSONObject(response);

                    if(!response.isEmpty()){
                        String nom = objN.get("nombre").toString();
                        // nombre.setText(nom);
                        String apm = objAM.get("app").toString();
                        //apMaterno.setText(apm);
                        String apep = objAP.get("apm").toString();
                        //apPaterno.setText(apep);
                        String fol = objFo.get("folio").toString();
                        //folio.setText(fol);
                        String huella = objFo.get("huella_id").toString();
                        String enroll = objFo.get("enrolado").toString();

                        url2= (url+"kardex?api_token="+token);
                        guardarPrefQR(nom,apm,apep,fol,huella,enroll);
                        ver.getSettings().setJavaScriptEnabled(true);
                        ver.getSettings().setAppCacheEnabled(true);
                        ver.getSettings().setSaveFormData(true);
                        ver.getSettings().setDomStorageEnabled(true);
                        ver.getSettings().setBuiltInZoomControls(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
                        {
                            ver.getSettings().setPluginState(WebSettings.PluginState.ON);
                        }
                        ver.loadUrl(""+url2);
                        ver.setWebViewClient(new WebViewClient() {
                            // evita que los enlaces se abran fuera nuestra app en el navegador de android
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                return false;
                            }
                        });
                    }
                    else{
                        AlertDialog.Builder escanerDial=new AlertDialog.Builder(LeerActivity.this);
                        //constructor
                        escanerDial.setTitle("ERROR ").
                                setMessage("CODIGO: QR-001").
                                setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        AlertDialog alertDialog=escanerDial.create();
                        alertDialog.show();
                    } } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder novalDial=new AlertDialog.Builder(LeerActivity.this);
                //constructor
                novalDial.setTitle("QR NO VALIDO").
                        setMessage("El codigo QR escaneado, no pertenece a la corporacion ").
                        setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alertDialog=novalDial.create();
                alertDialog.show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String, String>();
                parametros.put("api_token", token);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void guardarPrefQR(String nom, String apm, String apep, String fol, String huella, String enroll)
    {
        SharedPreferences preferences=getSharedPreferences("preferenciasQR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("nombre", nom);
        editor.putString("app",apep );
        editor.putString("apm", apm);
        editor.putString("huella_id", huella);
        editor.putString("folio", fol);
        editor.putString("enrolado", enroll);
        editor.commit();
    }
    public void onBackPressed() {
        AlertDialog.Builder verificaDial = new AlertDialog.Builder(LeerActivity.this);
        verificaDial.setTitle("Salir").
                setMessage("¿Desea salir del apartado? ").
                setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = verificaDial.create();
        alertDialog.show();
    }
}
