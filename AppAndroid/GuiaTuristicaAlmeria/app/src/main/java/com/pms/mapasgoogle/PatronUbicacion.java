package com.pms.mapasgoogle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Clase que comprueba que las coordenadas siguen un patrón
*/
public class PatronUbicacion {

    public static Matcher coordenadasCorrectas(String d) {
        //expresión regular que se ajusta al patrón de coordenadas de geolocalizicón correctas
        String pattern = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(d);
        return mat;
    }
}
