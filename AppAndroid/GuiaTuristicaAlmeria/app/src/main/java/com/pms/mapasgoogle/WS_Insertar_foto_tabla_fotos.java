package com.pms.mapasgoogle;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.pms.mapasgoogle.Config.URL_PHP;

public class WS_Insertar_foto_tabla_fotos extends AsyncTask<Void, Void, String> {

    private Activity context;

    int id_ubicacion, id_foto;
    String ruta_foto;
    public static String url;

    //constructor
    public WS_Insertar_foto_tabla_fotos(Activity context, int id_ubicacion, String ruta_foto){
        this.context = context;
        this.id_ubicacion = id_ubicacion;
        this.ruta_foto = ruta_foto;
    }

    @Override
    protected String doInBackground(Void... voids) {
        System.out.println("HHHHHHHHHHHHHHHHHHH He entrado en doInBackground de insertar usuario");
        String resultado = "ERROR";
            if (insertar())
                // la inserción del establecimiento ha sido exitosa
                resultado = "OK";
            else
                // ha habido un error al insertar el estableciomiento y no se pudo insertar
                resultado = "ERROR";
        return resultado;
    }
    /**
     * @return: true/false
     * devuelve true => si la inserción es correcta
     * devuelve false => si hubo un error en la inserción
     */
    private boolean insertar() {
        boolean resul = false;
        // interfaz para un cliente HTTP
        HttpClient httpclient;
        // define una lista de parámetros ("clave" "valor") que serán enviados por POST al script php
        List<NameValuePair> parametros_POST;
        // define un objeto para realizar una solicitud POST a través de HTTP
        HttpPost httppost;
        // crea el cliente HTTP
        httpclient = new DefaultHttpClient();
        // creamos el objeto httpost para realizar una solicitud POST al script PHP correspondiente
        httppost = new HttpPost("https://"+URL_PHP + Config.PHP_INSERTAR_FOTO); // Url del Servidor
        System.out.println("url: "+URL_PHP + Config.PHP_INSERTAR_FOTO);

        //Añadimos los datos que vamos a enviar por POST al script insertar_usuario.php
        // ** =>debe coincidir la clave con índice del $_POST[] indicado en el script insertar_usuario.php
        parametros_POST = new ArrayList<NameValuePair>(4);
        parametros_POST.add(new BasicNameValuePair("id_ubicacion", Integer.toString(id_ubicacion)));
        parametros_POST.add(new BasicNameValuePair("foto", ruta_foto));
        parametros_POST.add(new BasicNameValuePair("id_foto", Integer.toString(id_foto)));

        System.out.println("LOS DATOS A INSERTAR SON: ");
        System.out.println("id_ubicacion: " + this.id_ubicacion);
        System.out.println("ruta_foto: " + ruta_foto);
        System.out.println("id_foto: "+id_foto);

        try {
            // establece la entidad => como una lista de pares URL codificada.
            // Esto suele ser útil al enviar una solicitud HTTP POST
            httppost.setEntity(new UrlEncodedFormEntity(parametros_POST, "UTF-8"));
            System.out.println("parametros_POST: " + parametros_POST);
             // intentamos ejecutar la solicitud HTTP POST
            httpclient.execute(httppost);
            resul = true;
        } catch (UnsupportedEncodingException e) {
            // La codificación de caracteres no es compatible
            resul = false;
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // Señala un error en el protocolo HTTP
            resul = false;
            e.printStackTrace();
        } catch (IOException e) {
            // Error de Entrada / Salida
            resul = false;
            e.printStackTrace();
        }


        // devuelve el resultado de la inserción
        return resul;

    } // fin insertar()
    /**
     * Muestra una tostada de si se pudo o no insertar el tipo
     *
     * @param result
     */
    protected void onPostExecute(String result) {
        if (result.equals("OK")) {
            // inserción correcta
            Config.tostada(context, "Imagen insertada con éxito");
        } else
            Config.tostada(context, "ERROR, imagen no insertada");
    } // fin onPostExecute()

}
