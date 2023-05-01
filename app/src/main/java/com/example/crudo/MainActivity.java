package com.example.crudo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText et_email, et_pass;
    Button btn_login;
    CheckBox keep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.user_email);
        et_pass = findViewById(R.id.user_password);
        btn_login = findViewById(R.id.login);

        recuperar();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Iniciando sesión...",Toast.LENGTH_SHORT).show();
                login_user("https://crudo-app.000webhostapp.com/login-app.php");
            }
        });
    }
    public void login_user(String URL){
        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!et_email.getText().toString().isEmpty() && !et_pass.getText().toString().isEmpty()){
                    if(!response.isEmpty()){
                        guardarSesion();
                        Intent intent = new Intent(getApplicationContext(),home.class);
                        intent.putExtra("user_email",et_email.getText().toString());
                        intent.putExtra("user_pass",et_pass.getText().toString());
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this,"Usuario o contraseña incorrecta",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,"Por favor, complete los campos antes de continuar",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Hubo un problema al iniciar",Toast.LENGTH_SHORT).show();
                Log.d("Login",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("email",et_email.getText().toString());
                params.put("password",et_pass.getText().toString());
                return params;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);
    }
    public void guardarSesion(){
            SharedPreferences pref = getSharedPreferences("keeplogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("usser_email",et_email.getText().toString());
            editor.putString("usser_password",et_pass.getText().toString());
            editor.putBoolean("login",true);

    }
    private void recuperar(){
        SharedPreferences pref = getSharedPreferences("keeplogin",Context.MODE_PRIVATE);
        et_email.setText(pref.getString("usser_email",""));
        et_pass.setText(pref.getString("usser_password",""));
    }
}