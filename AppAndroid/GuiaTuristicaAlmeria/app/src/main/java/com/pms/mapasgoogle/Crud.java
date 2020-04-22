package com.pms.mapasgoogle;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crud extends AppCompatActivity {

    // atributos
    public static EditText accion, id_ubicacion, nombre, direccion, telefono, ubicacion, web, tipo;
    public boolean webCorrecta;

    public static Spinner sCategoria;

    private Button nuevo;

    private ImageButton mas;
    private ImageButton menos;
    public int posicion=0;  // posición de la ubicacion a visualizar de la lista de ubicaciones
    public int u, tipoUbicacion, mostrar;

    public List<Ubicaciones> listaUbicaciones; // Lista de ubicaciones obtenidos de la BD
//    public static Config conf;

    private Ubicaciones ubic; // objeto Ubicaciones

    private MapsActivity ActividadMapas;

    // URL del directorio de los scripts php del servidor
    public static String url, usuarioLogeado, registrosPorUsuario, usuariosRegistrados;

    boolean segundaCarga = false;

    private Button boton_administrar;
    private static Button boton_extraer_ubicacion;

    private boolean activo=false;

    String elemento, id, no, di, tel, ub, we, ti, usuario_validado;

    // objeto para describir una localizaicón en forma de un conjunto de cadenas
    private Address objetoDireccion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        // referencia a los EditText
        accion = (EditText) findViewById(R.id.accion);
        accion.setFocusable(false);
        id_ubicacion = (EditText) findViewById(R.id.id);
        tipo = (EditText) findViewById(R.id.tipo);
        nombre = (EditText) findViewById(R.id.nombre);
        direccion = (EditText) findViewById(R.id.direccion);
        telefono = (EditText) findViewById(R.id.tlf);
        ubicacion = (EditText) findViewById(R.id.ubic);

        web = (EditText) findViewById(R.id.web);
        sCategoria = (Spinner) findViewById(R.id.sCategoria);


/////////////// RECOGE EL USUARIO Y CONTRASEÑA LOGEADOS PARA MOSTRARLOS EN 'Crud'///////////////////
        //Obtengo el usuario logeado en 'LoginActivity' para mostrarlo en activity 'Crud' y poder
        //saber que usuario está administrando ubicaciones
        Bundle usuario=getIntent().getExtras();//Extrae el usuario logeado en 'LoginActivity'
        usuarioLogeado = usuario.getString("usuario");//Guarda en un String el valor anterior
        TextView mostrarUsuario = (TextView)findViewById(R.id.txtUsu);//Donde vamos a mostrar el usuario
        System.out.println("Crud usuarioLogeado: "+usuarioLogeado);

        //muestra si se ha logeado el usuario administrador o cualquier otro usuario
        if (usuarioLogeado.equals("1")) mostrarUsuario.setText("Usuario administrador");
        else mostrarUsuario.setText("Usuario "+usuarioLogeado);

        //Obtengo los registros del usuario logeado en 'LoginActivity'
        Bundle res = getIntent().getExtras();
        usuariosRegistrados = res.getString("registros");
        System.out.println("En 'Crud' los registros del usuario logeado son: " + usuariosRegistrados);


////////////////////////////CREACIÓN DE LA LISTA DESPLEGABLE/////////////////////
        //cargamos los elementos del spinner categoria el el ArrayList 'elementos'
        ArrayList<String>elementos = new ArrayList<>();
        elementos.add("Elige categoria");
        elementos.add("BAR");
        elementos.add("CAFETERIA");
        elementos.add("RESTAURANTE");
        ArrayAdapter adp = new ArrayAdapter(Crud.this, R.layout.spinner_layout, elementos);
        //Asignamos el adaptador al spinner
        sCategoria.setAdapter(adp);
        //agregamos los listener para poder identificar cuando he pulsado sobre uno de los elmentos
        sCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //identifica que elemento de la lista desplegable hemos pulsado
                elemento = (String) sCategoria.getAdapter().getItem(position);
                //inserta en el editText 'tipo' el código que corresponde al elemento seleccionado
                if (elemento.equals("Elige categoria")) tipo.setText("");
                if (elemento.equals("BAR")) tipo.setText("1");
                if (elemento.equals("CAFETERIA")) tipo.setText("2");
                if (elemento.equals("RESTAURANTE")) tipo.setText("3");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
////////////////////////////FIN DE CREACIÓN DE LA LISTA DESPLEGABLE/////////////////////

        refrescarRegistros();//Carga solo los registros que pertenece al usuario logeado

////////////////////////////FIN DE CARGAR SOLO LOS REGISTROS DE UN DETERMINADO USUARIO/////////////////////

        //Para menu contextual
        boton_administrar = (Button) findViewById(R.id.administrar);
        //Resgistra el 'boton_administar' para que tenga un menu contextual
        //Mediante una pulsación larga sobre el botón aparecera el menu contextual
        registerForContextMenu(boton_administrar);
        //Conexión con el hosting
        url = "http://" + Config.URL_PHP;// hosting en 000webhost
        // acceder al BOTÓN nuevo
        nuevo = (Button) findViewById(R.id.nuevo);
        //Define la acción del botón Nuevo
        nuevo.setOnClickListener(new View.OnClickListener() {
            //pone en blanco todos los EditText
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });
        // crea la lista de espectaculos
        listaUbicaciones = new ArrayList<Ubicaciones>();
        // listaUbicaciones = ma.listaUbicaciones;

        // acceder al botón más => El cual al pulsarlo avanza un registro
        mas=(ImageButton)findViewById(R.id.mas);
        // Definir la acción del botón 'menos' => Se mueve por nuestro ArrayList mostrando el nombre siguiente
        mas.setOnClickListener(new View.OnClickListener() {

            // la lista de alumnos va desde la posición 0 hasta el tamaño-1 => size()-1

            @Override
            public void onClick(View v) {
                // Comprobar si la lista de alumnos no está vacía
                if (!listaUbicaciones.isEmpty()) {
                    if (posicion >= listaUbicaciones.size() - 1) {
                        // se ha alcanzando o superado el final de lista
                        // posición debe valer el final de la lista por si se ha superado el valor
                        posicion = listaUbicaciones.size() - 1;
                        // se ha alcanzando el final de lalista
                        Config.tostada(getApplicationContext(), "Último de la lista");
                    } else
                        // no se ha alcanzando o superado el final de lista => avanzar
                        posicion++;

                    // visualizar el nombre de la lista situado en posición
                    mostrarTodo();
                }
            }

        });
        // acceder al botón MENOS => El cual al pulsarlo retrocede un registro
        menos = (ImageButton) findViewById(R.id.menos);
        // Definir la acción del botón 'menos' => Se mueve por el ArrayList mostrando el espectaculo anterior
        menos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Comprobar si la lista de espectaculos no está vacía
                if (!listaUbicaciones.isEmpty()) {
                    System.out.println("----------En 'menos' posicion es: "+posicion);
                    if (posicion > 0) {
                        // no se ha alcanzando el principio de lista => retroceder
                        posicion--;

                        // mostrar el espectaculo de la lista situado en posición
                        mostrarTodo();
                    } else {
                        // se ha alcanzando el principio de la lista
                        Config.tostada(getApplicationContext(), "Primero de la lista");
                    }
                }
            }
        });
    }//////////////////////////////////// FIN DE 'onCreate' ////////////////////////////////////////

    /**
     * Hilo que carga los registros de un determinado usuario
     */
    public void refrescarRegistros() {
        ////////////////////////////CARGA SOLO LOS REGISTROS DE UN DETERMINADO USUARIO/////////////////////
        Thread validar_usuario = new Thread() {
            @Override
            public void run() {
                //Compruebo si el usuario existe o no
                ValidarUsuario_tabla_ubicacion validarUsu = new ValidarUsuario_tabla_ubicacion();
                usuario_validado = validarUsu.enviarPost(usuarioLogeado);
                System.out.println("Registros de un usuario obtenidos en 'Crud' a través de la clase 'ValidarUsuario_tabla_ubicacion': " + usuario_validado);
                //metodo que me deja utilizar la interfaz grafica desde dentro del hilo
                runOnUiThread(new Runnable() {
                    //metodo que me cuenta los elementos que estoy consumiendo del webService
                    @Override
                    public void run() {
                        registrosPorUsuario = usuario_validado;
                        System.out.println("registrosPorUsuario 000000: "+registrosPorUsuario);
                    }
                });
            }
        };
        validar_usuario.start();//iniciamos el hilo que comprueba si el usuario existe o no
    }
    /**
     * Metodo para poner los EditText vacios, es estatico porque lo utilizo
     * en otras clases.
     */
    public static void limpiar(){
        accion.setText("");
        id_ubicacion.setText("");
        nombre.setText("");
        direccion.setText("");
        telefono.setText("");
        ubicacion.setText("");
        web.setText("");
        tipo.setText("");
        sCategoria.setSelection(0);
    }
    /**
     * Actualiza el mapa al pulsar el botón 'Ir hacia atrás' de la aplicación
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * Método que recorre listaUbicaciones
     */
    public void recorrerListaUbicaciones (){
        // Comprobar si la lista de alumnos no está vacía
        if (!listaUbicaciones.isEmpty()) {
            System.out.println("El tamaño de listaUbicaciones es: "+listaUbicaciones.size());
            //Recorro listaUbicaciones
            for (u = 0; u < listaUbicaciones.size(); u++) {
                //Guardo en el objeto Ubicacion un elemento de la listaUbicaciones
                ubic = listaUbicaciones.get(u);
                //            System.out.println("El nombre es: " + ubic.getNombre());
                mostrarTodo();
            }
        }else  Toast.makeText(this, "La lista 'listaUbicaciones' esta vacia", Toast.LENGTH_SHORT).show();
    }
    /**
     * Método que mostrar un establecimiento almacenado en el ArrayList listaUbicaciones
     */
    public void mostrarTodo() {
        // recoge en ubicaciones la información de la ubicacion situada en posicion de listaUbicaciones
        System.out.println("AAAAAAAAAA posicion: "+posicion);
        ubic = listaUbicaciones.get(posicion);
        // poner la información del espectaculo en los EditText
        id_ubicacion.setText(String.valueOf(ubic.getId()));
        nombre.setText(ubic.getNombre());
        direccion.setText(""+ubic.getDireccion());
        telefono.setText(""+ubic.getTelefono());
        ubicacion.setText(""+ubic.getUbicacion());
        web.setText(""+ubic.getWeb());
        tipo.setText(""+ubic.getTipo());
        //Muestra en la lista deplegable 'sCategoria' la categoria (BAR, CAFETERIA, RESTAURANTE)
        //a la que corresponde el id_ubicacion buscado
        tipoUbicacion = Integer.parseInt(ubic.getTipo());
        saberCategoria(tipoUbicacion);
    }
    /**
     * Método que muestra la ubicacion almacenada en el ArrayList listaUbicaciones en la posición pasada
     * como parámetro
     * @param posicion: posición de del espectaculo a mostrar
     */
    public void mostrarUnaUbicacion(int posicion) {
        System.out.println("listaUbicaciones esta vacia: "+listaUbicaciones.isEmpty());
//        System.out.println("id_ubicaciones en 'mostrarUnaUbicacion' es: "+listaUbicaciones.get(0).getId());
   //     System.out.println("id_ubicaciones en mostrarUnaUbicacion: "+listaUbicaciones.get(posicion).getId());
        // recoge en ubicaciones la información de la ubicacion situada en posicion de listaUbicaciones
        System.out.println("BBBBBBBBBB posicion: "+posicion);
        ubic = listaUbicaciones.get(posicion);
        // poner la información del espectaculo en los EditText
        id_ubicacion.setText(String.valueOf(ubic.getId()));
        nombre.setText(ubic.getNombre());
        direccion.setText(""+ubic.getDireccion());
        telefono.setText(""+ubic.getTelefono());
        ubicacion.setText(""+ubic.getUbicacion());
        web.setText(""+ubic.getWeb());
        tipo.setText(""+ubic.getTipo());
        //Muestra en la lista deplegable 'sCategoria' la categoria (BAR, CAFETERIA, RESTAURANTE)
        //a la que corresponde el id_ubicacion buscado
        tipoUbicacion = Integer.parseInt(ubic.getTipo());
        saberCategoria(tipoUbicacion);
    }
    /**
     * Método que averigua y muestra a que categoria corresponde la ubicación (BAR, CAFETERIA,
     * RESTAURANTE) que se muestra por pantalla según el tipo (1, 2, 3)
     * @param tipoUbicacion
     */
    public void saberCategoria(int tipoUbicacion){
        if (tipoUbicacion == 1) sCategoria.setSelection(tipoUbicacion);
        if (tipoUbicacion == 2) sCategoria.setSelection(tipoUbicacion);
        if (tipoUbicacion == 3) sCategoria.setSelection(tipoUbicacion);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //llama al método heredado de la clase padre
        super.onCreateContextMenu(menu, v, menuInfo);
//        if(v.getId()==R.id.administrar)
        //Infla el menu contextual al dar una pulsación larga al botón 'ADMINISTRAR'
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }
    /**
     * Método que controla cual de las opciones del menu contextual hemos pulsado
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        id = id_ubicacion.getText().toString();
        no = nombre.getText().toString();
        di = direccion.getText().toString();
        tel = telefono.getText().toString();
        ub = ubicacion.getText().toString();
        we = web.getText().toString();
        ti = tipo.getText().toString();
        switch (item.getItemId()) {
///////////////////////////////////////// MOSTRAR TODAS ///////////////////////////////////////////
             case R.id.menu_mostrar://Muestra todas las ubicaciones
                 accion.setText("MUESTRA TODO");
                 posicion = 0;//Para que siempre empiece mostrando el primer registro
                 listaUbicaciones.clear();
                 // Variable para controlar si 'WS_Cargar_Ubicaciones' se ejecuta por 1ª vez, caso
                 // en el que 'segundaCarga' = false y esto hace que carge las ubicaciones en el mapa.
                 // En el caso de que no sea la 1ª vez que se ejecuta 'WS_Cargar_Ubicaciones'
                 // 'segundaCarga' = true, lo cual indica que ejecute 'Crud.java'
                 segundaCarga = true;
                 System.out.println("listaUbicaiconessss antes de cargar todo esta vacia: "+listaUbicaciones.isEmpty());
                 System.out.println("//////////---------------registros: "+ registrosPorUsuario);
                 System.out.println("url: "+url);
                 System.out.println("usuarioLogeado: "+usuarioLogeado);
                 System.out.println("verUbicacion: "+ segundaCarga);

                 //1. carga listaUbicaciones con los datos de la BD mediante la petición POST
                 //2. mostrar el primero de ellos en pantalla
                 new WS_Cargar_Ubicaciones(Crud.this, listaUbicaciones, url, registrosPorUsuario, usuarioLogeado,
                      segundaCarga).execute();
                 break;
///////////////////////////////////////// BUSCAR POR ID ////////////////////////////////////////////
            case R.id.menu_buscar_una: //Busca y muestra por id_ubicacion
                accion.setText("BUSCA POR ID");
                posicion=0;//cuando busca un id, solo guardará un registro, por lo tanto ocupara la posicion 0
                listaUbicaciones.clear();
                Ubicaciones ubic = new Ubicaciones();
                if (!id_ubicacion.getText().toString().equals("")) {//Si EditText id_ubicación tiene datos
                    //Guarda en la variable 'ubicacion_buscada'el valor del editText 'id_ubicación',
                    // el cual es el que queremos buscar
                    String ubicacion_buscada = id_ubicacion.getText().toString();
                    System.out.println("ubicacion_buscada: " + ubicacion_buscada);
                    System.out.println("tamaño de listaUbicaciones: " + listaUbicaciones.size());
                    new WS_BuscarUnaUbicacion(Crud.this, listaUbicaciones, usuarioLogeado, ubicacion_buscada).execute();
                    System.out.println("-----------------------------posicion: " + posicion);
                }else Config.tostada(getApplicationContext(), "El campo ID debe tener datos");//Si no tiene datos me informa
                 break;

/////////////////////////////////////////// INSERTAR ///////////////////////////////////////////////
                case R.id.menu_insertar: //Inserta una ubicación
                accion.setText("INSERTA");
                    System.out.println("En insertar de Crud el tamaño de listaUbicaciones: " + listaUbicaciones.size());
            //    listaUbicaciones.clear();//para que al insertar un nuevo registro y pulse botón + o - no recorra el resto de registros que tiene el usuario
                //controla que no tenga ningún ID en el editText
                if (id.isEmpty()) {
                    //controla que alguno de estos campos nombre, direccion, ubicacion no tengan datos
                    if (no.isEmpty() || di.isEmpty() || ub.isEmpty() || ti.isEmpty()) {
                        Config.tostada(getApplicationContext(), "Nombre, Dirección, Ubicación y Tipo deben tener datos");
                    } else //En el caso de que nombre, direccion, ubicacion y tipo tengan datos
                        // 1. inserta los datos del nombre en la BD mediante petición POST
                        new WS_Insertar(Crud.this, url, id_ubicacion, nombre, direccion,
                                telefono, ubicacion, web, tipo, usuarioLogeado, webCorrecta).execute();
                } else Config.tostada(getApplicationContext(), "El ID no debe tener datos");// Si ID tiene algún dato

                refrescarRegistros();//Una vez insertado el nuevo registro refresca los registros que pertenecen al usuario logeado
                System.out.println("En insertar El tamaño de listaUbicaciones es: "+listaUbicaciones.size());
                break;

/////////////////////////////////////////// MODIFICAR //////////////////////////////////////////////
            case R.id.menu_modificar: //Actualiza
                accion.setText("MODIFICAR");
                System.out.println("NNNNNNNnombre: "+nombre.getText().toString().trim());
                System.out.println("direccion: "+direccion.getText().toString().trim());
                System.out.println("teléfono: "+telefono.getText().toString().trim());
                System.out.println("ubicacion: "+ubicacion.getText().toString().trim());
                System.out.println("tipo: "+tipo.getText().toString().trim());
                System.out.println("listaUbicaiconessss esta vacia: "+listaUbicaciones.isEmpty());
                System.out.println("En modificar El tamaño de listaUbicaciones es: "+listaUbicaciones.size());
                if (!id_ubicacion.getText().toString().trim().equalsIgnoreCase("")||
                        !nombre.getText().toString().trim().equalsIgnoreCase("")||
                        !direccion.getText().toString().trim().equalsIgnoreCase("")||
                        !telefono.getText().toString().trim().equalsIgnoreCase("")||
                        !ubicacion.getText().toString().trim().equalsIgnoreCase("")||
                        !web.getText().toString().trim().equalsIgnoreCase("")||
                        !tipo.getText().toString().trim().equalsIgnoreCase("")){
                    // intenta modificar los datos del nombre que se muestra en pantalla
                    new WS_Modificar(Crud.this, posicion, listaUbicaciones, url, nombre, direccion, telefono, ubicacion, web, tipo).execute();
                    // guardo en listaUbicaciones los cambios realizados
                    listaUbicaciones.get(posicion).setNombre(nombre.getText().toString().trim());
                    listaUbicaciones.get(posicion).setDireccion(direccion.getText().toString().trim());
                    listaUbicaciones.get(posicion).setTelefono(telefono.getText().toString().trim());
                    listaUbicaciones.get(posicion).setUbicacion(ubicacion.getText().toString().trim());
                    listaUbicaciones.get(posicion).setWeb(web.getText().toString().trim());
                    listaUbicaciones.get(posicion).setTipo(tipo.getText().toString().trim());
                    System.out.println("nOMBRE: "+listaUbicaciones.get(posicion).getNombre());
                }else Config.tostada(getApplicationContext(), "Debe mostrar el archivo a MODIFICAR");
                break;

//////////////////////////////////////////// ELIMINAR //////////////////////////////////////////////
            case R.id.menu_borrar: //Elimina una ubicación
                accion.setText("BORRA");
    //            listaUbicaciones.clear();
                String id_a_borrar = id_ubicacion.getText().toString().trim();
                System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiidUbic: "+id_a_borrar);
                System.out.println("usuario logeado " +usuarioLogeado);
                if (!id_a_borrar.equalsIgnoreCase("")){
                    //Busco en la BD del WebService el registro que quiero elminiar y lo cargo en 'listaUbicaciones'
                    //y si no pertenece al usuario logeado me informa a través de una tostada
                    new WS_BuscarUnaUbicacion(Crud.this, listaUbicaciones, usuarioLogeado, id_a_borrar).execute();

                    System.out.println("Borrar-->Ha pasaso por 'BuscarUnaUbicacion");

                    // Confirma si quieres borrar o no el registro
                    confirmDeleteUbicacion();
                    System.out.println("Ha pasado por confirmDeleteUbicacion");
 //                   listaUbicaciones.clear();//para que al borrar un nuevo registro y pulse botón + o - no recorra el resto de registros que tiene el usuario
                } else Config.tostada(getApplicationContext(), "Debe indicar el ID del archivo a BORRAR");
//                refrescarRegistros();//Una vez borrado el registro actualiza los registros que pertenecen al usuario logeado
            default:
                return super.onContextItemSelected(item);
        }
//        refrescarRegistros();
        return true;
    }
    /**
     * Método que muestra un cuadro de diálogo y pregunta al usuario si quiere borrar o no el
     * establecimiento que se muestra en pantalla. En caso de que confirmación 'Si' => lo borra de la BD
     */
    private void confirmDeleteUbicacion(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Definir el cuadro de diálogo
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("¿ Seguro que desea eliminar el establecimiento '"+nombre.getText().toString().trim()+"' ?");
        // Si el usuario pulsa el botón del "Si"
        alertDialogBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // borrar el establecimiento actual
                        new WS_Borrar(Crud.this, posicion, listaUbicaciones).execute();
                        System.out.println("Ha pasado por 'WS_Borrar'");
                        refrescarRegistros();//Una vez borrado el registro actualiza los registros que pertenecen al usuario logeado
                        System.out.println("Ha pasado por 'refrescarRegistros'");
                    }
                });
        // Si el usuario pulsa el botón del "NO" => no hace nada
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * Método que devuelve un objeto Addres a partir de una dirección en texto
     * @param d: dirección indicada (en modo texto)
     * @return un objeto Address
     */
    private Address convertirGeocode(String d) {
        System.out.println("direccion: "+d);
        // lista de direcciones obtenidas por Geocoder
        List<Address> listaDirecciones = null;
        // Construimos el objeto Geocoder: clase que transforma direcciones textuales
        // a coordenadas de Lat, Long y a la inversa
        Geocoder geocoder = new Geocoder(this);
        try {
            // obtenemos el array de direcciones por el nombre, indicándole que no guarde nada más que una
            listaDirecciones = geocoder.getFromLocationName(d, 1);
            if (listaDirecciones.isEmpty()) {//Si dirección no viable
                Toast.makeText(getBaseContext(), "               Dirección erronea \nImposible obtener esa coordenada", Toast.LENGTH_SHORT).show();
                //En el caso de que haya alguna coordenada en 'ubicacion' la borra
                ubicacion.setText("");
            }
        } catch (IOException e) {// si no se introduce ninguna dirección
            e.printStackTrace();
            Config.tostada(getApplicationContext(), "Escriba una dirección");
            //En el caso de que haya alguna coordenada en 'ubicacion' la borra
            ubicacion.setText("");
        }
        // devolvemos la dirección obtenida, es decir, el primer y único elemento del array
        if (listaDirecciones != null && listaDirecciones.size() > 0)
            return listaDirecciones.get(0);
        else
            return null;
    }
    /**
     * Al pulsar el bóton 'Extraer Ubicación' extrae de la dirección indicada la latitud - longitud
     * @param view
     */
    public void onClickGeocode(View view) {
        String escribeDirecion = direccion.getText().toString();
        if (!direccion.getText().toString().isEmpty()) {
            // asigna el objeto Address la información de la dirección indicada
            objetoDireccion = convertirGeocode(escribeDirecion);
            if (objetoDireccion != null) {
                //extraemos de toda la información que nos puede dar 'objetoDireccion' solo la latitud y
                //la logitud
                double lat = objetoDireccion.getLatitude();
                double lon = objetoDireccion.getLongitude();

/*                //Para limitar el nº de decimales
                DecimalFormat decimales = new DecimalFormat("#.000");//Indica el nº de decimales que va a mostrar
                System.out.println("antes de darles el formato");
                System.out.println("lat: "+lat);
                System.out.println("lon: "+lon);
                decimales.format(lat);//hace que la  latitud tenga hasta 7 decimales
                decimales.format(lon);//hace que la longitud tenga hasta 7 decimales
                System.out.println("despues de darles el formato");
                System.out.println("lat: "+decimales.format(lat));
                System.out.println("lon: "+decimales.format(lon));
 */
                //convertimos a String
                String latitud = Double.toString(lat);
                String longitud = Double.toString(lon);
                //Inserto en el editText ubicacion los dos valores
                ubicacion.setText(latitud + "," + longitud);
            }
        }else Config.tostada(getApplicationContext(), "Escriba una dirección");//Si en 'dirección' no hay datos
    }
    /**
     * Al pulsar el bóton 'Validar Web' comprueba mediante expresión regular, que la web introducida está bien formada
     * @param view
     */
    public void onClickValidarWeb(View view) {
        String we = web.getText().toString().trim();
        String url_valida =  "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
                "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$";
        Pattern url_pattern = Pattern.compile(url_valida);
        Matcher url_matcher = url_pattern.matcher(we);
        //Si hemos escrito en el EditText correspondiente a la web y si el patrón de coordenadas ES correcto
        if (!we.isEmpty() && (url_matcher.matches())) {
            Config.tostada(getApplicationContext(), "Patrón de web correcto");
            webCorrecta = true;
        }
        //Si hemos escrito en el EditText correspondiente a la web y si el patrón de coordenadas No ES correcto
        if (!we.isEmpty() && (!url_matcher.matches())) {
            Config.tostada(getApplicationContext(), "La web no es correcta");
            webCorrecta = false;//si la web no es correcta no inserta ninguna web en la BD del servidor
        }
        if (we.isEmpty())
            Config.tostada(getApplicationContext(), "Introduzca alguna web");
    }
}

