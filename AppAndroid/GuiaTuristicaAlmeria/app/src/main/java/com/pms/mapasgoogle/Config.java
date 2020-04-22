package com.pms.mapasgoogle;

import android.content.Context;
import android.widget.Toast;

public class Config {

    // dirección IP o URL del servidor
    public final static String URL_SERVIDOR = "satisfactory-plugs.000webhostapp.com"; // hosting en 000webhost

    // URL del directorio de los scripts php del servidor
    public final static String URL_PHP = URL_SERVIDOR + "/proyecto/";

    //script que valida un usario a tráves del usuario y contraseña
    public final static String PHP_VALIDAR = "validar.php";

    public final static String PHP_VALIDAR_USUARIO = "validar_usuario.php";

    public final static String PHP_VALIDAR_CONTRASEÑA   = "validar_contraseña.php";

    public final static String PHP_INSERTAR_USUARIO   = "insertar_usuario.php";

    public final static String PHP_INSERTAR_FOTO   = "insertar_tabla_fotos.php";

    public final static String OBTENER_FOTOS_URL   = "obtenerFotos.php";

    //script que valida un usario a tráves del usuario y contraseña
    public final static String PHP_VALIDAR_TABLA_USUARIO = "validar_tabla_usuario.php";

    // script de selección de todos los registros
    public final static String PHP_MOSTRAR_TODO = "mostrarUbicaciones.php";

    //script que busca una sola ubicación por id
    public final static String PHP_BUSCAR_UNA_UBICACION = "buscarUnaUbicacion.php";

    public final static String PHP_BUSCAR_REGISTROS_USUARIO = "buscarRegistrosDeUnUsuario.php";

    public final static String PHP_BUSCAR_UN_USUARIO = "buscarUnUsuario.php";

    // script de inserción de un registro
    public final static String PHP_INSERTAR = "insertar.php";

    // script de modificación de un registro
    public final static String PHP_MODIFICAR = "modificar.php";

    //script que busca una sola ubicación por id
    public final static String PHP_BORRAR= "borrar.php";

    public static void tostada(Context context, String mensaje) {
        Toast toast1 = Toast.makeText(context, mensaje, Toast.LENGTH_LONG);
        toast1.show();
    }
}

