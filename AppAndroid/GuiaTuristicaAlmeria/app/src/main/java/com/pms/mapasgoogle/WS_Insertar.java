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

public class WS_Insertar extends AsyncTask<Void, Void, String>{

    private Activity context;

    private final String URL_PHP, usuarioLogeado;
    private final EditText id, nombre, direccion, ubicacion, tipo, web, telefono;
    private boolean webCorrecta;

    //constructor
    public WS_Insertar(Activity context,  String url_php, EditText id,
                       EditText nombre, EditText direccion, EditText telefono, EditText ubicacion, EditText web, EditText tipo,
                       String usuarioLogeado, boolean webCorrecta){
        this.context = context;
        URL_PHP = url_php;
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.ubicacion = ubicacion;
        this.web = web;
        this.tipo = tipo;
        this.usuarioLogeado=usuarioLogeado;
        this.webCorrecta = webCorrecta;
    }

    @Override
    protected String doInBackground(Void... params) {

        String resultado = "ERROR";
        System.out.println("11111111111111111111 ID: "+id.getText().toString());
        if (id.getText().toString().isEmpty()) {
            System.out.println("222222222222222222222 ID: "+id.getText().toString());
            if (insertar())
                // la inserción del establecimiento ha sido exitosa
                resultado = "OK";
            else
                // ha habido un error al insertar el estableciomiento y no se pudo insertar
                resultado = "ERROR";
        }
        return resultado;
    }


    /**
     * Método que intenta insertar los datos en el servidor
     * a través del script => insert.php
     *
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
        httppost = new HttpPost(URL_PHP + Config.PHP_INSERTAR); // Url del Servidor
        System.out.println("url: "+URL_PHP + Config.PHP_INSERTAR);

        //Añadimos los datos que vamos a enviar por POST al script insertar.php
        // ** =>debe coincidir la clave con índice del $_POST[] indicado en el script insertar.php
        parametros_POST = new ArrayList<NameValuePair>(4);
        parametros_POST.add(new BasicNameValuePair("nombre", nombre.getText().toString().trim()));
        parametros_POST.add(new BasicNameValuePair("direccion", direccion.getText().toString().trim()));
        parametros_POST.add(new BasicNameValuePair("telefono", telefono.getText().toString().trim()));
        parametros_POST.add(new BasicNameValuePair("ubicacion", ubicacion.getText().toString().trim()));
        //Si la web introducida no es correcta no inserta nada en el campo de la web
        if (!webCorrecta) parametros_POST.add(new BasicNameValuePair("web", ""));
        else parametros_POST.add(new BasicNameValuePair("web", web.getText().toString().trim()));
        parametros_POST.add(new BasicNameValuePair("tipo", tipo.getText().toString().trim()));
        parametros_POST.add(new BasicNameValuePair("cod_usuario", usuarioLogeado));

    /*    ValidarUsuarioContraseña enviarUsuPass = new ValidarUsuarioContraseña();//Objeto de la clase 'ValidarUsuarioContraseña'
        final String res=enviarUsuPass.enviarPost(usuarioLogeado, contraseñaLogeada);
        System.out.println("///****-----Despues de INSERTAR res: "+res);*/

        System.out.println("LOS DATOS A INSERTAR SON: ");
        System.out.println("Nombre: " + nombre.getText().toString().trim());
        System.out.println("Dirección: " + direccion.getText().toString().trim());
        System.out.println("Telefono: " + telefono.getText().toString().trim());
        System.out.println("Ubicación: " + ubicacion.getText().toString().trim());
        System.out.println("Web: " + web.getText().toString().trim());
        System.out.println("Tipo: " + tipo.getText().toString().trim());
        System.out.println("cod_usuario: "+ usuarioLogeado);

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
            Config.tostada(context, "Establecimiento insertado con éxito");
//            listaUbicaciones.clear();
        } else
            Config.tostada(context, "ERROR, el elemento a insertar no puede tener 'ID'");
        //    Config.tostada(context, "ERROR, elemento no insertado\nCompruebe....\nQue campo 'id' este vacio");
    } // fin onPostExecute()

} // fin clase WS_insertar
