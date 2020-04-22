package com.pms.mapasgoogle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WS_BuscarUnaUbicacion extends AsyncTask<Void, Void, String> {

    private final Activity context; //contexto de la actividad principal
    private final String ubicacion_buscada, usuarioLogeado;
//    private final EditText id_ubicacion, tipo, nombre, ubicacion, direccion, web, cod_usuario, pas_usuario;
//    private boolean verUbicacion;
    public final List<Ubicaciones> listaUbicaciones;
    int u, posicion, tipoUbicacion;
    ProgressDialog pDialog; // barra de progreso
    public boolean registroEncontrado = false;

    WS_BuscarUnaUbicacion(Activity context, List<Ubicaciones> listaUbicaciones, String usuarioLogeado, String ubicacion_buscada) {
        this.context = context;
        this.listaUbicaciones = listaUbicaciones;
        this.usuarioLogeado = usuarioLogeado;
        this.ubicacion_buscada = ubicacion_buscada;
    }

    // antes de lanzar la tarea en 2º plano (doInBackground) define que hacer:
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // muestra una barra de progreso circular
        pDialog = ProgressDialog.show(context,"Buscando...","Espere...",false,false);
    }

    @Override
    protected String doInBackground(Void... voids) {

        String resultado = "";
        BuscaUbicacion buscaUbicacion=new BuscaUbicacion();
        // Llama al método 'enviarPost' de la clase 'BuscaUbicacion', el cual envia la 'ubicacion_buscada' y
        // la busca en la BD del WebService, guardando el resultado en el String 'res'
        final String res=buscaUbicacion.enviarPost(ubicacion_buscada);
        System.out.println("En doInBackground de WS_BuscarUnaUbicacion res: "+res);
        try{
            //Tanto si existe el id_ubicación que estamos buscando, como si no existe,
            //'res' siempre va a tener contenido, ya que, cuando si existe id_ubicacion
            //contendrá el registro correspondiente y cuando no existe id_ubicacion
            //res = [], por lo tanto, res.length()= 2
            if (res.length()>2){//Si es mayor que 2 es señal que el id_ubicación si existe en la base de datos
                System.out.println("res.length(): "+res.length());
                resultado = "OK";// hay ubicación que cargar
                System.out.println("rrrr resultado: "+resultado);
                listaUbicaciones.clear();
                posicion = 0;
                try {
                    Ubicaciones ubic = null;
                    ubic = new Ubicaciones();
                    // crea el objeto JSON en base al String respuesta
                    JSONObject json = new JSONObject(res);
                    System.out.println("xxxxxxxxxxx usuarioLogeado: "+usuarioLogeado);
                    System.out.println("xxxxxx cod_usuario: "+json.getJSONObject("resultado").getString("cod_usuario"));
                    //1- En el caso de que el usuario logeado sea igual que cod_usuario del registro de la BD o sea usuario 1 (admin) mostraria la ubicacion solicitada
                    if (json.getJSONObject("resultado").getString("cod_usuario").equals(usuarioLogeado)||usuarioLogeado.equals("1")) {
                      //  Config.tostada(this.context, "Imposible mostrar datos");
                        System.out.println("usuarioLogeado es "+usuarioLogeado+" y cod_usuario es "+json.getJSONObject("resultado").getString("cod_usuario"));

                        // guarda la ubicacion getJSONObject encontrada, en el objeto
                        // 'ubic' si en el json existe el índice de nombre "resultado"
                        // y coincide con el nombre de la columna de la tabla_ubicacion)
                        ubic.setId(Integer.parseInt(json.getJSONObject("resultado").getString("id_ubicacion")));
                        ubic.setNombre(json.getJSONObject("resultado").getString("nombre"));
                        ubic.setDireccion(json.getJSONObject("resultado").getString("direccion"));
                        ubic.setTelefono(json.getJSONObject("resultado").getString("telefono"));
                        ubic.setUbicacion(json.getJSONObject("resultado").getString("ubicacion"));
                        ubic.setWeb(json.getJSONObject("resultado").getString("web"));
                        ubic.setTipo(json.getJSONObject("resultado").getString("tipo"));
                        // añade la ubicación a la lista de ubicaciones
                        listaUbicaciones.add(ubic);
                        //Muestra en la lista deplegable 'sCategoria' la categoria (BAR, CAFETERIA, RESTAURANTE)
                        //a la que corresponde el id_ubicacion buscado
                        tipoUbicacion = Integer.parseInt(json.getJSONObject("resultado").getString("tipo"));
                        //Llama al método que me muestra la categoria, que se encuentra en 'Crud.java'
                        ((Crud) context).saberCategoria(tipoUbicacion);
                        //Muestro por consola el contenido de listaUbicaciones
                        System.out.println("nnnn id_ubicacion: " + ubic.getId());
                        System.out.println("Id_ubicacion: " + json.getJSONObject("resultado").getString("id_ubicacion"));
                        System.out.println("nnnn Nombre: " + ubic.getNombre());
                        System.out.println("Nombre: " + json.getJSONObject("resultado").getString("nombre"));
                        System.out.println("Direccion: " + json.getJSONObject("resultado").getString("direccion"));
                        System.out.println("Ubicacion: " + json.getJSONObject("resultado").getString("ubicacion"));
                        System.out.println("Web: " + json.getJSONObject("resultado").getString("web"));
                        System.out.println("Tipo: " + json.getJSONObject("resultado").getString("tipo"));
                        registroEncontrado = true;
                    }else {//Si usuarioLogeado no coincide con el cod_usuario del registro de la BD buscado
                        //Config.tostada(this.context, "Imposible mostrar datos\n" + "ID no corresponde al usuario " + usuarioLogeado);
                        resultado = "ERROR";
                        System.out.println("resultado(OK-ERROR): "+resultado);
                        System.out.println("Imposible mostrar datos\n" + "ID no corresponde al usuario " + usuarioLogeado);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{//Si res.length()<= 2, es señal de que no existe el id_ubicacion en la BD
                resultado = "ERROR";// no hay ubicación que cargar
                System.out.println("rrrr no resultado: "+resultado);
                System.out.println("res.length(): "+res.length());
        //        Config.tostada(context,"Esa ubicación no existe");
                System.out.println("Esa ubicación no existe");
                System.out.println("res.length(): "+res.length());
            }
        }catch (Exception e){}
        return resultado;
    }

    protected void onPostExecute(String result) {
        pDialog.dismiss();// oculta la barra de progreso
        System.out.println("rrrr result: "+result);
        if (result.equals("OK")) {
            System.out.println("posicionnnnnnnn onPostExecute de WS_BuscarUnaUbicacion: "+posicion);
            //Llama al método que me muestra la ubicación que se encuentra en 'Crud.java'
            ((Crud) context).mostrarUnaUbicacion(posicion);
            //      mostrarUbicacion();
        }else Config.tostada(this.context, "ID inexistente o no pertenece a usuario "+usuarioLogeado);
    } // fin onPostExecute()

}
