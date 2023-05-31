package com.example.login_base;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Calendar;

public class PrincipalActivity extends AppCompatActivity {
    Button btnCerrar;
    TextView imei;
    private TextView año;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        año = findViewById(R.id.textViewAño);
        Calendar fecha = Calendar.getInstance();
        int añoV = fecha.get(Calendar.YEAR);
        año.setText(+añoV+"© DERECHOS RESERVADOS SSP/C4/CSI");

        btnCerrar=findViewById(R.id.btnCerrar);
        imei = (TextView) findViewById(R.id.txtimei);imei.setVisibility(View.VISIBLE);
        String pImei = getIntent().getStringExtra("pImei");
        imei.setText(pImei);

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ValidarActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
