package com.pms.mapasgoogle;

public class Foto {

    private String url_imagen;
    private int id_ubicacion, id_foto;

    public Foto(int id_foto, int id_ubicacion, String url_imagen) {
        this.id_foto = id_foto;
        this.url_imagen = url_imagen;
        this.id_ubicacion = id_ubicacion;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public int get_id_foto() {
        return id_foto;
    }

}
