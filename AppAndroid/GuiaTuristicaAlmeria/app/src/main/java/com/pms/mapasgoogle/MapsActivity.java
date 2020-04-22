package com.pms.mapasgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    public int x, y, a, m, k, u;
    public int mostrar = 0;//para mostrar todas las ubicaciones o según su tipo
    public String url;
    private boolean iguales = false;
    //declaraciçón de marcadores
    private Marker mBares;
    private Marker mCafeterias;
    private Marker mRestaurantes;

    private String nombreArray;

    // Declaración de un ArrayList de "Marker".
    ArrayList<Marker> arrayBares = new ArrayList<Marker>();
    ArrayList<Marker> arrayCafeterias = new ArrayList<Marker>();
    ArrayList<Marker> arrayRestaurantes = new ArrayList<Marker>();

    public List<Ubicaciones> listaUbicaciones; // Lista de ubicaciones obtenidos de la BD
    private Ubicaciones ubic; // objeto Ubicaciones
    private LayoutInflater inflater;

    // atributos
    public EditText nombre, direccion, ubicacion, web, tipo, cod_usuario, pas_usuario;
    //booleano que si es 'false' indica que 'WS_Cargar_Ubicaciones' se esta iniciando por primera vez
    public boolean verUbicacion = false;
    public String registros; //// Son variables trampa que en realidad solo tendra contenido a partir de logearse el usuario
    public String usuarioLogeado = "";
    private GoogleMap mMap;
    private CameraUpdate camUpd;

    // atributos
    public EditText administrar;

    public Crud c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // crea la lista de espectaculos
        listaUbicaciones = new ArrayList<Ubicaciones>();

        //Visualiza en consola la url de Conexión al hosting
        url = "http://" + Config.URL_PHP;// hosting en 000webhost
        System.out.println("******La url de conexión a la Base de Datos es: "+url+"******");

    }
    /**
     * Muestra el mapa de Almería con un zoom de 12f e inicia un servicio web que se encarga de
     * extraer los datos de la base de datos y usarlos para visualizar todas las ubicaciones en
     * el mapa
     * @param map
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng almeria = new LatLng(36.8417741, -2.4589911);
        //Objeto CameraUpdate con coordenas correspondientes a Almería y zoom (12f) como parámetros
        camUpd = CameraUpdateFactory.newLatLngZoom(almeria, 13f);
        //Posiciona la cámara del mapa de forma animada sobre los parametros antes indicados*/
        mMap.animateCamera(camUpd);

        //1. carga listaUbicaciones con los datos de la BD mediante la petición POST
        //2. mostrar todas las ubic en el mapa
        new WS_Cargar_Ubicaciones(MapsActivity.this, listaUbicaciones, url, registros, usuarioLogeado, //'registros' y 'usuarioLogeado', solo tendra contenido cuando se haya logeado el usuario
                 verUbicacion).execute();
        if (!listaUbicaciones.isEmpty()) {
            System.out.println("88888888888888888El tamaño de listaUbicaciones es: "+listaUbicaciones.size());
        }else {
            System.out.println("8888888888888888888888 listaUbicaciones esta vacia");
        }
    }
    /**
     * Método que recorre listaUbicaciones
     */
    public void recorrerListaUbicaciones (){
        System.out.println("El tamaño de listaUbicaciones es: "+listaUbicaciones.size());
        // Comprobar si la lista de alumnos no está vacía
        if (!listaUbicaciones.isEmpty()) {
            System.out.println("El tamaño de listaUbicaciones es: "+listaUbicaciones.size());
    //        c.listaUbicaciones = listaUbicaciones;
            //Recorro listaUbicaciones
            for (u = 0; u < listaUbicaciones.size(); u++) {
                extraeLatidudLongitud(u);
               // nom = extraeNombre(u);
            }
        }else  Toast.makeText(this, "La lista 'listaUbicaciones' esta vacia", Toast.LENGTH_SHORT).show();
    }
    /**
     * Método que extrae de listaUbicaciones el String que representa las coordenadas de la ubicación, comprueba
     * que se ajusta al patrón de coordenadas de geolocalización correcto y lo divide en latitud y longitud
     */
    public void extraeLatidudLongitud(int u){
        y = 0;
        //Guardo en el objeto Ubicacion un elemento de la listaUbicaciones
        ubic = listaUbicaciones.get(u);
        //Obtengo el String del campo donde se encuentran las coordenadas del lugar
        //para dividirlo en latitud y longitud
        String d = ubic.getUbicacion();
        //Llama a la clase 'PatronUbicacion' para comprobar que las coordenadas son correctas según un patrón
        Matcher mat = PatronUbicacion.coordenadasCorrectas(d);
        //si el patrón de coordenadas es correcto
        if (mat.matches()) {
            // el patrón tiene la forma correcta (por ejemplo, "36.840326,-2.459256"), empezamos
            // por romper la cadena por la ','
            String[] coordenadas = d.split(",");
            //Llamada al método que va a tipoDeUbicacionAMostrar el mapa con los diferentes iconos según el
            //tipo de ubicación(bar, cafeteria o restaurante)
            tipoDeUbicacionAMostrar(coordenadas, u, y, mostrar);
        }
    }// fin extraeLatidudLongitud()
    /**
     * Método que comprueba si se van a visualizar todas las ubicaciones o solo las de un tipo
     * determinado (Bares, Museos, Monumentos)
     * @param coordenadas
     * @param u
     * @param y
     * @param mostrar
     */
    public void tipoDeUbicacionAMostrar(String [] coordenadas, int u, int y, int mostrar) {
        int tipo = Integer.parseInt(ubic.getTipo());
        // convertimos cada parte a Double indicándole que sólo se quede con una
        double latitud = Double.parseDouble(coordenadas[0]);
        double longitud = Double.parseDouble(coordenadas[1]);
        final LatLng posicion = new LatLng(latitud, longitud);
        //La primera vez que pasa por aquí 'mostrar = 0', y marca todas las ubicaciones en el mapa
        if (mostrar == 0) {
            MarcaConIcono(tipo, u, y, posicion);
        }
        //Marca las ubic de un solo tipo (bar, cafeteria o restaurante)
        if ((tipo==1||tipo==2||tipo==3)&& mostrar == tipo) {
            MarcaConIcono(tipo, u, y, posicion);
        }
    }
    /**
     * Método que marca el mapa con iconos según el tipo de ubicación de que se trate
     *(bar, cafeteria o restaurante)
     * @param tipo
     * @param posicion
     */
    public void MarcaConIcono (int tipo, int x, int y, LatLng posicion) {
        String mensaje = "Pulsa ventana para ver información";
        switch (tipo) {
            case 1:
                    //Añade marcador 'bares' al mapa
                    mBares=mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.ic_bares))
                            .anchor(0.0f, 1.0f)
                            .position(posicion)
                            .title(ubic.getNombre())
                            .snippet(mensaje)
                    );
                arrayBares.add(mBares);//Añade al array de bares el marcador mBares
                mBares.setTag(0);
                url = ubic.getWeb();
                break;
            case 2:
                //Añade marcador 'museos' al mapa
                mRestaurantes =mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.ic_cafeterias))
                        .anchor(0.0f, 1.0f)
                        .position(posicion)
                        .title(ubic.getNombre())
                        .snippet(mensaje)
                );
                arrayRestaurantes.add(mRestaurantes);
                mRestaurantes.setTag(0);
                break;
            case 3:
                //Añade marcador 'monumentos' al mapa
                mCafeterias =mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.ic_restaurantes))
                        .anchor(1.0f, 1.0f)
                        .position(posicion)
                        .title(ubic.getNombre())
                        .snippet(mensaje)
                );
                arrayCafeterias.add(mCafeterias);
                mCafeterias.setTag(0);
                break;
        }
        mMap.setOnInfoWindowClickListener(this);
    //    mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * Al pulsar sobre el infoWindow abre la página web del marcador clicado
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Abriendo información de "+ marker.getTitle(),
                Toast.LENGTH_SHORT).show();
        comparaNombre(marker);
    }
    /** Comprueba que el nombre del marcador en el que se ha hecho 'click' es igual a alguno de los
     nombres que contienen los array --> 'arrayBares' 'arrayCafeterias' y 'arrayRestaurantes'*/
    public boolean comparaNombre(Marker marker) {  //onMarkerClick
        for ( a = 0; a <= arrayBares.size()-1; a++) {
            //Comprueba que el nombre del marcador en el que se ha hecho 'click' es igual
            //a alguno de los nombres del 'arrayBares'.
            if (marker.equals(arrayBares.get(a))) {
                nombreArray = arrayBares.get(a).getTitle();
                identificaMarcador(nombreArray);//Recorre la matriz y compara el nombre de la ubicación con el nombre del marcador
                a = arrayBares.size();
            }
        }
        for (a = 0; a <= arrayCafeterias.size()-1; a++) {
            if (marker.equals(arrayCafeterias.get(a))) {
                nombreArray = arrayCafeterias.get(a).getTitle();
                identificaMarcador(nombreArray);//Recorre la matriz y compara el nombre de la ubicación con el nombre del marcador
                a = arrayCafeterias.size();
                System.out.println("========================== arrayCafeterias: "+a);
            }
        }
        for (a = 0; a <= arrayRestaurantes.size()-1; a++) {
            if (marker.equals(arrayRestaurantes.get(a))) {
                nombreArray = arrayRestaurantes.get(a).getTitle();
                identificaMarcador(nombreArray);//Recorre listaUbicaciones comparando el nombre de la ubicación con el nombre del marcador
                a = arrayRestaurantes.size();
            }
        }
        // Devuelve falso para indicar que no hemos consumido el evento y esperamos que se produzca
        // el comportamiento predeterminado (que la cámara se mueva de manera que el marcador está
        // centrado y para que se abra la ventana de información del marcador, si tiene uno).
        return false;
    }

    /**
     * Recorre listaUbicaciones comprobando si el campo 'nombre' coincide con el 'nombre' de los
     * registros del array correspondiente (arrayBares, arrayRestaurantes, arrayCafeterias) y en
     * el caso de que coincida, lanza un intent visualizando la activity con información del
     * establecimiento clicado
     * @param nombreArray
     */
    private void identificaMarcador (String nombreArray) {
        //Recorre listaUbicaciones y compara el nombre de la ubicación con el nombre del marcador
        for (m = 0; m <= listaUbicaciones.size(); m++) {
            ubic=listaUbicaciones.get(m);//guarda en 'ubic' el objeto que contiene la posición 'm' de listaUbicaciones
            String nombreEnLista = ubic.getNombre();
            //Si coinciden los nombres lanza un intent visualizando la página web
            if (nombreArray.equals(nombreEnLista)) {
                boolean enviarDatos = true;
                //Una vez que tengo el valor de la web correspondiente a la ubicación lo paso a la
                //activity 'Datos_establecimiento' que es donde se puede solicitar este dato
                Intent intentDatosEstablecimiento = new Intent(this,Datos_establecimiento.class);
                intentDatosEstablecimiento.putExtra("envia_datos", enviarDatos);
                intentDatosEstablecimiento.putExtra("web", ubic.getWeb());//envia la web a la activity 'Datos_establecimiento'
                intentDatosEstablecimiento.putExtra("telefono", ubic.getTelefono());//envia el teléfono a la activity 'Datos_establecimiento'
                intentDatosEstablecimiento.putExtra("nombre", ubic.getNombre());//envia el nombre a la activity 'Datos_establecimiento'
                intentDatosEstablecimiento.putExtra("id_ubicacion", ubic.getId());//envia id_ubicacion a la activity 'Datos_establecimiento'
                startActivity(intentDatosEstablecimiento);

                m = listaUbicaciones.size();//Se le da este valor para que no siga buscando
            }
        }
    }
    /**
     * Activa el menú
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    /**
     * Responde a la pulsación del item del menú
     * @param item opción del menú
     * @return true para opción seleccionada
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_administrar:
                Toast.makeText(this, "Has clicado en 'Administrar categorias'", Toast.LENGTH_SHORT).show();
                logearse(item);
                break;
            case R.id.menu_bar:
                mMap.clear();
                arrayBares.clear();
                bar();
                break;
            case R.id.menu_cafeteria:
                mMap.clear();
                arrayCafeterias.clear();
                cafeteria();
                break;
            case R.id.menu_restaurante:
                mMap.clear();
                arrayRestaurantes.clear();
                restaurante();
                break;
            case R.id.menu_todas:
                mMap.clear();
                arrayBares.clear();
                arrayCafeterias.clear();
                arrayRestaurantes.clear();
                todas();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Método que abre la actividad para que el usuario se logee
     */
    public void logearse(MenuItem item) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        Config.tostada(getApplicationContext(),"Introduzca usuario y contraseña");
    }
    /**
     * Método bar
     * Le da el valor '1' a la variable 'mostrar' para comprobar posteriormente si conincide con el
     * campo 'Tipo = 1' de la base de datos a través del método 'ubic' y así mostrar solo los
     * Bares'
     */
    private void bar() {
        mostrar = 1;
        recorrerListaUbicaciones();
        Config.tostada(getApplicationContext(),"BARES DE TAPAS");
    }
    /**
     * Método cafeteria
     * Le da el valor '3' a la variable 'mostrar' para comprobar posteriormente si conincide con el
     * campo 'Tipo = 3' de la base de datos a través del método 'ubic' y así mostrar solo los
     * Monumentos'
     */
    private void cafeteria() {
        mostrar = 2;
        recorrerListaUbicaciones();
        Config.tostada(getApplicationContext(),"CAFETERIAS");
    }
    /**
     * Método restaurante
     * Le da el valor '2' a la variable 'mostrar' para comprobar posteriormente si conincide con el
     * campo 'Tipo = 2' de la base de datos a través del método 'ubic' y así mostrar solo los
     * Museos'
     */
    private void restaurante() {
        mostrar = 3;
        recorrerListaUbicaciones();
        Config.tostada(getApplicationContext(),"RESTAURANTES");
    }
    /**
     * Método todas
     * Le da el valor '0' a la variable 'mostrar' para mostrar todas las ubic registradas en
     * la base de datos.
     */
    private void todas() {
        mostrar = 0;
        recorrerListaUbicaciones();
        Config.tostada(getApplicationContext(),"MOSTRANDO TODAS LAS CATEGORIAS");
    }

//****************************************** FIN - MENU ********************************************
}