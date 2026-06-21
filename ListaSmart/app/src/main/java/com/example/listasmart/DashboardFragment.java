package com.example.listasmart;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

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
        TextView tvFaixaRanking = view.findViewById(R.id.tvFaixaRanking);
        TextView tvPercentualCupons = view.findViewById(R.id.tvPercentualCupons);
        TextView tvPercentualCuponsNumero = view.findViewById(R.id.tvPercentualCuponsNumero);
        TextView tvLegendaCupons = view.findViewById(R.id.tvLegendaCupons);
        TextView tvLegendaManuais = view.findViewById(R.id.tvLegendaManuais);
        TextView tvTendenciaPreco = view.findViewById(R.id.tvTendenciaPreco);
        TextView tvResumoTendencia = view.findViewById(R.id.tvResumoTendencia);
        TextView tvOportunidadeMercado = view.findViewById(R.id.tvOportunidadeMercado);
        TextView tvUltimosRegistros = view.findViewById(R.id.tvUltimosRegistros);
        TextView tvHistoricoVazio = view.findViewById(R.id.tvHistoricoVazio);
        TextView tvTopProdutosVazio = view.findViewById(R.id.tvTopProdutosVazio);
        LinearLayout llHistoricoTimeline = view.findViewById(R.id.llHistoricoTimeline);
        LinearLayout llTopProdutosRecentes = view.findViewById(R.id.llTopProdutosRecentes);
        ProgressBar pbRankingMercado = view.findViewById(R.id.pbRankingMercado);
        CircularProgressIndicator cpiOrigemRegistros = view.findViewById(R.id.cpiOrigemRegistros);

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
            int totalManuais = Math.max(totalPrecos - totalCupons, 0);
            String ranking = dbHelper.obterPosicaoRankingMercado(marketId);
            String mediaPreco = dbHelper.obterMediaPrecoMercado(marketId);
            int totalMercados = dbHelper.obterTotalMercadosCadastrados();

            tvTotalPrecos.setText(String.valueOf(totalPrecos));
            tvTotalCupons.setText(String.valueOf(totalCupons));
            tvRankingMercado.setText(ranking);
            tvMediaPreco.setText(mediaPreco);

            int percentualCupons = totalPrecos > 0 ? (totalCupons * 100) / totalPrecos : 0;
            int percentualManuais = totalPrecos > 0 ? (totalManuais * 100) / totalPrecos : 0;
            int posicaoRanking = extrairPosicaoRanking(ranking);
            int progressoRanking = calcularProgressoRanking(posicaoRanking, totalMercados);

            tvPercentualCuponsNumero.setText(percentualCupons + "%");
            tvPercentualCupons.setText("Cupons");
            tvLegendaCupons.setText(totalCupons + (totalCupons == 1 ? " cupom enviado" : " cupons enviados") + " • " + percentualCupons + "%");
            tvLegendaManuais.setText(totalManuais + (totalManuais == 1 ? " registro manual" : " registros manuais") + " • " + percentualManuais + "%");
            tvFaixaRanking.setText(obterFaixaRanking(ranking, totalMercados));

            pbRankingMercado.setProgress(progressoRanking);
            cpiOrigemRegistros.setProgress(percentualCupons);

            if ("Sem ranking".equals(ranking)) {
                tvResumoRanking.setText("Ainda não há dados suficientes para comparar este mercado");
                tvResumoDesempenho.setText("Seu mercado já registrou " + totalPrecos + " preços e enviou " + totalCupons + " cupons. Continue alimentando os dados para entrar no ranking.");
                tvComparativoMercado.setText("Seu mercado ainda está construindo histórico para análise competitiva.");
            } else {
                tvResumoRanking.setText("Posição calculada pela média de preços entre " + totalMercados + " mercados cadastrados");
                tvResumoDesempenho.setText("Seu mercado ocupa " + ranking + " e mantém média de " + mediaPreco + ".");
                tvComparativoMercado.setText("Você já pode comparar sua competitividade com os demais mercados cadastrados.");
            }

            java.util.List<HistoricoPrecoModel> historico = dbHelper.obterHistoricoMediaPrecoMercado(marketId);
            java.util.List<TopProdutoDashboardModel> topProdutos = dbHelper.obterTopProdutosRecentesMercado(marketId);

            if (!"Sem dados".equals(produtoMaisPesquisado)) {
                tvOportunidadeMercado.setText("O produto mais pesquisado no app é " + produtoMaisPesquisado + ". Vale reforçar preço e visibilidade dessa categoria.");
            } else {
                tvOportunidadeMercado.setText("Cadastre mais dados para gerar recomendações automáticas.");
            }

            atualizarTendenciaPreco(tvTendenciaPreco, tvResumoTendencia, historico);
            renderizarHistoricoTimeline(llHistoricoTimeline, tvHistoricoVazio, historico);
            renderizarTopProdutosRecentes(llTopProdutosRecentes, tvTopProdutosVazio, topProdutos);
            tvUltimosRegistros.setText(dbHelper.obterUltimosRegistrosMercado(marketId));
        } else {
            tvTotalPrecos.setText("0");
            tvTotalCupons.setText("0");
            tvRankingMercado.setText("Sem ranking");
            tvResumoRanking.setText("Ainda não há dados suficientes para comparar este mercado");
            tvMediaPreco.setText("Sem dados");
            tvResumoDesempenho.setText("Cadastre preços e cupons para começar a gerar indicadores e aparecer no ranking.");
            tvComparativoMercado.setText("Sem comparativo disponível.");
            tvFaixaRanking.setText("Sem dados para comparar");
            tvPercentualCuponsNumero.setText("0%");
            tvPercentualCupons.setText("Cupons");
            tvLegendaCupons.setText("0 cupons enviados • 0%");
            tvLegendaManuais.setText("0 registros manuais • 0%");
            pbRankingMercado.setProgress(0);
            cpiOrigemRegistros.setProgress(0);
            tvTendenciaPreco.setText("Sem dados suficientes");
            tvResumoTendencia.setText("Ainda não há histórico suficiente para analisar tendência.");
            tvOportunidadeMercado.setText("Cadastre mais dados para gerar recomendações automáticas.");
            renderizarHistoricoTimeline(llHistoricoTimeline, tvHistoricoVazio, new java.util.ArrayList<>());
            renderizarTopProdutosRecentes(llTopProdutosRecentes, tvTopProdutosVazio, new java.util.ArrayList<>());
            tvUltimosRegistros.setText("Sem registros recentes.");
        }

        return view;
    }

    private int extrairPosicaoRanking(String ranking) {
        if (ranking == null || !ranking.contains("º")) {
            return 0;
        }

        try {
            String numero = ranking.substring(0, ranking.indexOf("º")).trim();
            return Integer.parseInt(numero);
        } catch (Exception e) {
            return 0;
        }
    }

    private int calcularProgressoRanking(int posicao, int totalMercados) {
        if (posicao <= 0 || totalMercados <= 0) {
            return 0;
        }

        return ((totalMercados - posicao + 1) * 100) / totalMercados;
    }

    private String obterFaixaRanking(String ranking, int totalMercados) {
        if ("Sem ranking".equals(ranking)) {
            return "Sem dados para comparar";
        }

        int posicao = extrairPosicaoRanking(ranking);

        if (totalMercados <= 1) {
            return "Mercado único com dados suficientes no sistema";
        }

        if (posicao == 1) {
            return "Melhor média de preços entre os mercados";
        }

        if (posicao <= Math.max(2, totalMercados / 2)) {
            return "Seu mercado está na metade superior do ranking";
        }

        return "Há espaço para ganhar competitividade no ranking";
    }

    private void renderizarHistoricoTimeline(LinearLayout container, TextView vazio, java.util.List<HistoricoPrecoModel> historico) {
        container.removeAllViews();
        container.addView(vazio);

        if (historico == null || historico.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < historico.size(); i++) {
            HistoricoPrecoModel item = historico.get(i);
            View itemView = inflater.inflate(R.layout.item_historico_timeline, container, false);

            TextView tvDataHistoricoItem = itemView.findViewById(R.id.tvDataHistoricoItem);
            TextView tvPrecoHistoricoItem = itemView.findViewById(R.id.tvPrecoHistoricoItem);
            TextView tvProdutosHistoricoItem = itemView.findViewById(R.id.tvProdutosHistoricoItem);
            View viewLinhaInferior = itemView.findViewById(R.id.viewLinhaInferior);
            View viewPontoTimeline = itemView.findViewById(R.id.viewPontoTimeline);

            tvDataHistoricoItem.setText(item.getData());
            tvPrecoHistoricoItem.setText(item.getPrecoMedio());
            tvProdutosHistoricoItem.setText(
                    item.getQuantidadeProdutos() +
                            (item.getQuantidadeProdutos() == 1 ? " produto na média" : " produtos na média")
            );

            int corDestaque = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dashboard_warning);

            if (i > 0) {
                double valorAnterior = historico.get(i - 1).getPrecoMedioValor();

                if (item.getPrecoMedioValor() < valorAnterior) {
                    corDestaque = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dashboard_success);
                } else if (item.getPrecoMedioValor() > valorAnterior) {
                    corDestaque = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dashboard_danger);
                }
            }

            tvPrecoHistoricoItem.setTextColor(corDestaque);
            viewPontoTimeline.setBackgroundTintList(android.content.res.ColorStateList.valueOf(corDestaque));

            if (i == historico.size() - 1) {
                viewLinhaInferior.setVisibility(View.INVISIBLE);
            }

            container.addView(itemView);
        }
    }

    private void atualizarTendenciaPreco(TextView titulo, TextView resumo, java.util.List<HistoricoPrecoModel> historico) {
        if (historico == null || historico.size() < 2) {
            titulo.setText("Sem dados suficientes");
            titulo.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white));
            resumo.setText("Ainda não há histórico suficiente para analisar tendência.");
            return;
        }

        HistoricoPrecoModel ultimo = historico.get(historico.size() - 1);
        HistoricoPrecoModel penultimo = historico.get(historico.size() - 2);

        double diferenca = ultimo.getPrecoMedioValor() - penultimo.getPrecoMedioValor();

        if (diferenca < 0) {
            titulo.setText("↓ Em queda");
            titulo.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dashboard_success));
            resumo.setText("A média caiu de " + penultimo.getPrecoMedio() + " para " + ultimo.getPrecoMedio() + ".");
        } else if (diferenca > 0) {
            titulo.setText("↑ Em alta");
            titulo.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dashboard_danger));
            resumo.setText("A média subiu de " + penultimo.getPrecoMedio() + " para " + ultimo.getPrecoMedio() + ".");
        } else {
            titulo.setText("→ Estável");
            titulo.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.dashboard_warning));
            resumo.setText("A média permaneceu em " + ultimo.getPrecoMedio() + " nos dois últimos pontos.");
        }
    }

    private void renderizarTopProdutosRecentes(LinearLayout container, TextView vazio, java.util.List<TopProdutoDashboardModel> produtos) {
        container.removeAllViews();
        container.addView(vazio);

        if (produtos == null || produtos.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int maiorQuantidade = produtos.get(0).getQuantidadeRegistros();

        for (TopProdutoDashboardModel item : produtos) {
            View itemView = inflater.inflate(R.layout.item_top_produto_dashboard, container, false);

            TextView tvNomeTopProduto = itemView.findViewById(R.id.tvNomeTopProduto);
            TextView tvQuantidadeTopProduto = itemView.findViewById(R.id.tvQuantidadeTopProduto);
            ProgressBar pbTopProduto = itemView.findViewById(R.id.pbTopProduto);

            int percentual = maiorQuantidade > 0 ? (item.getQuantidadeRegistros() * 100) / maiorQuantidade : 0;

            tvNomeTopProduto.setText(item.getNomeProduto());
            tvQuantidadeTopProduto.setText(
                    item.getQuantidadeRegistros() +
                            (item.getQuantidadeRegistros() == 1 ? " registro" : " registros")
            );
            pbTopProduto.setProgress(percentual);

            container.addView(itemView);
        }
    }
}