package com.pms.mapasgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Detecta si no hemos introducido usuario o contraseña
 * Detecta si el usuario o la contraseña no son correctos
 * Comprueba si existe o no el usuario que queremos logear
 * Permite visualizar contraseña
 * Permite iniciar el registro de un nuevo usuario
 */
public class LoginActivity extends AppCompatActivity {

    EditText txtUsuario, txtContraseña;
    Button btnIngresar, btnRegistrar;
    String registros_de_usuario;
    CheckBox checkbox;
    public List<Ubicaciones> listaUbicaciones;
    //url donde se encuentra la imagen que quiero visualizar en 'LoginActivity'
    private static final String URL_IMAGEN_LOGEO = "http://satisfactory-plugs.000webhostapp.com/proyecto/imagenes/login.png";
    //Declaro la variable para manipular la imagen
    private ImageView imagenLogeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtContraseña = (EditText) findViewById(R.id.txtContraseña);
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        checkbox = (CheckBox)findViewById(R.id.checkBox);

        //asocio la variable de 'imagenLogeo' a imageView declarado en el layout 'activity_login.xml'
        imagenLogeo = findViewById(R.id.imageView);
        //Llamo al método que me va a cargar la imagen a traves de la url donde se encuentra
        cargarImagenConPicasso();

        // crea la lista de espectaculos
        listaUbicaciones = new ArrayList<Ubicaciones>();

        //////////////// AL MARCAR EL CHECKBOX PERMITE VISUALIZAR LA CONTRASEÑA ////////////////////
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Objeto de la clase 'MostrarContrasena'
                MostrarContrasena mc = new MostrarContrasena();
                //ejecuta al método 'mostrarContrasena' de la clase 'MostrarContrasena'
                mc.mostrarContrasena(txtContraseña, isChecked);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        //Abre la Activity que me permitirá registrar un nuevo usuario
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegistrarUsuario.class);
                startActivity(i);
            }
        });

        final Existe_usuario existe_usuario = new Existe_usuario();//objeto de la clase 'Existe_usuario_en_tabla_usuarios'
        //Permite abrir la Activity 'Crud' si el usuario y la contraseña existen
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Controla que el usuario o la contraseña no este vacio
                if (txtUsuario.getText().toString().equals("")||txtContraseña.getText().toString().equals(""))
                    Config.tostada(getApplicationContext(),"Debe introducir usuario y contraseña");
                else {//En el caso de que usuario y contraseña tenga datos
                    // Crea el hilo al pulsar el boton 'Ingresar',
                    // el cual comprueba si el usuario existe o no
                    // junto con la contraseña en tabla_usuarios
                    Thread validar_usuario_contraseña = new Thread() {
                        @Override
                        public void run() { //envia los datos al webService y retorna los valores que devuelve el webService
                            ValidarUsuarioContraseña enviarUsuPass = new ValidarUsuarioContraseña();//Objeto de la clase 'EnviarUsuarioContraseña'
                            //llamo al método 'enviarPost' de la clase 'EnviarUsuarioContraseña', el cual me comprueba si el usuario y
                            //contraseña existe en tabla_usuarios y devuelve el registro
                            registros_de_usuario = enviarUsuPass.enviarPost(txtUsuario.getText().toString(), txtContraseña.getText().toString());
                            System.out.println("8/*/*/*/*/*/*/*/*/En LoginActivity rspta: " + registros_de_usuario);
                            //metodo que me deja utilizar la interfaz grafica desde dentro del hilo
                            runOnUiThread(new Runnable() {
                                //cuenta los elementos que estoy consumiendo del webService, es decir,
                                //me indica si ha encontrado(1) o no (0) el usuario y la contraseña buscado
                                @Override
                                public void run() {
                                    int r = existe_usuario.hay_registros(registros_de_usuario);//almacenaria el '1' o el '0'
                                    System.out.println("r: " + r);
                                    if (r > 0) { //Si es '1' es que ha encontrado el usuario y la contraseña --> abre actividad 'Crud'
                                        Intent i = new Intent(getApplicationContext(), Crud.class);
                                        //envia usuario y contraseña y sus registros a la activity 'Crud'
                                        i.putExtra("usuario", txtUsuario.getText().toString());
                           //             i.putExtra("contraseña", txtContraseña.getText().toString());
                                        i.putExtra("registros", registros_de_usuario);
                                        Config.tostada(getApplicationContext(), "Mantenga pulsado el botón\n            'ADMINISTRAR'");
                                        startActivity(i);
                                    } else {//Si es '0' es que no hay registros, es decir, que ese usuario y esa contraseña juntos no existen
                                        // lo cual puede suceder por dos motivos:
                                        // 1.- que el usuario no exista
                                        // 2.- que el usuario o la contraseña no sean correctos
                                        Thread validar_usuario = new Thread() {
                                            @Override
                                            public void run() {
                                                //Compruebo si el usuario existe o no
                                                ValidarUsuario_tabla_usuarios validarUsu = new ValidarUsuario_tabla_usuarios();
                                                registros_de_usuario = validarUsu.enviarPost(txtUsuario.getText().toString());
                                                System.out.println("Registros de un usuario obtenidos en 'LoginActivity' a través de la clase 'ValidarUsuario_tabla_usuarios': " + registros_de_usuario);
                                                //metodo que me deja utilizar la interfaz grafica desde dentro del hilo
                                                runOnUiThread(new Runnable() {
                                                    //metodo que me cuenta los elementos que estoy consumiendo del webService
                                                    @Override
                                                    public void run() {
                                                        //almacenaria el '1' o el '0'
                                                        int r = existe_usuario.hay_registros(registros_de_usuario);
                                                        System.out.println("r: " + r);
                                                        if (r == 0) { //Si es '0' es que no hay registros por lo tanto el usuario no existe
                                                            Config.tostada(getApplicationContext(), "Usuario no existe\n\n  ¡¡REGISTRATE!!");
                                                        } else {//Si es '1' es que hay registros, lo que indica que usuario existe,
                                                                //por lo tanto el usuario o contraseña no son correctos
                                                            Config.tostada(getApplicationContext(), "Usuario o Contraseña incorrectos");
                                                        }
                                                    }
                                                });
                                            }
                                        };
                                        validar_usuario.start();//iniciamos el hilo que comprueba si el usuario existe o no, o si el usuario o la contraseña son correctos o no
                                    }
                                }
                            });
                        }
                    };
                    validar_usuario_contraseña.start();//iniciamos el hilo que valida el usuario y la contraseña
                }
            }
        });
    }
    private void cargarImagenConPicasso(){
        Picasso.get().load(URL_IMAGEN_LOGEO).into(imagenLogeo);
    }
}


