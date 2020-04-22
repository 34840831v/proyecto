package com.pms.mapasgoogle;

import android.text.InputType;
import android.widget.EditText;

public class MostrarContrasena {
    public void mostrarContrasena (EditText password, boolean isChecked){
        if(isChecked) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            password.setInputType(129);
        }
    }
}
