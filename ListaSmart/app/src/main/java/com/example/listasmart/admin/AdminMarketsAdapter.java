package com.example.listasmart;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class AdminMarketsAdapter extends RecyclerView.Adapter<AdminMarketsAdapter.ViewHolder> {

    public interface OnMarketClickListener {
        void onMarketClick(MercadoAdminModel mercado);
    }

    public interface OnDeleteClickListener {
        void onDelete(MercadoAdminModel mercado);
    }

    private List<MercadoAdminModel> lista;
    private final OnMarketClickListener onMarketClickListener;
    private final OnDeleteClickListener onDeleteClickListener;

    public AdminMarketsAdapter(List<MercadoAdminModel> lista,
                               OnMarketClickListener onMarketClickListener,
                               OnDeleteClickListener onDeleteClickListener) {
        this.lista = lista;
        this.onMarketClickListener = onMarketClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void atualizarLista(List<MercadoAdminModel> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MercadoAdminModel mercado = lista.get(position);

        holder.tvNomeMercado.setText(mercado.getNomeMercado());
        holder.tvResponsavel.setText("Responsável: " + mercado.getNomeResponsavel());
        holder.tvEmail.setText(mercado.getEmail());
        holder.tvEndereco.setText(mercado.getEndereco());

        if (mercado.getImagemUri() != null && !mercado.getImagemUri().isEmpty()) {
            holder.ivMarket.setImageURI(Uri.parse(mercado.getImagemUri()));
        } else {
            holder.ivMarket.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> onMarketClickListener.onMarketClick(mercado));
        holder.btnExcluir.setOnClickListener(v -> onDeleteClickListener.onDelete(mercado));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMarket;
        TextView tvNomeMercado;
        TextView tvResponsavel;
        TextView tvEmail;
        TextView tvEndereco;
        Button btnExcluir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMarket = itemView.findViewById(R.id.ivMarketItem);
            tvNomeMercado = itemView.findViewById(R.id.tvNomeMercadoItem);
            tvResponsavel = itemView.findViewById(R.id.tvResponsavelItem);
            tvEmail = itemView.findViewById(R.id.tvEmailItem);
            tvEndereco = itemView.findViewById(R.id.tvEnderecoItem);
            btnExcluir = itemView.findViewById(R.id.btnExcluirMercadoItem);
        }
    }
}