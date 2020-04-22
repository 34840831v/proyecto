package com.pms.mapasgoogle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Clase que permitira tomar los datos de la BD, generar las vistas y mostrarlas en el RecyclerView
 * es decir, carga las imagenes a nuestra galeria
 */
public class AdapterFoto extends RecyclerView.Adapter<AdapterFoto.ViewHolderFotos>
               implements View.OnClickListener {

    ArrayList<Foto> listaFotos;
    private View.OnClickListener listener;//Este va a ser el escuchador del evento clicar en una imagen del recyclerView

    public AdapterFoto(ArrayList<Foto> listaFotos) {
        this.listaFotos = listaFotos;
    }
    /**
     * nos permitira acceder a los item de la interfaz y mostrarlos en el RecyclerView.
     * Inicializa la interfaz de un item de la lista a partir del layout utilizando para ello
     * 'LayoutInflater' y lo guarda en el 'ViewHolderEquipos'
     */
    @NonNull
    @Override
    public ViewHolderFotos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Llamada a nuestra vista
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_rv_foto, null, false);
        //pongo el escuchador a escuchar el evento de clicar en una imagen
        view.setOnClickListener(this);
        //Enviamos la vista a nuestro holder
        ViewHolderFotos viewHolder = new ViewHolderFotos(view);
        return viewHolder;
    }

    /**
     * actualiza los campos de la interfaz viewHolderEquipos, con los elementos que correspondan a
     * la posición. Reutiliza las vistas en vez de utilizar por ejemplo el buscar por id
     * @param viewHolderFotos
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderFotos viewHolderFotos, int i) {
        System.out.println("En listaFotos la url_imagen en la posicion "+i+" es: "+listaFotos.get(i).getUrl_imagen());
        Picasso.get().load(listaFotos.get(i).getUrl_imagen()).resize(200, 200).into(viewHolderFotos.ivFoto);
    }

    /**
     * Nos devuelve el tamaño de la lista
     * @return
     */
    @Override
    public int getItemCount() {
        System.out.println("El tamaño de listaFotos es: "+listaFotos.size());
        return listaFotos.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if (listener!=null) {
            listener.onClick(view);
        }
    }

    /**
     * proporciona una referencia a las vistas para cada elemento de datos
     * los elementos de datos complejos pueden necesitar más vistas por elemento,
     * y usted proporciona acceso a todas las vistas para un elemento de datos en un titular de vista
     */
    public class ViewHolderFotos extends RecyclerView.ViewHolder {
        //cada registro contendra una cadena, correspondiente al nombre y una imagen
        ImageView ivFoto;

        public ViewHolderFotos(@NonNull View itemView) {
            super(itemView);

            ivFoto = itemView.findViewById(R.id.ivFoto);
        }
    }
}

