package com.pms.mapasgoogle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class WS_Cargar_Ubicaciones extends AsyncTask<Void, Void, String> {

    private final Activity context; //contexto de la actividad principal
    private final String URL_PHP, registros, usuarioLogeado;
//    private final EditText tipo, nombre, ubicacion, direccion, web, cod_usuario, pas_usuario;
    private boolean segundaCarga;
    public final List<Ubicaciones> listaUbicaciones;
    int u;

    WS_Cargar_Ubicaciones(Activity context, List<Ubicaciones> listaUbicaciones, String url_php,
                          String registros, String usuarioLogeado, boolean segundaCarga) {
        this.context = context;
        this.listaUbicaciones = listaUbicaciones;
        this.URL_PHP = url_php;
        this.registros = registros;
        this.usuarioLogeado = usuarioLogeado;
        this.segundaCarga = segundaCarga;
        System.out.println("En constructor WS_Cargar_Ubicaciones --> registros: "+ registros);
    }

    ProgressDialog loading; // barra de progreso
    // antes de lanzar la tarea en 2º plano (doInBackground) define que hacer:
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // muestra una barra de progreso circular
    //    loading = ProgressDialog.show(context,"Buscando...","Cargando...",false,false);
        // Crea el diálogo de progreso si es necesario
        if (loading == null)
            loading = new ProgressDialog(context);
        loading.setMessage("Conectando a la Base de Datos....");
        loading.setIndeterminate(false);
        loading.setCancelable(false);
        loading.show();
    }
    /**
     * tarea en segundo plano para obtener todos los registros de la base de datos y
     * cargarlos en 'listaUbicaciones'
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {
        String hay_datos = "ERROR";
        if (filtrarDatos()) {
            // hay ubicación que cargar
            hay_datos = "OK";
        } else
            // no hay ubicación que cargar
            hay_datos = "ERROR";

        return hay_datos;
    }
    /**
     * Método que crea un objeto Ubicaciones con los datos(String)recibidos del servidor y lo almacena en
     * nuestro ArrayList listaUbicaciones
     *
     * @return: true /false
     * devuelve true => si hay algún nombre que cargar
     * false => en caso contrario
     */
    private boolean filtrarDatos() {
        String respuesta = "";
        Ubicaciones ubicacion = null;
        boolean resul = false;
        boolean error_json = false; // para detectar un error al transformar a JSON
        listaUbicaciones.clear();
        respuesta = cargar();
        System.out.println("respuesta aaa: "+respuesta);
        // para cargar solo los registros que pertence al un usuario --> comprueba 2 cosas:
        // 1- si se trata de un usuario distinto al usuario administrador
        // 2- si la carga de ubicaciones no se hace por 1ª vez (segundaCarga=true)
        if (!usuarioLogeado.equals("1") && segundaCarga) //si es cualquier otro usuario, solo muestra los establecimientos que le pertenezcan
               respuesta = registros;


        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRR  espuesta: "+respuesta);//////////////////////
        listaUbicaciones.clear();
        // Compara respuesta ignorando mayúsculas y minúsculas con la cadena ""
        if (!respuesta.equalsIgnoreCase("")) { //si hay datos
            JSONObject json; // define un objeto JSON
            try {
                // crea el objeto JSON en base al String respuesta
                json = new JSONObject(respuesta);

                // devuelve un array json si existe el índice de nombre "tabla_ubicacion"**
                // ***($json['tabla_ubicacion'][]=$fila; -- => archivo.php=>mostrarUbicaciones.php )
                JSONArray jsonArray = json.optJSONArray("tabla_ubicacion");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ubicacion = new Ubicaciones();
                    // obtener el objeto JSON de la posición i
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    // guarda la ubicacion jsonArrayChild en el objeto 'ubicacion'
                    // **(el índice debe ser el nombre de la columna de la tabla=>ubicacion)
                    ubicacion.setId(jsonArrayChild.optInt("id_ubicacion"));
                    ubicacion.setNombre(jsonArrayChild.optString("nombre"));
                    ubicacion.setDireccion(jsonArrayChild.optString("direccion"));
                    ubicacion.setTelefono(jsonArrayChild.optString("telefono"));
                    ubicacion.setUbicacion(jsonArrayChild.optString("ubicacion"));
                    ubicacion.setTipo(jsonArrayChild.optString("tipo"));
                    ubicacion.setWeb(jsonArrayChild.optString("web"));
                    ubicacion.setCodUsuario(jsonArrayChild.optString("cod_usuario"));


                    // añade la ubicación a la lista de ubicaciones
                    listaUbicaciones.add(ubicacion);

                    //Muestro por consola el contenido de listaUbicaciones
                    System.out.println("Nombre: "+listaUbicaciones.get(i).getNombre());
                    System.out.println("Direccion: "+listaUbicaciones.get(i).getDireccion());
                    System.out.println("Telefonoooooo: "+listaUbicaciones.get(i).getTelefono());
                    System.out.println("Ubicacion: "+listaUbicaciones.get(i).getUbicacion());
                    System.out.println("Web: "+listaUbicaciones.get(i).getWeb());
                    System.out.println("Tipo: "+listaUbicaciones.get(i).getTipo());
                    System.out.println("---------------------------------------");
                //    int posicion =  listaUbicaciones.indexOf(listaUbicaciones.get(i).getId());

                }
                System.out.println("listaUbicaiconessss despues de cargar todo esta vacia: "+listaUbicaciones.isEmpty());

            } catch (JSONException e) {
                // Error al convertir a JSON
                // => esto sucede porque no hay ubicaciones en la consulta y el array json está vacío
                // o por cualquier otro motivo
                e.printStackTrace();
                error_json = true;
            }

            if (error_json)
                resul = false;
            else
                resul = true;
        } else // no hay datos
            resul = false;

        return resul;
    } // fin filtrarDatos()
    /**
     * Método que realiza una consulta a la BD de todas las ubicaciones
     * a través del script => mostrarUbicaciones.php
     *
     * @return: Devuelve los datos del servidor en forma de String
     */
    private String cargar() {
        // almacenará la respuesta de la BD del servidor en un String
        String resultado = "";
        // crea el cliente HTTP por defecto
        HttpClient httpclient = new DefaultHttpClient();
        // crea el objeto httpost para realizar la solicitud POST del script PHP correspondiente
        HttpPost httppost = new HttpPost(URL_PHP + Config.PHP_MOSTRAR_TODO); // Url del Servidor
        System.out.println("******La URL donde se encuentra el archivo PHP es: "+URL_PHP+Config.PHP_MOSTRAR_TODO);

        // para la respuesta de la solicitud al servidor
        HttpResponse response;

        try {
            //ejecuta petición enviando datos por POST y obtiene respuesta
            response = httpclient.execute(httppost);
            // obtiene la entidad del mensaje de respuesta HTTP
            HttpEntity entity = response.getEntity();
            // crea un nuevo flujo de entrada tipo InputStream => instream
            // => con la entidad HTTP => entity
            InputStream instream = entity.getContent();
            // convierte la respuesta del servidor => instream =>
            // a formato cadena (String) => resultado
            resultado = convertStreamToString(instream);
            System.out.println("Resultado ooo: "+resultado);

        } catch (ClientProtocolException e) {
            // error en el protocolo HTTP
            e.printStackTrace();
        } catch (IOException e) {
            // error de E/S
            e.printStackTrace();
        }

        return resultado;

    } // fin cargar()

    /**
     * Método que convierte la respuesta del servidor => is
     * => a formato cadena (String) => y la devuelve
     *
     * @param is: respuesta del servidor
     * @throws IOException
     * @return: respuesta en formato String
     */
    public static String convertStreamToString(InputStream is) throws IOException {

        String resul = ""; // resultado a devolver
        BufferedReader reader = null;

        //Convierte respuesta a String
        try {
            // crear un flujo de entrada de tipo BufferedReader en base
            // a un flujo de entrada InputStreamReader con un juego de caracteres de tipo "UTF-8"
            // el tamaño del buffer es de 8 caracteres
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);

            // quién no sepa que es un StringBuilder =>
            // http://picandocodigo.net/2010/java-stringbuilder-stringbuffer/

            // crea una cadena de caracteres modificable => StringBuilder
            StringBuilder sb = new StringBuilder();

            // lee todas las líneas del fichero a través del flujo de entrada reader
            String line = null;
            while ((line = reader.readLine()) != null)
                // añade cada línea leída del fichero con un salto de línea => "\n"
                sb.append(line + "\n");

            // guardamos el resultado de la respuesta en el String => resul
            resul = sb.toString();

            // Log.e("getpostresponse", " resul= " + sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("log_tag", "Error E/S al convertir el resultado " + e.toString());

        } finally {
            // la clausula finally siempre se ejecuta => salten excepciones o no
            // por eso es conveniente intentar cerrar aquí los flujos => por si hay un error
            // en la lectura del flujo por ejemplo de tipo E/S => IOException
            try {
                if (is != null)
                    is.close(); // cerrar el flujo de entrada is
                if (reader != null)
                    reader.close(); // cerrar el flujo de entrada reader
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("log_tag", "Error E/S al cerrar los flujos de entrada " + e.toString());
            }

        }

        return resul;

    } // fin convertStreamToString()
    /**
     * Distingue si se ejecuta por 1ª vez o no
     * 1ª vez --> carga todas las ubicaciones de la BD y las muestra en el mapa (MapsActivity).
     * 2ª vez o más --> carga todas las ubicaciones y muestra en activity 'Crud' la primera ubicación de la lista.
     * Muestra mensaje de error si no se hay ubicaciones que cargar
     * @param result
     */
    protected void onPostExecute(String result) {
        loading.dismiss();// oculta la barra de progreso
        if (result.equals("OK")) {
            //Tengo que distinguir si es la 1ª vez que carga o no
            if (segundaCarga == false) {//carga por 1ª vez --> en este caso carga las ubicaciones en 'MapsActivity'
                ((MapsActivity) context).recorrerListaUbicaciones();// Ejecuta el método 'recorrerListaUbicaciones()' de la clase 'MapsActivity'
                System.out.println("segundaCarga: " + segundaCarga + " --> LA APLICACIÓN ES INICIADA POR 1ª VEZ Y MUESTRA LAS UBICACIONES EN EL MAPA");
            }else {// No es la 1ª vez que carga, por lo que en este caso carga y muestra las ubicaciones en la activity 'Crud'
                ((Crud)context).recorrerListaUbicaciones();// Ejecuta el método 'recorrerListaUbicaciones()' de la clase 'Crud'
                System.out.println("segundaCarga: " + segundaCarga + " --> MUESTRA LOS REGISTROS DE LA BD EN 'CRUD.XML'");
            }
        } else Config.tostada(this.context, "ERROR, no hay ubicaciones que cargar");
    } // fin onPostExecute()

}
