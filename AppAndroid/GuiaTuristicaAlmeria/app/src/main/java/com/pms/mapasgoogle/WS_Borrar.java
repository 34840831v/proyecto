package com.pms.mapasgoogle;

import android.app.Activity;
import android.app.ProgressDialog;
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

import static com.pms.mapasgoogle.Crud.direccion;
import static com.pms.mapasgoogle.Crud.nombre;
import static com.pms.mapasgoogle.Crud.tipo;
import static com.pms.mapasgoogle.Crud.ubicacion;
import static com.pms.mapasgoogle.Crud.url;
import static com.pms.mapasgoogle.Crud.web;


// Este Web Service permitirá eliminar una ubicación de la BD
// definimos la clase WS_Borrar que borra una ubicación en la BD
// para que se lancen sus tareas en segundo plano
public class WS_Borrar extends AsyncTask<Void,Void,String> {

    private Activity context;
    private int posicion;
    private String id_ubicacion;
    private List<Ubicaciones> listaUbicaciones;
    private Crud crud;

    public WS_Borrar(Activity context, int posicion, List<Ubicaciones> listaUbicaciones) {
        this.context = context;
        this.posicion = posicion;
        this.listaUbicaciones = listaUbicaciones;
    }
    ProgressDialog loading;
    /*
     * Antes de ejecutar el hilo en segundo plano, muestra una barra de progreso circular
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = ProgressDialog.show(context, "Borrando...", "Espere...", false, false);
    }

    /*
     * Eliminar una ubicación en segundo plano
     */
    @Override
    protected String doInBackground(Void... params) {
        String resultado = "ERROR";
        System.out.println("RRRRR resultado: "+resultado);
        System.out.println("nombre en listaUbicaciones: "+listaUbicaciones.toString());
        if (eliminarUbicacion()) {
            // la eliminación de la ubicación ha sido exitosa
            resultado = "OK";
            System.out.println("resultado:"+resultado);
            nombre.setText("");
            direccion.setText("");
            ubicacion.setText("");
            web.setText("");

            tipo.setText("");
        }// else
            // ha habido un error al eliminar la ubicación
          //  resultado = "ERROR";
        System.out.println("resultado: "+resultado);
        return resultado;
    }

    /*
     *Al terminar el hilo muestra lo sucedido
     */
    @Override
    protected void onPostExecute(String result) {
        loading.dismiss();//ocultamos barra de progreso

        if (result.equals("OK")) {
            Config.tostada(context, "Registro eliminado correctamente");
            //Refrescamos la IU, con la lista actualizada
            System.out.println("");
            posicion=0;
            listaUbicaciones.clear();

        } else
            Config.tostada(context,"ERROR, Establecimiento no eliminado!");


}//Fin clase WS_Borrar

    /**
     * Método para eliminar al espectaculo actual de la BD
     */
    private boolean eliminarUbicacion() {
        if (listaUbicaciones.isEmpty()) {
            System.out.println("lllllllllllllllllistaUbicaciones esta vacia");
            return false;
        } else {
            Ubicaciones ubic = listaUbicaciones.get(posicion);
            System.out.println("EEEEEEEEEEEEEEEEEEEEEE El nombre es: " +ubic.getNombre());
            System.out.println("iiiiiiiiiiiiiiiiiiiiiiiii id: "+ubic.getId());
            boolean resul = false;
            // interfaz para un cliente HTTP
            HttpClient httpclient;
            // define una lista de parámetros ("clave" "valor") que serán enviados por POST al script php
            List<NameValuePair> parametros_GET;
            // define un objeto para realizar una solicitud POST a través de HTTP
            HttpPost httppost;
            // crea el cliente HTTP
            httpclient = new DefaultHttpClient();
            // creamos el objeto httpost para realizar una solicitud POST al script borrarUbicacion.php
            httppost = new HttpPost(url + Config.PHP_BORRAR); // Url del Servidor
            System.out.println("httppost: "+httppost.toString());

            //Añadimos los datos que vamos a enviar por GET al script borrarUbicacion.php

            //************** A tener en cuenta en la Tarea********************************************
            //***** para poder modificar  y eliminar, debe añadirse el id de la ubicación a actualizar
            // debe coincidir la clave con índice del $_GET[] indicado en el script borrarUbicacion.php
            parametros_GET = new ArrayList<NameValuePair>(1);
            id_ubicacion = String.valueOf(ubic.getId());
            System.out.println("El id a borrar es paramentros_GET: "+id_ubicacion);
            parametros_GET.add(new BasicNameValuePair("id_ubicacion", id_ubicacion));

            //**********************************************************************
            try {
                // establece la entidad => como una lista de pares URL codificada.
                // Esto suele ser útil al enviar una solicitud HTTP GET
                httppost.setEntity(new UrlEncodedFormEntity(parametros_GET));
                // intentamos ejecutar la solicitud HTTP GET
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
            System.out.println("resul: "+resul);
            // devuelve el resultado del borrado
            return resul;
        }
    }// fin de borrarUbicacion()
}