package com.pms.mapasgoogle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Datos_establecimiento extends AppCompatActivity {

    String link, nombre, phone, aux_url_imagen;
    Bundle direccionWeb, telf, nombreEstablecimiento, idEstablecimiento;

    int id, id_foto, auxUbicacion;
    TextView name, noImagenes;

    ImageView imagenView;

    JSONObject jsonObject, jsonObject1;
    JSONArray jsonArray;

    ArrayList<Foto> listaFotosUsuario;//lista para almacenar las imagenes de un usuario determinado
    public ArrayList<Foto> listaDeTodasLasFotos;//lista para almacenar todas las imagenes
    RecyclerView rvFotos;//Por cada registro que tengamos en la BD se va a generar una fila en el RecyclerView

    String OBTENER_FOTOS = "http://satisfactory-plugs.000webhostapp.com/proyecto/obtenerFotos.php"; //Obtiene id_foto, id_ubicacion y foto de todas las fotos

    //url donde se encuentra la imagen que quiero visualizar en la ImageView de 'Datos_establecimiento' con Picasso
    private static final String IMAGEN_COMER_EN_ALMERIA = "http://satisfactory-plugs.000webhostapp.com/proyecto/imagenes/almeria.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_establecimiento_layout);

        System.out.println("web antes es:      "+link);

        Bundle recibir = getIntent().getExtras();
        boolean  recibeDatos = recibir.getBoolean("envia_datos");
        System.out.println("/////////////// recibeDatos                       ////////   :"+recibeDatos);
        if (recibeDatos) {
            //Obtengo la dirección web de la ubicación que hemos pulsado en el mapa
            direccionWeb = getIntent().getExtras();//Extrae la web
            link = direccionWeb.getString("web");//Guarda en un String el valor anterior

            System.out.println("web despues es:      " + link);

            //Obtengo el teléfono de la ubicación que hemos pulsado en el mapa
            telf = getIntent().getExtras();//Extrae el teléfono
            phone = telf.getString("telefono");//Guarda en un String el valor anterior

            //Obtengo el nombre de la ubicación que hemos pulsado en el mapa
            Bundle nombreEstablecimiento = getIntent().getExtras();//Extrae el nombre del establecimiento
            nombre = nombreEstablecimiento.getString("nombre");//Guarda en un String el valor anterior
        }
            //Obtengo el id del establecimiento que hemos pulsado en el mapa
            idEstablecimiento = getIntent().getExtras();//Extrae el id del establecimiento
            id = idEstablecimiento.getInt("id_ubicacion");//Guarda en un String el valor anterior
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx id_ubicacion: " + id);


        name = (TextView)findViewById(R.id.txtName);
        name.setText(nombre);
        noImagenes = (TextView)findViewById(R.id.txtNoImagenes);

        rvFotos = findViewById(R.id.rvFotos);//obteniendo la vista del recyclerView del xml
        rvFotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFotos.setHasFixedSize(true);
        listaFotosUsuario = new ArrayList<Foto>();//inicializando listaFotosUsuario
        listaDeTodasLasFotos = new ArrayList<Foto>();//inicializando listaDeTodasLasFotos

        //asocio la variable de 'imagenView' a ImageView declarado en 'datos_establecimiento_layout.xml'
        imagenView = (ImageView) findViewById(R.id.imagen);
        cargarImagenConPicasso();

        //Este método configura y parsea a json
        //desplegar en el recyclerview
        obtenerFotos();
    }

    private void cargarImagenConPicasso(){
        Picasso.get().load(IMAGEN_COMER_EN_ALMERIA).into(imagenView);
    }

    /**
     * Al hacer clik en el bóton 'WEB' nos abre la página
     * web del establecimiento que hemos pulsado en el mapa
     * @param view
     */
    public void onClickWeb(View view) {
        //Muestra la web del establecimiento
        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(in);
    }

    /**
     * Llama por teléfono al establecimiento seleccionado
     */
    public void onClickLlamar(View v) {
        String tlf = phone;
        Intent intent_llamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tlf));
        startActivity(intent_llamar);
    }

    public void onClickAnadirFoto(View v) {
        //El id_ubicacion obtenido de 'MapsActivity' se pasa también a Cargar_imagen.java, activity
        //desde la cual se va ha insertar los valores correspondientes (id_ubicacion, foto) en tabla_fotos
        Intent intentCargarImagen = new Intent(this,Cargar_imagen.class);
        intentCargarImagen.putExtra("id_ubicacion", id);//envia id_ubicacion a la activity 'Cargar_imagen'
        intentCargarImagen.putExtra("id_foto", id_foto);//envia id_foto a la activity 'Cargar_imagen'
        startActivity(intentCargarImagen);
        finish();
    }

    public void onClickVerComentarios(View view) {
        System.out.println("123156789 /////// id: "+id);
        Intent intentVerComentarios = new Intent(this, Mostrar_comentarios.class);
        intentVerComentarios.putExtra("id_ubicacion", id);//envia id_ubicacion a la activity 'Mostrar_comentarios'
        intentVerComentarios.putExtra("nombre", nombre);//envia el nombre del establecimiento a 'Mostrar_comentarios'
        System.out.println("nombre:                            "+nombre);
        startActivity(intentVerComentarios);
    }

    /**
     * Creamos un String con el Request
     * El request tipo POST está definido como primer parámetro
     * La URL está definida como segundo parámetro
     * Tenemos Response Listener y un Error Listener (3º y 4º parámetros respectivamente)
     * En response listener obtenemos JSON response como un String
     */
    public void obtenerFotos() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, OBTENER_FOTOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//Obtiene una respuesta a la consulta php 'OBTENER_FOTOS_URL'
                        try {
                            jsonObject = new JSONObject(response);//Convierte la respuesta de la consulta php 'OBTENER_FOTOS' en JSON y la guarda en un JSONObject
                            jsonArray = jsonObject.getJSONArray("Fotos");//El JSONObject anterior lo guardo en un JSONArray

                            for (int i = 0; i < jsonArray.length(); i++) {//Recorro el JSONArray
                                jsonObject1 = jsonArray.getJSONObject(i);//Guarda en otro JSONObject los datos (id_ubicacion y la url donde esta la imagen) que se encuentra en la posición 'i'
                                aux_url_imagen = jsonObject1.getString("foto");//Guarda en la variable aux_url_imagen la url donde está la imagen de la posición 'i'
                                auxUbicacion = jsonObject1.getInt("id_ubicacion");//Guarada en auxUbicacion el id_ubicación de la ubicación o establecimiento de la posición 'i'
                                id_foto = jsonObject1.getInt("id_foto");////Guarada en auxUbicacion el id_ubicación de la ubicación o establecimiento de la posición 'i'

                                System.out.println("aux_url_imagen: "+aux_url_imagen);
                                System.out.println("auxUbicacion: "+auxUbicacion);
                                System.out.println("id_ubicacion: "+ id);
                                System.out.println("id_foto: "+id_foto);
                                System.out.println("--------------------");

                                //En el caso de que el id_ubicación que hemos pulsado en 'MapsActivity'
                                //coincida con el id_ubicacion de la posición 'i' del jsonObject1 que
                                // estamos leyendo guarda en la lista las imganenes, con lo cual,
                                //guardaría solo las fotos que le corresponde al establecimiento, para
                                //posteriormente mostrarlas en el 'RecyclerView'
                                if (id==auxUbicacion) listaFotosUsuario.add(new Foto(id_foto, auxUbicacion, aux_url_imagen));
                            }

                            //En el caso de que el establecimiento no tenga ninguna imagen aún, informa sobre ello
                            if (listaFotosUsuario.size()==0)
                                noImagenes.setText("Este establecimiento\n no tiene imagenes que mostrar");

                            AdapterFoto adaptador = new AdapterFoto(listaFotosUsuario);//Crea un objeto de la clase 'AdapterFoto'

                            //Funcionalidad que va a tener el evento de clicar en una imagen del
                            //recyclerView, la cual va a ser mostrar en el ImageView la imagen ampliada
                            adaptador.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Muestra la imagen
                                    Picasso.get().load(listaFotosUsuario.get(rvFotos.getChildAdapterPosition(view)).getUrl_imagen()).into(imagenView);
                                }
                            });
                            //inserta en el recyclerView 'rvFotos' el resultado del 'AdapaterFoto'
                            //el cual, son las imagenes correspondientes al establecimiento
                            rvFotos.setAdapter(adaptador);

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
    /**
     * Actualiza la imagenes al pulsar el botón 'Ir hacia atrás' de la aplicación
     * añadiendo la imagen que hemos subido al recyclerView de 'Datos_establecimiento'
     * @param keyCode
     * @param event
     * @return
     */
 /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intentMapsActivity = new Intent(this,MapsActivity.class);
            startActivity(intentMapsActivity);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
*/
}
