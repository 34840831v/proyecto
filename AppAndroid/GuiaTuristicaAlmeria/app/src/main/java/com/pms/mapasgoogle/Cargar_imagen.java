package com.pms.mapasgoogle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Cargar_imagen extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    String GUARDAR_FOTO_EN_CARPETA_IMAGENES = "http://satisfactory-plugs.000webhostapp.com/proyecto/guardar_foto_en_carpeta_imagenes.php";
//    String INSERTAR_EN_TABLA_FOTOS = "http://satisfactory-plugs.000webhostapp.com/proyecto/insertar_tabla_fotos.php";
    String nombre;
    Bitmap bitmap;

    ImageView imageView;
    EditText etNombreFoto;

    //Para insertar en tabla_fotos
    private RequestQueue rq;
    private Context ctx;
    private StringRequest strq;

    String KEY_IMAGE = "foto";
    String KEY_NOMBRE = "nombre";

    int id_ubicacion, id_foto;
    public ArrayList<Foto> listaDeTodasLasFotos;//lista para almacenar todas las imagenes

    String OBTENER_FOTOS_URL = "http://satisfactory-plugs.000webhostapp.com/proyecto/obtenerFotos.php";

    Datos_establecimiento datos_establecimiento = new Datos_establecimiento();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_imagen);

        etNombreFoto = findViewById(R.id.etNombreFoto);
        imageView = (ImageView) findViewById(R.id.imagenACargar);

        //Obtengo el valor de id_ubicacion enviado desde la activity 'Datos_establecimiento'
        Bundle id = getIntent().getExtras();//Extrae id_ubicacion enviado desde 'Datos_establecimiento'
        id_ubicacion = id.getInt("id_ubicacion");//Guarda en un String el valor anterior y que usa en línea 97
        //Obtengo el valor de id_ubicacion enviado desde la activity 'Datos_establecimiento'
        Bundle idfoto = getIntent().getExtras();//Extrae id_foto enviado desde 'Datos_establecimiento'
        id_foto = idfoto.getInt("id_foto");//Guarda en un String el valor anterior y que usa en línea 97
        System.out.println("id_foto:                                   "+id_foto);
        System.out.println("id_ubicacion:                            "+id_ubicacion);

    }


//////////////////////////////////// INICIO DE ELEGIR IMAGEN////////////////////////////////////////
    //BOTÓN PARA BUSCAR IMAGEN
    public void onClickElegir(View view) {
        elegirImagen();
    }
    //PARA SELECCIONAR UNA IMAGEN
    private void elegirImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), PICK_IMAGE_REQUEST);
    }
/////////////////////////////////////// FIN DE ELEGIR IMAGEN////////////////////////////////////////

//////////////////////////////////////////////////// INICIO DE SUBIR IMAGEN AL WEB SERVICE /////////////////////////////////////////////////////////
    public void onClickSubir(View v) {

        guardarFotoEnCarpetaImagenes(); //Método que guarda la foto en la carpeta 'imagenes' del web service

    //////////////  INSERTAR EL ID_UBICACION Y LA URL DONDE SE ENCUENTRA LA FOTO, EN 'tabla_fotos' DEL WEB SERVICE  //////////////

        //Ruta donde se guarda la imagen en el WebService
        final String ruta_foto = "http://satisfactory-plugs.000webhostapp.com/proyecto/imagenes/"+nombre+".png";
        System.out.println("ruta_foto:            "+ruta_foto);
        //Llamada al hilo que insertara 'id_ubicacion' y la url donde se encuentra la imagen en tabla_fotos de la BD
        new WS_Insertar_foto_tabla_fotos(Cargar_imagen.this,  id_ubicacion, ruta_foto).execute();
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////  FIN DE SUBIR IMAGEN AL WEB SERVICE  /////////////////////////////////////////////////////////
    }

    //CONVIERTE LA IMAGEN EN UN STRING
    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    //SUBIR IMAGEN
    //Método que guarda la foto en la carpeta 'imagenes' del web service
    public void guardarFotoEnCarpetaImagenes() {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        //Le añado al nombre el id_foto (el cual es único) de este modo no se repetirá ningún nombre de imagen
        id_foto ++; //id_foto tiene el valor del último id_foto si le sumo 1 obtengo el id_foto que se crearía la proxima vez, que es lo que realmente busco
        nombre = Cargar_imagen.this.etNombreFoto.getText().toString().trim()+id_foto; //al nombre que escribimos en el editText le añadimos el id_foto para que no haya un nombre de imagen igual
        //INICIAR LA CONEXIÓN CON VOLLEY
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GUARDAR_FOTO_EN_CARPETA_IMAGENES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//SE EJECUTA CUANDO LA CONSULTA SALIO BIEN, SIN ERRORES
                        loading.dismiss();
                        Toast.makeText(Cargar_imagen.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(Cargar_imagen.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String imagen = getStringImagen(bitmap);

                System.out.println("El nombre de la foto es: "+nombre);
                System.out.println("El id del establecimiento (id_ubicacion) es: "+id_ubicacion);

                Map<String, String> params = new Hashtable<String, String>();
                params.put(KEY_IMAGE, imagen);
                params.put(KEY_NOMBRE, nombre);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
/////////////////////////////////////// FIN DE SUBIR IMAGEN////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Cómo obtener el mapa de bits de la Galería
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Configuración del mapa de bits en ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ha habido un error al cargar");
            }
        }
    }

    /**
     * Actualiza la imagenes al pulsar el botón 'Ir hacia atrás' de la aplicación
     * añadiendo la imagen que hemos subido al recyclerView de 'Datos_establecimiento'
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            boolean enviarDatos = false;
            Intent intentDatosEstablecimiento = new Intent(this,Datos_establecimiento.class);
            intentDatosEstablecimiento.putExtra("envia_datos", enviarDatos);
            intentDatosEstablecimiento.putExtra("id_ubicacion", id_ubicacion);
            startActivity(intentDatosEstablecimiento);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

