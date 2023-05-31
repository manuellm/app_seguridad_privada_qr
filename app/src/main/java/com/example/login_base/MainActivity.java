package com.example.login_base;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText edtUsuario,edtPassword;
    Button btnLogin;
    String usuario,password;
    String token2;
    private TextView año;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        año = findViewById(R.id.textViewAño);
        Calendar fecha = Calendar.getInstance();
        int añoV = fecha.get(Calendar.YEAR);
        año.setText(+añoV+"© DERECHOS RESERVADOS SSP/C4/CSI");

        edtUsuario=findViewById(R.id.edtUsuario);
        edtPassword=findViewById(R.id.edtPassword);
        btnLogin=findViewById(R.id.btnLogin);
        recuperarPreferencias();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario=edtUsuario.getText().toString();
                password=edtPassword.getText().toString();

                if (!usuario.isEmpty()&&!password.isEmpty()){
                    validaUsuario("https://seguridad-privada.sspleon.gob.mx/api/login");

                }else {
                    AlertDialog.Builder escanerDial=new AlertDialog.Builder(MainActivity.this);
                    //constructor
                    escanerDial.setTitle("Error de captura ").
                            setMessage("No se permiten campos vacios").
                            setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog=escanerDial.create();
                    alertDialog.show();
                }
            }
        });
    }
    private void validaUsuario(String URL){
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject objToken = null;
                try {
                 objToken = new JSONObject(response);
                    if(!response.isEmpty()){
                        Intent intent=new Intent(getApplicationContext(),Verificar.class);
                        String tok = objToken.get("token").toString();
                        token2= tok;
                        guardarPreferencias();
                        startActivity(intent);
                        finish();
                    }
            else{
                AlertDialog.Builder escanerDial=new AlertDialog.Builder(MainActivity.this);
                //constructor
                escanerDial.setTitle("Usuario o contraseña incorrecto ").
                        setMessage("El usuario o contraseña es incorrecto,").
                        setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alertDialog=escanerDial.create();
                alertDialog.show();
            }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder escanerDial=new AlertDialog.Builder(MainActivity.this);
                //constructor
                escanerDial.setTitle("Usuario o contraseña incorrecto ").
                        setMessage("El usuario o contraseña es incorrecto,").
                        setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog alertDialog=escanerDial.create();
                alertDialog.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password", password);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void guardarPreferencias(){
        SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("password", password);
        editor.putString("api_token", token2);
        editor.putBoolean("sesion",true);
        editor.commit();
    }
    private void recuperarPreferencias (){
        SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        edtUsuario.setText(preferences.getString("usuario", ""));
    }
    @Override
    public void onBackPressed() {

    }
}

////////////////////