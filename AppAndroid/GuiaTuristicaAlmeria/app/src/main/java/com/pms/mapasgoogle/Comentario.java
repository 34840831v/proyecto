package com.pms.mapasgoogle;

public class Comentario {
    private int id_ubic;
    private String comentarios;


    public Comentario(int id_ubic, String comentarios) {
        this.id_ubic = id_ubic;
        this.comentarios = comentarios;
    }

    public int get_id_ubic() {
        return id_ubic;
    }

    public String getComentarios() {
        return comentarios;
    }

}
