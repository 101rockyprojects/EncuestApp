package com.example.crudo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Crear los objetos a utilizar
    TextView tv_ced, tv_nombre, tv_email, tv_gen;
    ArrayAdapter<String> adpB1, adpB2, adpB3;
    Spinner spB1,spB2,spB3;
    CheckBox chkBox1,chkBox2,chkBox3;
    int id;
    String lista_barrios;
    String[] optR1 = new String[0];
    String[] optR2 = new String[0];
    String[] optR3 = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Enlazar los objetos creados con los elementos de la vista
        tv_ced = findViewById(R.id.textced);
        tv_nombre = findViewById(R.id.textname);
        tv_email = findViewById(R.id.textemail);
        tv_gen = findViewById(R.id.textgen);

        spB1 = findViewById(R.id.spb1);
        spB1.setOnItemSelectedListener(this);
        spB2 = findViewById(R.id.spb2);
        spB2.setOnItemSelectedListener(this);
        spB3 = findViewById(R.id.spb3);
        spB3.setOnItemSelectedListener(this);

        chkBox1 = findViewById(R.id.checkB1);
        chkBox2 = findViewById(R.id.checkB2);
        chkBox3 = findViewById(R.id.checkB3);
        
        // Recibiendo email de la vista anterior
        String user_email = save(); 
        // Obtener el id del empleado con el correo que hizo login
        id = buscar_por_email("https://crudo-app.000webhostapp.com/search-app.php?email="+user_email+"");
        tv_email.setText(user_email);

    }
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    // Obtener los SharedPreferences
    private String save(){
        SharedPreferences pref = getSharedPreferences("keeplogin",Context.MODE_PRIVATE);
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
    
    // Imprimir los datos del usuario segun su correo
    private int buscar_por_email(String URL){
        JsonArrayRequest jsonar = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jso = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jso = response.getJSONObject(i);
                        id = Integer.parseInt(jso.getString("id"));
                        tv_ced.setText(jso.getString("ced"));
                        tv_nombre.setText(jso.getString("name"));
                        tv_gen.setText(jso.getString("genero"));
                        Log.d("Imprimir ids_rutas",id+" "+(id+5)+" "+(id+10));
                        // Obtener las rutas que le corresponden al usuario dicha semana
                        traerBarrios("https://crudo-app.000webhostapp.com/search-barrios.php?id="+id+"",1);
                        traerBarrios("https://crudo-app.000webhostapp.com/search-barrios.php?id="+(id+5)+"",2);
                        traerBarrios("https://crudo-app.000webhostapp.com/search-barrios.php?id="+(id+10)+"",3);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error al conectar", Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue rq = Volley.newRequestQueue(home.this);
        rq.add(jsonar);
        return id;
    }
    // Obtener la lista de barrios de la ruta del usuario
    public void traerBarrios(String URL,int ruta){
        JsonArrayRequest jsonar = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jso = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jso = response.getJSONObject(i);
                        lista_barrios = jso.getString("lista_barrios");
                        // Hacer un Array con los ids de los barrios separandolos por coma
                        String[] id_barrio = lista_barrios.split(",");
                        for (int j = 0; j < id_barrio.length; j++) {
                            // Por cada barrio de ese Array hacer la búsqueda de su nombre mediante el id
                            imprimirBarrios("https://crudo-app.000webhostapp.com/search-barrio-by-id.php?id=" + id_barrio[j] + "",ruta);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error al conectar", Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue rq = Volley.newRequestQueue(home.this);
        rq.add(jsonar);
    }
    // Ingresar los nombres de los barrios en el Spinner que le corresponde
    public void imprimirBarrios(String URL,int ruta){
        JsonArrayRequest jsonar = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jso = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jso = response.getJSONObject(i);
                        String nuevoBarrio = jso.getString("nombre");
                        Log.d("Rutas", ruta+": "+nuevoBarrio);
                        // Si el barrio es de la ruta 1 lo añade al Array
                        if(ruta==1) {
                            optR1 = Arrays.copyOf(optR1, optR1.length + 1);
                            optR1[optR1.length - 1] = nuevoBarrio;
                        } else {
                            // Sino, si el barrio es de la ruta 2 lo añade al Array
                            if(ruta==2) {
                                optR2 = Arrays.copyOf(optR2, optR2.length + 1);
                                optR2[optR2.length - 1] = nuevoBarrio;
                            } else {
                                // Sino, el barrio es de la ruta 3 lo añade al Array
                                if(ruta==3){
                                    optR3 = Arrays.copyOf(optR3, optR3.length + 1);
                                    optR3[optR3.length-1] = nuevoBarrio;
                                }
                            }
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                // Al terminar de recolectar los 5 barrios de la ruta 1 los añade al Spinner 1
                Log.d("Lenght",optR1.length+" "+optR2.length+" "+optR3.length);
                if (optR1.length == 5){
                    adpB1 = new ArrayAdapter<String>(home.this, android.R.layout.simple_spinner_item, optR1);
                    spB1.setAdapter(adpB1);
                }
                // Al terminar de recolectar los 5 barrios de la ruta 2 los añade al Spinner 2
                if (optR2.length == 5){
                    adpB2 = new ArrayAdapter<String>(home.this, android.R.layout.simple_spinner_item, optR2);
                    spB2.setAdapter(adpB2);
                }
                // Al terminar de recolectar los 5 barrios de la ruta 3 los añade al Spinner 3
                if (optR3.length == 5){
                    adpB3 = new ArrayAdapter<String>(home.this, android.R.layout.simple_spinner_item, optR3);
                    spB3.setAdapter(adpB3);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error al conectar", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue rq = Volley.newRequestQueue(home.this);
        rq.add(jsonar);
    }
    
    // Guarda el nombre del barrio seleccionado y lo manda a la siguiente vista
    public void encuesta(View view){
        Intent intent = new Intent(getApplicationContext(),agregar.class);
        // Si se seleccionaron los 3 Chexkbox
        if(chkBox1.isChecked() && chkBox2.isChecked() && chkBox3.isChecked()){
            // Imprimimos al usuario que ya termino
            Toast.makeText(this, "Parece que ya acabaste con las rutas de esta semana. ¡Felicidades!", Toast.LENGTH_SHORT).show();
        } else {
            if(!chkBox1.isChecked()) {
                // Si no esta chequeado el Checkbox 1 tomamos el barrio del Spinner 1
                intent.putExtra("barrio",spB1.getSelectedItem().toString());
                intent.putExtra("id_ruta",String.valueOf(0));
            } else {
                if(!chkBox2.isChecked()) {
                    // Si esta chequeado el Checkbox 1 y no esta chequeado el 2 tomamos el barrio del Spinner 2
                    intent.putExtra("barrio",spB2.getSelectedItem().toString());
                    intent.putExtra("id_ruta",String.valueOf(5));
                } else {
                    if(!chkBox3.isChecked()) {
                        // Si los Checkbox 1 y 2 estan chequeados tomamos el barrio del Spinner 3
                        intent.putExtra("barrio",spB3.getSelectedItem().toString());
                        intent.putExtra("id_ruta",String.valueOf(10));
                    }
                }
            }
            // Enviamos el id del empleado, el email y de que ruta es el barrio
            intent.putExtra("id_empleado",String.valueOf(id));
            intent.putExtra("user_email",tv_email.getText().toString());
            startActivity(intent);
        }
    }
    // Cerrar sesion y volver al Login
    public void logout(View view){
        SharedPreferences pref = getSharedPreferences("keeplogin", Context.MODE_PRIVATE);
        pref.edit().clear().commit();
        Intent intent = new Intent(getApplicationContext(),redireccion.class);
        startActivity(intent);
    }
}
