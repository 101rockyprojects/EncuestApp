package com.example.crudo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class agregar extends AppCompatActivity {
    // Creamos los objetos a utilizar
    TextView nombre_barrio, tv_total,email;
    EditText et_encuestado, et_jefe, et_n_menores,et_n_adultos,et_direccion;
    Button b_agregar;
    RequestQueue requestQueue;
    ArrayAdapter<String> adpTV;
    Spinner spTV;
    String[] optTV = new String[]{"Selecciona tipo de vivienda","Arrendado","Propia","Familiar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        // Enlazamos los objetos creados con los elementos de la vista, recuperamos e imprimimos el email enviado de la anterior vista
        email = findViewById(R.id.tv_fondo_email);
        String user_email = save_email();
        email.setText(user_email);
        nombre_barrio = findViewById(R.id.barrio_name);
        String b = save();
        nombre_barrio.setText(b+"   ");

        adpTV = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, optTV);
        spTV = findViewById(R.id.spTv);
        spTV.setAdapter(adpTV);

        et_encuestado = findViewById(R.id.encuestado);
        et_jefe = findViewById(R.id.jefe_hogar);
        et_n_menores = findViewById(R.id.n_menores);
        et_n_adultos = findViewById(R.id.n_adultos);
        et_direccion = findViewById(R.id.direccion);
        tv_total = findViewById(R.id.total);
        
        // Calcular automaticamente el total de residentes
        et_n_menores.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            // Realizar la suma cada vez que se modifica el contenido de et_n_menores
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!et_n_adultos.getText().toString().isEmpty() && !et_n_menores.getText().toString().isEmpty()){
                    int number1 = Integer.parseInt(et_n_menores.getText().toString());
                    int number2 = Integer.parseInt(et_n_adultos.getText().toString());
                    int result = number1 + number2;
                    tv_total.setText(String.valueOf(result));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_n_adultos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // Realizar la suma cada vez que se modifica el contenido de et_n_adultos
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!et_n_adultos.getText().toString().isEmpty() && !et_n_menores.getText().toString().isEmpty()){
                    int number1 = Integer.parseInt(et_n_menores.getText().toString());
                    int number2 = Integer.parseInt(et_n_adultos.getText().toString());
                    int result = number1 + number2;
                    tv_total.setText(String.valueOf(result));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        b_agregar = findViewById(R.id.bt_agregar);

        // Al darle al botón Agregar subir los datos a la BDD
        b_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertarDatos("https://crudo-app.000webhostapp.com/create-app.php");
            }


            public void insertarDatos(String URL) {
                final ProgressDialog progressDialog = new ProgressDialog(agregar.this);
                progressDialog.setMessage("Cargando... \n Espera unos segundos :)");
                // Guardamos lo que se ingreso en los EditText
                String encuestado = et_encuestado.getText().toString();
                String jefe = et_jefe.getText().toString();
                String n_menores = et_n_menores.getText().toString();
                String n_adultos = et_n_adultos.getText().toString();
                String direc = et_direccion.getText().toString();
                // Siempre que haya un campo sin haber completado
                if(encuestado.isEmpty() || jefe.isEmpty() || n_adultos.isEmpty() || n_menores.isEmpty() || direc.isEmpty() || spTV.getSelectedItemPosition()==0 || Integer.parseInt(tv_total.getText().toString)<=0){
                    // Imprimir que se llenen todos los campos
                    Toast.makeText(agregar.this, "Ingresa todos los datos antes de continuar",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    progressDialog.show();
                    StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equalsIgnoreCase("Datos Actualizados Correctamente")) {
                                Toast.makeText(agregar.this, "Agregado correctamente", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), home.class));
                                finish();
                            } else {
                                Log.d("WTF",response.toString());
                                Toast.makeText(agregar.this, response, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("DBug",error.getMessage());
                            Toast.makeText(agregar.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }){
                        // Obtener y mandar todos los datos por params y subirlo al archivo PHP que hace el INSERT en la BDD
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String,String>();
                            Bundle extras = getIntent().getExtras();
                            String id_empleado = extras.getString("id_empleado");
                            params.put("id_empleado",id_empleado);

                            String ruta = extras.getString("id_ruta");
                            int id_ruta = (Integer) Integer.parseInt(id_empleado) + Integer.parseInt(ruta);
                            Log.d("Suma",id_empleado+" + "+ruta+" = "+id_ruta);
                            params.put("id_ruta",String.valueOf(id_ruta));

                            params.put("nombre_barrio",nombre_barrio.getText().toString());

                            //fecha_hora se digita al insertar en PHP
                            params.put("nombre_encuestado",encuestado);
                            params.put("nombre_jefe_hogar",jefe);
                            params.put("tipo_vivienda", String.valueOf(spTV.getSelectedItemPosition()));
                            params.put("num_familiares",tv_total.getText().toString());
                            params.put("num_adultos",n_adultos);
                            params.put("num_menores",n_menores);
                            params.put("direccion",direc);
                            for (Map.Entry<String, String> entry : params.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                Log.d("Encuesta",key + ": " + value);
                            }
                            return params;
                        }
                    };

                    requestQueue = Volley.newRequestQueue(agregar.this);
                    requestQueue.add(request);


                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    // Limpiar los campos de la encuesta
    private void limpiar(){
        et_encuestado.setText("");
        et_jefe.setText("");
        spTV.setSelection(0);
        et_n_menores.setText("");
        et_n_adultos.setText("");
        tv_total.setText("");
        et_direccion.setText("");
    }
    // Obtener el barrio seleccionado de la vista anterior
    private String save(){
        Bundle extras = getIntent().getExtras();
        String barrio = extras.getString("barrio");
        return barrio;
    }
    // Guardar email
    private String save_email(){
        SharedPreferences pref = getSharedPreferences("keeplogin", Context.MODE_PRIVATE);
        boolean sesion = pref.getBoolean("login",false);
        String email_a_buscar;
        if(sesion){
            email_a_buscar = pref.getString("usser_email","");
        } else {
            Bundle extras = getIntent().getExtras();
            email_a_buscar = extras.getString("user_email");
        }
        return email_a_buscar;
    }
    // Devolverme al home al presionar un botón de la vista
    public void goback(View view){
        Toast.makeText(agregar.this, "Cancelando operación...",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),home.class);
        intent.putExtra("user_email",email.getText().toString());
        startActivity(new Intent(getApplicationContext(),home.class));
    }
}
