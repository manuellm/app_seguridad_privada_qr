package com.example.login_base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Comprobar si ha aceptado o no ha aceptado permisos READ_PHONE_STATE
        if (CheckPermission(Manifest.permission.READ_PHONE_STATE) &&
                CheckPermission(Manifest.permission.CAMERA) &&
                CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                CheckPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                CheckPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            // Ha aceptado
            Intent intent = new Intent(getApplicationContext(), ValidarActivity.class);
            startActivity(intent);
            finish();
        } else{
            //No ha aceptado
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Estmos en el caso del telefono
        switch (requestCode){
            case 1000:

                if (permissions[0].equals(Manifest.permission.READ_PHONE_STATE) &&
                        permissions[1].equals(Manifest.permission.CAMERA) &&
                        permissions[2].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        permissions[3].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        permissions[4].equals(Manifest.permission.ACCESS_COARSE_LOCATION)){
                    // Comprobar si ha sido aceptado o denegado el permiso
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[3] == PackageManager.PERMISSION_GRANTED){
                        // Concedio el permiso
                        Intent intent = new Intent(getApplicationContext(), ValidarActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        // No se concedio el permiso
                        AlertDialog.Builder verificaDial = new AlertDialog.Builder(inicio.this);

                        verificaDial.setTitle("FALTA OTORGAR PERMISOS").
                                setMessage(" Es necesario otorgar todos los permisos solicitados, se cerrara la aplicacion").
                                setPositiveButton(" Aceptar ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog=verificaDial.create();
                        alertDialog.show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
    private boolean CheckPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onBackPressed() {
    }
}