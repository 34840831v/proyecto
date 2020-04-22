package com.pms.mapasgoogle;

import org.json.JSONArray;
/**
 * Clase que comprueba si hay registros en la base de datos
 */
public class Existe_usuario {
    //Método que comprueba si hay o no registros
    //(Si 'respuesta' es 1 hay registros)
    //(Si 'respuesta' es 0 no hay registros)
    public int hay_registros(String rspta){
        int res=0;
        try{
            //convierte nuevamente el String respuesta a JSON para poderlo contar
            JSONArray json = new JSONArray(rspta);
            //averigua cuantos elementos tiene el json, es decir, su longitud
            if (json.length()>0) res=1;
        }catch (Exception e){}
        return res;
    }
}
