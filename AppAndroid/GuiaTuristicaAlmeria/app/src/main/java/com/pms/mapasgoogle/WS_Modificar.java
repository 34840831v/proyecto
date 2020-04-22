package com.pms.mapasgoogle;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.EditText;

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

public class WS_Modificar extends AsyncTask<Void, Void, String> {
    //para recibir el contexto de la actividad principal
    private final Activity context;
    private final int posicion;
    private final String URL_PHP;
    EditText nombre, direccion, telefono, ubicacion, tipo, web;
    private final List<Ubicaciones> listaUbicaciones;

    //constructor
   public WS_Modificar(Activity context, int posicion, List<Ubicaciones> listaUbicaciones, String url_php,
                         EditText nombre, EditText direccion, EditText telefono, EditText ubicacion, EditText web, EditText tipo){
        this.context = context;
        this.posicion = posicion;
        this.listaUbicaciones = listaUbicaciones;
        URL_PHP = url_php;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.ubicacion = ubicacion;
        this.web = web;
        this.tipo = tipo;
       System.out.println("88888888888888888 telefono: "+telefono.getText());
    }

    /**
     * Actualizar el tipo en segundo plano
     */
    @Override
    protected String doInBackground(Void... params) {
        String resultado = "ERROR";
        if (modificar())
            // la modificación del establecimiento ha sido exitosa
            resultado = "OK";
        else
            // ha habido un error al modificar el establecimiento
            resultado = "ERROR";

        return resultado;
    }//Fin doInBackground


    /**
     * Método para modificar el tipo actual de la BD
     * a través del script => modificar.php
     * @return true/false => modificación existosa/error al modificar
     */
    private boolean modificar() {
        System.out.println("En modificar listaUbicaciones vacia: "+listaUbicaciones.isEmpty());
        if (listaUbicaciones.isEmpty()) {
            return false;
        } else {
            //obtiene posición en la lista del tipo a modificar
            Ubicaciones ubic = listaUbicaciones.get(posicion);
            boolean resul = false;
            // interfaz para un cliente HTTP
            HttpClient httpclient;
            // define una lista de parámetros ("clave" "valor") que serán enviados por POST al script php
            List<NameValuePair> parametros_PUT;
            // define un objeto para realizar una solicitud POST a través de HTTP
            HttpPost httppost;
            // crea el cliente HTTP
            httpclient = new DefaultHttpClient();
            // creamos el objeto httpost para realizar una solicitud POST al script PHP correspondiente
            httppost = new HttpPost(URL_PHP + Config.PHP_MODIFICAR); // Url del Servidor

            //obtiene el id del tipo a modificar
            String idUbic = String.valueOf(ubic.getId());
            System.out.println("iiiiiiiiiiiiiiiiiiiii idUbic: "+idUbic);
            //Añadimos los datos que vamos a enviar por POST al script modificar.php
            //** para poder modificar  (y eliminar), debe añadirse el id del tipo a actualizar
            // debe coincidir la clave con índice del $_POST[] indicado en el script modificar.php
            parametros_PUT = new ArrayList<NameValuePair>(5);
            parametros_PUT.add(new BasicNameValuePair("id_ubicacion", idUbic));
            parametros_PUT.add(new BasicNameValuePair("nombre", nombre.getText().toString().trim()));
            parametros_PUT.add(new BasicNameValuePair("direccion", direccion.getText().toString().trim()));
            parametros_PUT.add(new BasicNameValuePair("telefono", telefono.getText().toString().trim()));
            parametros_PUT.add(new BasicNameValuePair("ubicacion", ubicacion.getText().toString().trim()));
            parametros_PUT.add(new BasicNameValuePair("web", web.getText().toString().trim()));
            parametros_PUT.add(new BasicNameValuePair("tipo", tipo.getText().toString().trim()));

            try {
                // establece la entidad => como una lista de pares URL codificada.
                // Esto suele ser útil al enviar una solicitud HTTP POST
                httppost.setEntity(new UrlEncodedFormEntity(parametros_PUT, "UTF-8"));
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

        }
    }//Fin actualizar()

    /**
     * Muestra un mensaje indicando si se ha modificado o no el tipo.   *
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {

        if (result.equals("OK")) {
            System.out.println("En onPostExecute de modificar resultado: "+result);
            Config.tostada(context, "Ubicación ha sido modificada correctamente");
        } else
            Config.tostada(context, "ERROR, Ubicación no modificada!");
            System.out.println("En onPostExecute de modificar resultado: "+result);
    }//Fin onPostExecute

}//fin clase WS_modificar
