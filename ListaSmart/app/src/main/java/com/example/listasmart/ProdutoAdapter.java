package com.example.listasmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder> {

    public interface OnAdicionarClickListener {
        void onAdicionarClick(ProdutoModel produto);
    }

    private List<ProdutoModel> listaProdutos;
    private final OnAdicionarClickListener listener;

    public ProdutoAdapter(List<ProdutoModel> listaProdutos, OnAdicionarClickListener listener) {
        this.listaProdutos = listaProdutos;
        this.listener = listener;
    }

    public void atualizarLista(List<ProdutoModel> novaLista) {
        this.listaProdutos = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProdutoModel produto = listaProdutos.get(position);
        holder.txtNome.setText(produto.getNome());
        holder.txtMercado.setText(produto.getMercado());
        holder.txtTipo.setText(produto.getTipoRegistro());

        if (produto.getPreco() == 0.0) {
            holder.txtPreco.setText("Sem preço");
        } else {
            holder.txtPreco.setText(String.format(Locale.getDefault(), "R$ %.2f", produto.getPreco()));
        }

        holder.btnAdicionarProduto.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdicionarClick(produto);
            }
        });

        if (produto.getImagemUri() != null && !produto.getImagemUri().isEmpty()) {
            // Se você usar biblioteca de imagem futuramente (Glide/Picasso), carrega aqui.
        }
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtMercado, txtTipo, txtPreco;
        ImageView imgProduto;
        android.widget.Button btnAdicionarProduto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeProduto);
            txtMercado = itemView.findViewById(R.id.txtMercadoProduto);
            txtTipo = itemView.findViewById(R.id.txtTipoRegistro);
            txtPreco = itemView.findViewById(R.id.txtPrecoProduto);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            btnAdicionarProduto = itemView.findViewById(R.id.btnAdicionarProduto);
        }
    }
}