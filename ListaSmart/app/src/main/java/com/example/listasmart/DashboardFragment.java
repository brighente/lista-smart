package com.example.listasmart;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        TextView tvResumoMercado = view.findViewById(R.id.tvResumoMercado);
        TextView tvTotalPrecos = view.findViewById(R.id.tvTotalPrecos);
        TextView tvTotalCupons = view.findViewById(R.id.tvTotalCupons);
        TextView tvProdutoMaisPesquisado = view.findViewById(R.id.tvProdutoMaisPesquisado);
        TextView tvCategoriaMaisPesquisada = view.findViewById(R.id.tvCategoriaMaisPesquisada);
        TextView tvRankingMercado = view.findViewById(R.id.tvRankingMercado);
        TextView tvResumoRanking = view.findViewById(R.id.tvResumoRanking);
        TextView tvMediaPreco = view.findViewById(R.id.tvMediaPreco);
        TextView tvResumoDesempenho = view.findViewById(R.id.tvResumoDesempenho);
        TextView tvComparativoMercado = view.findViewById(R.id.tvComparativoMercado);
        TextView tvPercentualCupons = view.findViewById(R.id.tvPercentualCupons);
        TextView tvOportunidadeMercado = view.findViewById(R.id.tvOportunidadeMercado);
        TextView tvHistoricoMediaPreco = view.findViewById(R.id.tvHistoricoMediaPreco);
        TextView tvUltimosRegistros = view.findViewById(R.id.tvUltimosRegistros);
        ProgressBar pbPercentualCupons = view.findViewById(R.id.pbPercentualCupons);

        tvTituloMercado.setText(
                marketName != null && !marketName.isEmpty() ? marketName : "Dashboard do Mercado"
        );

        if (marketImage != null && !marketImage.isEmpty()) {
            ivMercadoDashboard.setImageURI(Uri.parse(marketImage));
        } else {
            ivMercadoDashboard.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        tvResumoMercado.setText("Visão geral do desempenho e da competitividade do mercado");

        String produtoMaisPesquisado = dbHelper.obterProdutoMaisPesquisado();
        String categoriaMaisPesquisada = dbHelper.obterCategoriaMaisPesquisada();

        tvProdutoMaisPesquisado.setText(produtoMaisPesquisado);
        tvCategoriaMaisPesquisada.setText(categoriaMaisPesquisada);

        if (marketId != null && !marketId.isEmpty()) {
            int totalPrecos = dbHelper.obterTotalPrecosPorMercado(marketId);
            int totalCupons = dbHelper.obterTotalCuponsPorMercado(marketId);
            String ranking = dbHelper.obterPosicaoRankingMercado(marketId);
            String mediaPreco = dbHelper.obterMediaPrecoMercado(marketId);
            int totalMercados = dbHelper.obterTotalMercadosCadastrados();

            tvTotalPrecos.setText(String.valueOf(totalPrecos));
            tvTotalCupons.setText(String.valueOf(totalCupons));
            tvRankingMercado.setText(ranking);
            tvMediaPreco.setText(mediaPreco);

            int percentualCupons = totalPrecos > 0 ? (totalCupons * 100) / totalPrecos : 0;
            tvPercentualCupons.setText(percentualCupons + "% dos registros vieram de cupons");
            pbPercentualCupons.setProgress(percentualCupons);

            if ("Sem ranking".equals(ranking)) {
                tvResumoRanking.setText("Ainda não há dados suficientes para comparar este mercado");
                tvResumoDesempenho.setText("Seu mercado já registrou " + totalPrecos + " preços e enviou " + totalCupons + " cupons. Continue alimentando os dados para entrar no ranking.");
                tvComparativoMercado.setText("Seu mercado ainda está em fase inicial de comparação com os demais.");
            } else {
                tvResumoRanking.setText("Posição calculada pela média de preços entre " + totalMercados + " mercados cadastrados");
                tvResumoDesempenho.setText("Seu mercado ocupa " + ranking + " e mantém média de " + mediaPreco + ".");
                tvComparativoMercado.setText("Você concorre com " + totalMercados + " mercados e já tem dados suficientes para análise competitiva.");
            }

            if (!"Sem dados".equals(produtoMaisPesquisado)) {
                tvOportunidadeMercado.setText("O produto mais pesquisado no app é " + produtoMaisPesquisado + ". Vale reforçar preço e visibilidade dessa categoria.");
            } else {
                tvOportunidadeMercado.setText("Cadastre mais dados para gerar recomendações automáticas.");
            }

            tvHistoricoMediaPreco.setText(dbHelper.obterHistoricoMediaPrecoMercado(marketId));
            tvUltimosRegistros.setText(dbHelper.obterUltimosRegistrosMercado(marketId));
        } else {
            tvTotalPrecos.setText("0");
            tvTotalCupons.setText("0");
            tvRankingMercado.setText("Sem ranking");
            tvResumoRanking.setText("Ainda não há dados suficientes para comparar este mercado");
            tvMediaPreco.setText("Sem dados");
            tvResumoDesempenho.setText("Cadastre preços e cupons para começar a gerar indicadores e aparecer no ranking.");
            tvComparativoMercado.setText("Sem comparativo disponível.");
            tvPercentualCupons.setText("0% dos registros vieram de cupons");
            pbPercentualCupons.setProgress(0);
            tvOportunidadeMercado.setText("Cadastre mais dados para gerar recomendações automáticas.");
            tvHistoricoMediaPreco.setText("Sem histórico disponível.");
            tvUltimosRegistros.setText("Sem registros recentes.");
        }

        return view;
    }
}