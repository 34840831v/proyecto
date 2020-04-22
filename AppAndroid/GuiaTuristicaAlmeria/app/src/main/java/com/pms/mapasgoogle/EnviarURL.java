package com.pms.mapasgoogle;

import java.net.MalformedURLException;
import java.net.URL;

public class EnviarURL {
    URL url;
    //Método que realiza la operación POST enviando el usuario y contraseña y extrae los datos
    public URL enviar_Url(String archivo_php) {
        try {//Controla
            url = new URL("https://" + archivo_php);
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
