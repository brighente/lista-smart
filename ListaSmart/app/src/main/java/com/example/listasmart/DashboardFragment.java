package com.example.listasmart;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private DatabaseHelper dbHelper;

    public DashboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        String marketId = requireActivity().getIntent().getStringExtra("MARKET_ID");
        String marketName = requireActivity().getIntent().getStringExtra("MARKET_NAME");
        String marketImage = requireActivity().getIntent().getStringExtra("MARKET_IMAGE");

        ImageView ivMercadoDashboard = view.findViewById(R.id.ivMercadoDashboard);
        TextView tvTituloMercado = view.findViewById(R.id.tvTituloMercado);
        TextView tvTotalPrecos = view.findViewById(R.id.tvTotalPrecos);
        TextView tvTotalCupons = view.findViewById(R.id.tvTotalCupons);
        TextView tvProdutoMaisPesquisado = view.findViewById(R.id.tvProdutoMaisPesquisado);
        TextView tvCategoriaMaisPesquisada = view.findViewById(R.id.tvCategoriaMaisPesquisada);
        TextView tvRankingMercado = view.findViewById(R.id.tvRankingMercado);
        TextView tvMediaPreco = view.findViewById(R.id.tvMediaPreco);

        tvTituloMercado.setText(
                marketName != null && !marketName.isEmpty() ? marketName : "Dashboard do Mercado"
        );

        if (marketImage != null && !marketImage.isEmpty()) {
            ivMercadoDashboard.setImageURI(Uri.parse(marketImage));
        } else {
            ivMercadoDashboard.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (marketId != null && !marketId.isEmpty()) {
            tvTotalPrecos.setText(String.valueOf(dbHelper.obterTotalPrecosPorMercado(marketId)));
            tvTotalCupons.setText(String.valueOf(dbHelper.obterTotalCuponsPorMercado(marketId)));
            tvRankingMercado.setText(dbHelper.obterPosicaoRankingMercado(marketId));
            tvMediaPreco.setText(dbHelper.obterMediaPrecoMercado(marketId));
        } else {
            tvTotalPrecos.setText("0");
            tvTotalCupons.setText("0");
            tvRankingMercado.setText("Sem ranking");
            tvMediaPreco.setText("Sem dados");
        }

        tvProdutoMaisPesquisado.setText(dbHelper.obterProdutoMaisPesquisado());
        tvCategoriaMaisPesquisada.setText(dbHelper.obterCategoriaMaisPesquisada());

        return view;
    }
}