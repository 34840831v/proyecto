package com.pms.mapasgoogle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarUsuario extends AppCompatActivity {
    EditText txtUsuReg;
    TextView tvUsuario_validado;
    Button btnValidarUsuario;
    String usuario_validado;
    Activity context;
    ProgressDialog loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_usuario_layout);

        txtUsuReg = (EditText) findViewById(R.id.txtUserRegistro);
        btnValidarUsuario = (Button) findViewById(R.id.btnUsuarioValidado);

        final Existe_usuario existe_usuario_en_tabla_usuarios = new Existe_usuario();

        btnValidarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Controla que el usuario o la contraseña no este vacio
                if (txtUsuReg.getText().toString().equals(""))
                    Config.tostada(getApplicationContext(),"Debe introducir usuario");
                else {
     //////////////////VENTANA PROGRESDIALOG MIENTRAS BUSCA SI EXISTE USUARIO //////////////////////////
                    boolean cargar=true;//controla que
                    if (cargar) {
                        loading = new ProgressDialog(RegistrarUsuario.this);
                        loading.setTitle("Buscando usuario");
                        loading.setMessage("Comprobando existencia de usuario");
                        loading.setMax(100);
                        loading.setCancelable(false);
                        loading.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loading.dismiss();
                            }
                        });
                    /*    new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (loading.getProgress() <= loading.getMax()) {
                                        if (loading.getProgress() == loading.getMax()) {
                                            loading.dismiss();
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }).start();*/
                        loading.show();
                    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
                    Thread validar_usuario = new Thread() {
                        @Override
                        public void run() {
                            //Compruebo si el usuario existe o no
                            ValidarUsuario_tabla_usuarios validarUsu = new ValidarUsuario_tabla_usuarios();
                            usuario_validado = validarUsu.enviarPost(txtUsuReg.getText().toString());
                            System.out.println("Usuario obtenido a través de la clase 'ValidarUsuario': " + usuario_validado);
                            //metodo que me deja utilizar la interfaz grafica desde dentro del hilo
                            runOnUiThread(new Runnable() {
                                //metodo que me cuenta los elementos que estoy consumiendo del webService
                                @Override
                                public void run() {
                                    //Comprueba si hay registros o no y almacenaria el '1' o el '0'
                                    int r = existe_usuario_en_tabla_usuarios.hay_registros(usuario_validado);
                                    System.out.println("r: " + r);
                                    if (r > 0) {//Si es '1' es que hay registros, lo que indica que usuario existe
                                        Config.tostada(getApplicationContext(), "Usuario "+txtUsuReg.getText().toString()+ " ya existe");
                                        loading.dismiss();//Como el usuario existe detiene el ProgressDialog de busqueda
                                    } else {//Si es '0' es que no hay registros por lo tanto el usuario no existe
                                        loading.dismiss();//Detiene el ProgressDialog de busqueda
                                        Intent i = new Intent(getApplicationContext(), RegistrarContraseña.class);
                                        //envia usuario y contraseña a la activity 'Crud'
                                        i.putExtra("usuario", txtUsuReg.getText().toString());
                                        i.putExtra("registros", usuario_validado);
                                        startActivity(i);
                                        Config.tostada(getApplicationContext(), "Introduzca ahora contraseña");
                                    }
                                }
                            });
                        }
                    };
                    validar_usuario.start();//iniciamos el hilo que comprueba si el usuario existe o no
                }
            }
        });
    }
}
