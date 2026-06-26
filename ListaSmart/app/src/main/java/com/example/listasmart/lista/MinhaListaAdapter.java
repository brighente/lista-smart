package com.example.listasmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class MinhaListaAdapter extends RecyclerView.Adapter<MinhaListaAdapter.ViewHolder> {

    public interface OnItemListaActionListener {
        void onAumentar(ItemListaCompraModel item);
        void onDiminuir(ItemListaCompraModel item);
        void onRemover(ItemListaCompraModel item);
    }

    private List<ItemListaCompraModel> itens;
    private final OnItemListaActionListener listener;

    public MinhaListaAdapter(List<ItemListaCompraModel> itens, OnItemListaActionListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    public void atualizarLista(List<ItemListaCompraModel> novaLista) {
        this.itens = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_compra, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemListaCompraModel item = itens.get(position);

        holder.tvNomeItemLista.setText(item.getNomeProduto());
        holder.tvQuantidadeItemLista.setText(String.valueOf(item.getQuantidade()));

        if (item.getPrecoReferencia() > 0) {
            holder.tvPrecoReferenciaItemLista.setText(
                    String.format(Locale.getDefault(), "Preço de referência: R$ %.2f", item.getPrecoReferencia())
            );
        } else {
            holder.tvPrecoReferenciaItemLista.setText("Preço de referência: sem preço");
        }

        holder.btnAumentarItem.setOnClickListener(v -> listener.onAumentar(item));
        holder.btnDiminuirItem.setOnClickListener(v -> listener.onDiminuir(item));
        holder.btnRemoverItem.setOnClickListener(v -> listener.onRemover(item));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeItemLista;
        TextView tvPrecoReferenciaItemLista;
        TextView tvQuantidadeItemLista;
        Button btnAumentarItem;
        Button btnDiminuirItem;
        Button btnRemoverItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeItemLista = itemView.findViewById(R.id.tvNomeItemLista);
            tvPrecoReferenciaItemLista = itemView.findViewById(R.id.tvPrecoReferenciaItemLista);
            tvQuantidadeItemLista = itemView.findViewById(R.id.tvQuantidadeItemLista);
            btnAumentarItem = itemView.findViewById(R.id.btnAumentarItem);
            btnDiminuirItem = itemView.findViewById(R.id.btnDiminuirItem);
            btnRemoverItem = itemView.findViewById(R.id.btnRemoverItem);
        }
    }
}