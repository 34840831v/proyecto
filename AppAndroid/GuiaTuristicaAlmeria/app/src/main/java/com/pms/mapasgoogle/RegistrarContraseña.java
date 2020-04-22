package com.pms.mapasgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarContraseña extends AppCompatActivity {

    String usuario_a_registrar, URL_PHP;
    EditText txtPassRegistro;
    Button btnRegister;
    CheckBox checkbox;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_password_layout);

        txtPassRegistro = (EditText)findViewById(R.id.txtPassRegistro);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        checkbox = (CheckBox)findViewById(R.id.checkBox);

        ///////////////////////////// RECOGE EL USUARIO Y CONTRASEÑA LOGEADOS //////////////////////////////////
        //Obtengo el usuario logeado en 'LoginActivity' para que muestre en la activity 'Crud' el
        //usuario que está administrando ubicaciones
        Bundle usuario=getIntent().getExtras();//Extrae el usuario logeado en 'RegistrarUsuario.java'
        usuario_a_registrar = usuario.getString("usuario");//Guarda en un String el valor anterior
        TextView mostrarUsuario = (TextView)findViewById(R.id.tvUsuario);//Donde vamos a mostrar el usuario
        mostrarUsuario.setText("Usuario\n "+ usuario_a_registrar);
        System.out.println("'RegistrarContraseña' usuarioLogeado: "+ usuario_a_registrar);

        ////////////////////// PERMITE VISUALIZAR LA CONTRASEÑA ///////////////////////
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Objeto de la clase 'MostrarContrasena'
                MostrarContrasena mc = new MostrarContrasena();
                //ejecuta al método 'mostrarContrasena' de la clase 'MostrarContrasena'
                mc.mostrarContrasena(txtPassRegistro, isChecked);
            }
        });
        //////////////////////////////////////////////////////////////////////////////

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtPassRegistro.getText().toString().equals("")) //Controla que el usuario o la contraseña no este vacio
                    Config.tostada(getApplicationContext(),"usuario "+ usuario_a_registrar +"...Debe introducir una contraseña");
                else {//Inserta el usuario y contraseña en la tabla_usuarios
                    final String contraseña_a_registrar=txtPassRegistro.getText().toString();
                    System.out.println("usuario a registrar: "+usuario_a_registrar);
                    System.out.println("contraseña a registrar: "+contraseña_a_registrar);
                    new WS_Insertar_Usuario(RegistrarContraseña.this,  usuario_a_registrar, contraseña_a_registrar).execute();
                    //Una vez insertado el usuario vuelve a 'LoginActivity'
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}

