package com.pms.mapasgoogle;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Clase que se encarga de enviar usuario y contraseña que se está logeando y en el caso de que exista
 * extraer sus datos de la base de datos
 */
public class ValidarUsuarioContraseña {
    //Método que realiza la operación POST enviando el usuario y contraseña y extrae los datos
    public String enviarPost(String usu, String pas) {
        String parametros="usu="+usu+"&pas="+pas;
        System.out.println("parametros: "+parametros);
        HttpURLConnection connection=null;
        String respuesta="";
        try{
            //**Realizamos los pasos para enviar los datos al servidor**

            //almacena la url
            URL url = new URL("https://"+Config.URL_PHP+Config.PHP_VALIDAR);
            System.out.println("url: "+url);
            //Establecemos conexión
            connection=(HttpURLConnection)url.openConnection();
            //indicamos el método de envio de datos (POST)
            connection.setRequestMethod("POST");
            //Indicar los parametros que se van a enviar
            connection.setRequestProperty("Content-Length",""+Integer.toString(parametros.getBytes().length));
            //Requerimos salida de datos
            connection.setDoOutput(true);
            //enviar los datos
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            //Escribimos los datos que van a salir
            wr.writeBytes(parametros);
            //cerramos
            wr.close();

            //**Pasos a realizar para recuperar los datos que debemos de traer desde el servidor **
            //**Tomo la respuesta que me envia el WebService y la recorre para ponerla en la variable 'respuesta'**

            //Leer o consumir el dato que nos devuelve la conexión al webService
            Scanner inStream = new Scanner(connection.getInputStream());
            //Recorro todas la líneas que devuelve el WebService
            //y lo concateno a 'repuesta'
            while(inStream.hasNextLine()){
                respuesta+=inStream.nextLine();
            }
            connection.disconnect();
        }catch (Exception e){
            System.out.println("No ha realizado la conexion. Salta al catch");
        }
        //devuelve la respuesta con los datos del servidor
        return respuesta;
    }
}
