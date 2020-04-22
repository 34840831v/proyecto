package com.pms.mapasgoogle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.ViewHolderComentarios> {

    ArrayList<Comentario> listaComentarios;

    public AdapterComentario(ArrayList<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    @NonNull
    @Override
    public ViewHolderComentarios onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Llamada a nuestra vista
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_comentario, null, false);
        return new ViewHolderComentarios(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderComentarios viewHolderComentarios, int i) {
       // viewHolderComentarios.tvUbic.setText(listaComentarios.get(i).get_id_ubic());
        viewHolderComentarios.tvComentario.setText(listaComentarios.get(i).getComentarios());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class ViewHolderComentarios extends RecyclerView.ViewHolder {
        //cada registro contendra una cadena, correspondiente al nombre y una imagen

        TextView tvUbic, tvComentario;

        public ViewHolderComentarios(@NonNull View itemView) {
            super(itemView);
        //    tvUbic = itemView.findViewById(R.id.tvUbic);
            tvComentario = itemView.findViewById(R.id.tvComentario);
        }
    }
}

