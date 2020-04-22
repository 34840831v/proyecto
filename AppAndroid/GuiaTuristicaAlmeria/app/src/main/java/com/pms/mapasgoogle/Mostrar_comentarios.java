package com.pms.mapasgoogle;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Mostrar_comentarios extends AppCompatActivity {

    ArrayList<Comentario> listaComentarios;//lista para almacenar las imagenes de un usuario determinado
    RecyclerView rvComentarios;//Por cada registro que tengamos en la BD se va a generar una fila en el RecyclerView
    TextView tvMostrarNombre;
    String OBTENER_COMENTARIOS = "http://satisfactory-plugs.000webhostapp.com/proyecto/consultarComentarios.php";

    JSONObject jsonObject, jsonObject1;
    JSONArray jsonArray;

    int aux_ubic, id;
    String aux_comentario, nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentarios_layout);

        tvMostrarNombre = findViewById(R.id.tvMostrarNombre);
        rvComentarios = findViewById(R.id.rvComentarios);
        rvComentarios.setLayoutManager(new GridLayoutManager(this, 2));

        //Obtengo el id del establecimiento que hemos pulsado en el mapa
        Bundle idEstablecimiento = getIntent().getExtras();//Extrae el id del establecimiento
        id = idEstablecimiento.getInt("id_ubicacion");//Guarda en un String el valor anterior
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx id_ubicacion: "+id);

        //Obtengo el nombre de la ubicación que hemos pulsado en el mapa
        Bundle nombreEstablecimiento = getIntent().getExtras();//Extrae el nombre del establecimiento
        nombre = nombreEstablecimiento.getString("nombre");//Guarda en un String el valor anterior
        tvMostrarNombre.setText(nombre);

        listaComentarios = new ArrayList<Comentario>();
        obtenerComentarios();
    }

    public void obtenerComentarios(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, OBTENER_COMENTARIOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//Obtiene una respuesta a la consulta php 'OBTENER_COMENTARIOS'
                        try {
                            jsonObject = new JSONObject(response);//Convierte la respuesta de la consulta php 'OBTENER_COMENTARIOS' en JSON y la guarda en un JSONObject
                            jsonArray = jsonObject.getJSONArray("Comentario");//El JSONObject anterior lo guardo en un JSONArray

                            for (int i = 0; i < jsonArray.length(); i++) {//Recorro el JSONArray
                                jsonObject1 = jsonArray.getJSONObject(i);//Guarda en otro JSONObject los datos (id_ubic y el comentario sobre el establecimiento) que se encuentra en la posición 'i'
                                aux_comentario = "* "+jsonObject1.getString("comentarios");//Guarda en la variable aux_comentario el comentario sobre el establecimiento de la posición 'i'
                                aux_ubic = jsonObject1.getInt("id_ubic");//Guarada en aux_ubic el id_ubic de la ubicación o establecimiento de la posición 'i'

                                System.out.println("aux_comentario: "+aux_comentario);
                                System.out.println("aux_ubic: "+aux_ubic);
                                System.out.println("id: "+id);
                                System.out.println("--------------------");

                                //En el caso de que el id_ubicación que hemos pulsado en 'MapsActivity'
                                //coincida con el id_ubicacion de la posición 'i' del jsonObject1 que
                                // estamos leyendo guarda en la lista las imganenes, con lo cual,
                                //guardaría solo las fotos que le corresponde al establecimiento, para
                                //posteriormente mostrarlas en el 'RecyclerView'
                                if (id==aux_ubic) listaComentarios.add(new Comentario(aux_ubic, aux_comentario));
                            }
                        /*
                            System.out.println("El nº total de imagenes es: "+listaComentarios.size());
                            for (int j = 0; j < jsonArray.length(); j++) {
                                System.out.println("listaComentarios: "+listaComentarios.get(j).getComentarios());
                            }
                        */

                            AdapterComentario adaptadorComen = new AdapterComentario(listaComentarios);//Crea un objeto de la clase 'AdapterFoto'

                            rvComentarios.setAdapter(adaptadorComen);
                            //Para que se vea cada comentario en una línea distinta
                            rvComentarios.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComentarios.getContext(),
                                    ((LinearLayoutManager) rvComentarios.getLayoutManager()).getOrientation());
                            rvComentarios.addItemDecoration(dividerItemDecoration);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(stringRequest);
    }

}
