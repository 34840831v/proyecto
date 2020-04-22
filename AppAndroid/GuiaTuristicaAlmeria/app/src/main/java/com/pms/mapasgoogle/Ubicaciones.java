package com.pms.mapasgoogle;

public class Ubicaciones {
    // atributos
    private String nombre, direccion, telefono, ubicacion, web, tipo, cod_usuario;
    private int id;

    // para insertar los métodos get y set de forma automática en Android Studio
    // pulsar Alt + Insert  => escoger Getter and Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodUsuario() {
        return cod_usuario;
    }

    public void setCodUsuario(String cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

}


