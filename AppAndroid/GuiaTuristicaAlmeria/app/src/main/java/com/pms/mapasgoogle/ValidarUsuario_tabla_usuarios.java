package com.pms.mapasgoogle;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ValidarUsuario_tabla_usuarios {
    //Método que realiza la operación POST enviando el usuario extrae los datos
    public String enviarPost(String usu) {
        String parametro="usu="+usu;
        System.out.println("parametro: "+parametro);
        HttpURLConnection connection=null;
        String usuario_validado="";
        try{
            //**Realizamos los pasos para enviar los datos al servidor**

            //almacena la url
            URL url = new URL("https://"+Config.URL_PHP+Config.PHP_VALIDAR_USUARIO);
            System.out.println("url: "+url);
            //Establecemos conexión
            connection=(HttpURLConnection)url.openConnection();
            System.out.println("1");
            //indicamos el método de envio de datos (POST)
            connection.setRequestMethod("POST");
            System.out.println("2");
            //Indicar los parametro que se van a enviar
            connection.setRequestProperty("Content-Length",""+Integer.toString(parametro.getBytes().length));
            System.out.println("3");
            //Requerimos salida de datos
            connection.setDoOutput(true);
            System.out.println("4");
            //enviar los datos
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            System.out.println("5");
        //    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            //Escribimos los datos que van a salir
            wr.writeBytes(parametro);
            System.out.println("6");
            //cerramos
            wr.close();
            System.out.println("7");

            //**Pasos a realizar para recuperar los datos que debemos de traer desde el servidor **
            //**Tomo la respuesta que me envia el WebService y la recorre para ponerla en la variable 'respuesta'**

            //Leer o consumir el dato que nos devuelve la conexión al webService
            Scanner inStream = new Scanner(connection.getInputStream());
            //Recorro todas la líneas que devuelve el WebService
            //y lo concateno a 'repuesta'
            while(inStream.hasNextLine()){
                usuario_validado+=inStream.nextLine();
            }
        }catch (Exception e){
            System.out.println("No ha realizado la conexion. Salta al catch");
        }
        //devuelve la respuesta con los datos del servidor
        return usuario_validado;
    }
}
