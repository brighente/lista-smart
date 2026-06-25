package com.example.listasmart;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        TextView tvTotalProdutosComPreco = view.findViewById(R.id.tvTotalProdutosComPreco);
        TextView tvTotalRegistrosPreco = view.findViewById(R.id.tvTotalRegistrosPreco);
        TextView tvTotalManuais = view.findViewById(R.id.tvTotalManuais);
        TextView tvTotalCupons = view.findViewById(R.id.tvTotalCupons);
        TextView tvProdutoMaisPesquisado = view.findViewById(R.id.tvProdutoMaisPesquisado);
        TextView tvCategoriaMaisPesquisada = view.findViewById(R.id.tvCategoriaMaisPesquisada);
        TextView tvRankingMercado = view.findViewById(R.id.tvRankingMercado);
        TextView tvResumoRanking = view.findViewById(R.id.tvResumoRanking);
        TextView tvResumoDesempenho = view.findViewById(R.id.tvResumoDesempenho);
        TextView tvComparativoMercado = view.findViewById(R.id.tvComparativoMercado);
        TextView tvFaixaRanking = view.findViewById(R.id.tvFaixaRanking);
        TextView tvBaseCompetitividade = view.findViewById(R.id.tvBaseCompetitividade);
        TextView tvAbrirOportunidadesPreco = view.findViewById(R.id.tvAbrirOportunidadesPreco);
        TextView tvPercentualCupons = view.findViewById(R.id.tvPercentualCupons);
        TextView tvPercentualCuponsNumero = view.findViewById(R.id.tvPercentualCuponsNumero);
        TextView tvLegendaCupons = view.findViewById(R.id.tvLegendaCupons);
        TextView tvLegendaManuais = view.findViewById(R.id.tvLegendaManuais);
        TextView tvOportunidadeMercado = view.findViewById(R.id.tvOportunidadeMercado);
        TextView tvMaioresDiferencasPrecoVazio = view.findViewById(R.id.tvMaioresDiferencasPrecoVazio);
        TextView tvUltimosRegistros = view.findViewById(R.id.tvUltimosRegistros);
        TextView tvHistoricoVazio = view.findViewById(R.id.tvHistoricoVazio);
        TextView tvOportunidadesLiderDashboardVazio = view.findViewById(R.id.tvOportunidadesLiderDashboardVazio);
        LinearLayout llMaioresDiferencasPreco = view.findViewById(R.id.llMaioresDiferencasPreco);
        LinearLayout llHistoricoTimeline = view.findViewById(R.id.llHistoricoTimeline);
        LinearLayout llOportunidadesLiderDashboard = view.findViewById(R.id.llOportunidadesLiderDashboard);
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
        tvAbrirOportunidadesPreco.setOnClickListener(v -> abrirTelaOportunidades());

        String produtoMaisPesquisado = dbHelper.obterProdutoMaisPesquisado();
        String categoriaMaisPesquisada = dbHelper.obterCategoriaMaisPesquisada();

        tvProdutoMaisPesquisado.setText(produtoMaisPesquisado);
        tvCategoriaMaisPesquisada.setText(categoriaMaisPesquisada);

        if (marketId != null && !marketId.isEmpty()) {
            int totalProdutosComPreco = dbHelper.obterTotalProdutosComPrecoPorMercado(marketId);
            int totalRegistrosPreco = dbHelper.obterTotalRegistrosPrecoPorMercado(marketId);
            int totalCupons = dbHelper.obterTotalCuponsPorMercado(marketId);
            int totalManuais = dbHelper.obterTotalManuaisPorMercado(marketId);
            CompetitividadeMercadoModel competitividade = dbHelper.obterCompetitividadeMercado(marketId);

            tvTotalProdutosComPreco.setText(String.valueOf(totalProdutosComPreco));
            tvTotalRegistrosPreco.setText(String.valueOf(totalRegistrosPreco));
            tvTotalManuais.setText(String.valueOf(totalManuais));
            tvTotalCupons.setText(String.valueOf(totalCupons));

            int percentualCupons = totalRegistrosPreco > 0 ? (totalCupons * 100) / totalRegistrosPreco : 0;
            int percentualManuais = totalRegistrosPreco > 0 ? (totalManuais * 100) / totalRegistrosPreco : 0;

            tvPercentualCuponsNumero.setText(percentualCupons + "%");
            tvPercentualCupons.setText("Cupons");
            tvLegendaCupons.setText(totalCupons + (totalCupons == 1 ? " cupom enviado" : " cupons enviados") + " • " + percentualCupons + "%");
            tvLegendaManuais.setText(totalManuais + (totalManuais == 1 ? " registro manual" : " registros manuais") + " • " + percentualManuais + "%");
            cpiOrigemRegistros.setProgress(percentualCupons);

            if (!competitividade.possuiBaseComparavel()) {
                tvRankingMercado.setText("Sem ranking");
                tvResumoRanking.setText("Ainda não há base comparável suficiente para medir competitividade por menor preço.");
                tvComparativoMercado.setText("Sem disputa válida entre mercados");
                tvFaixaRanking.setText("Cadastre preços de produtos que também existam em outros mercados.");
                tvBaseCompetitividade.setText("A comparação considera apenas o último preço de produtos com pelo menos 2 mercados.");
                tvResumoDesempenho.setText("Seu mercado já registrou " + totalRegistrosPreco + " preços, sendo " + totalCupons + " via cupom e " + totalManuais + " manuais. Amplie a base comparável para entrar no ranking.");
            } else {
                String ranking = competitividade.getPosicao() + "º lugar";
                String percentualVitorias = String.format(java.util.Locale.getDefault(), "%.1f%% de vitórias por menor preço", competitividade.getPercentualVitorias());
                String baseComparacao = competitividade.getProdutosVencidos() +
                        (competitividade.getProdutosVencidos() == 1 ? " vitória em " : " vitórias em ") +
                        competitividade.getProdutosComparaveis() +
                        (competitividade.getProdutosComparaveis() == 1 ? " produto comparável" : " produtos comparáveis");

                tvRankingMercado.setText(ranking);
                tvResumoRanking.setText(
                        "Comparativo por menor preço entre " +
                                competitividade.getTotalMercadosCompetitivos() +
                                (competitividade.getTotalMercadosCompetitivos() == 1 ? " mercado com base comparável." : " mercados com base comparável.")
                );
                tvComparativoMercado.setText(percentualVitorias);
                tvFaixaRanking.setText(baseComparacao);
                tvBaseCompetitividade.setText("Empates no menor preço contam como vitória compartilhada.");
                tvResumoDesempenho.setText(competitividade.getFaixaResumo() + ".");
                tvComparativoMercado.setContentDescription(percentualVitorias);
            }

            java.util.List<HistoricoPrecoModel> historico = dbHelper.obterHistoricoMediaPrecoMercado(marketId);
            java.util.List<OportunidadePrecoProdutoModel> oportunidadesLider = dbHelper.obterOportunidadesVirarLiderMercado(marketId, 3);
            java.util.List<DiferencaPrecoProdutoModel> produtosMaiorDiferenca = dbHelper.obterProdutosMaiorDiferencaPreco();

            renderizarMaioresDiferencasPreco(llMaioresDiferencasPreco, tvMaioresDiferencasPrecoVazio, produtosMaiorDiferenca);

            if (!"Sem dados".equals(produtoMaisPesquisado) && !"Sem dados".equals(categoriaMaisPesquisada)) {
                tvOportunidadeMercado.setText(
                        "No app inteiro, o produto mais pesquisado no momento é " +
                                produtoMaisPesquisado +
                                " e a categoria com mais interesse é " +
                                categoriaMaisPesquisada +
                                ". Vale dar visibilidade a esses destaques no seu mercado."
                );
            } else if (!"Sem dados".equals(produtoMaisPesquisado)) {
                tvOportunidadeMercado.setText(
                        "No app inteiro, o produto mais pesquisado no momento é " +
                                produtoMaisPesquisado +
                                ". Vale reforçar preço e visibilidade desse item no seu mercado."
                );
            } else if (!"Sem dados".equals(categoriaMaisPesquisada)) {
                tvOportunidadeMercado.setText(
                        "No app inteiro, a categoria com mais interesse no momento é " +
                                categoriaMaisPesquisada +
                                ". Vale destacar produtos dessa categoria no seu mercado."
                );
            } else {
                tvOportunidadeMercado.setText("Cadastre mais dados para gerar recomendações automáticas.");
            }

            renderizarHistoricoTimeline(llHistoricoTimeline, tvHistoricoVazio, historico);
            tvOportunidadesLiderDashboardVazio.setText("Sem oportunidades imediatas para ganhar a lideranca agora.");
            renderizarProdutosTaticosDashboard(llOportunidadesLiderDashboard, tvOportunidadesLiderDashboardVazio, oportunidadesLider);
            tvUltimosRegistros.setText(dbHelper.obterUltimosRegistrosMercado(marketId));
        } else {
            tvTotalProdutosComPreco.setText("0");
            tvTotalRegistrosPreco.setText("0");
            tvTotalManuais.setText("0");
            tvTotalCupons.setText("0");
            tvRankingMercado.setText("Sem ranking");
            tvResumoRanking.setText("Ainda não há base comparável suficiente para medir competitividade por menor preço.");
            tvResumoDesempenho.setText("Cadastre preços e cupons para começar a gerar indicadores e aparecer no ranking.");
            tvComparativoMercado.setText("Sem disputa válida entre mercados");
            tvFaixaRanking.setText("Cadastre preços para formar uma base comparável");
            tvBaseCompetitividade.setText("A comparação considera apenas o último preço de produtos com pelo menos 2 mercados.");
            tvPercentualCuponsNumero.setText("0%");
            tvPercentualCupons.setText("Cupons");
            tvLegendaCupons.setText("0 cupons enviados • 0%");
            tvLegendaManuais.setText("0 registros manuais • 0%");
            cpiOrigemRegistros.setProgress(0);
            tvOportunidadeMercado.setText("Cadastre mais dados para gerar recomendações automáticas.");
            renderizarMaioresDiferencasPreco(llMaioresDiferencasPreco, tvMaioresDiferencasPrecoVazio, new java.util.ArrayList<>());
            renderizarHistoricoTimeline(llHistoricoTimeline, tvHistoricoVazio, new java.util.ArrayList<>());
            tvOportunidadesLiderDashboardVazio.setText("Sem oportunidades imediatas para ganhar a lideranca agora.");
            renderizarProdutosTaticosDashboard(llOportunidadesLiderDashboard, tvOportunidadesLiderDashboardVazio, new java.util.ArrayList<>());
            tvUltimosRegistros.setText("Sem registros recentes.");
        }

        return view;
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

    private void renderizarProdutosTaticosDashboard(
            LinearLayout container,
            TextView vazio,
            java.util.List<OportunidadePrecoProdutoModel> produtos
    ) {
        container.removeAllViews();
        container.addView(vazio);

        if (produtos == null || produtos.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (OportunidadePrecoProdutoModel item : produtos) {
            View itemView = inflater.inflate(R.layout.item_produto_tatico_dashboard, container, false);

            TextView tvNomeProdutoTatico = itemView.findViewById(R.id.tvNomeProdutoTatico);
            TextView tvCategoriaProdutoTatico = itemView.findViewById(R.id.tvCategoriaProdutoTatico);
            TextView tvResumoProdutoTatico = itemView.findViewById(R.id.tvResumoProdutoTatico);
            TextView tvContextoProdutoTatico = itemView.findViewById(R.id.tvContextoProdutoTatico);
            TextView tvBadgeProdutoTatico = itemView.findViewById(R.id.tvBadgeProdutoTatico);
            TextView tvPrecoAtualProdutoTatico = itemView.findViewById(R.id.tvPrecoAtualProdutoTatico);
            TextView tvPrecoAlvoProdutoTatico = itemView.findViewById(R.id.tvPrecoAlvoProdutoTatico);
            View viewIndicadorProdutoTatico = itemView.findViewById(R.id.viewIndicadorProdutoTatico);

            tvNomeProdutoTatico.setText(item.getNomeProduto());
            tvCategoriaProdutoTatico.setText(item.getNomeCategoria());

            int corOportunidade = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.brand_yellow);

            tvBadgeProdutoTatico.setText("Oportunidade");
            tvBadgeProdutoTatico.setBackgroundResource(R.drawable.bg_chip_warning);
            tvResumoProdutoTatico.setBackgroundResource(R.drawable.bg_tactical_summary_warning);
            tvResumoProdutoTatico.setTextColor(corOportunidade);
            viewIndicadorProdutoTatico.setBackgroundColor(corOportunidade);
            tvResumoProdutoTatico.setText(
                    "Faltam " +
                            String.format(java.util.Locale.getDefault(), "R$ %.2f", item.getDiferencaParaLider()) +
                            " para igualar o menor preco."
            );
            tvPrecoAtualProdutoTatico.setText(
                    item.getPrecoMercado() != null
                            ? String.format(java.util.Locale.getDefault(), "R$ %.2f", item.getPrecoMercado())
                            : "Sem preco"
            );
            tvPrecoAlvoProdutoTatico.setText(
                    item.getMenorPreco() != null
                            ? String.format(java.util.Locale.getDefault(), "R$ %.2f", item.getMenorPreco())
                            : "Sem base"
            );
            tvContextoProdutoTatico.setText(
                    "Lider atual: " +
                            item.getMercadoReferencia() +
                            " • " +
                            item.getTotalBuscas() +
                            (item.getTotalBuscas() == 1 ? " busca no app" : " buscas no app")
            );

            container.addView(itemView);
        }
    }

    private void renderizarMaioresDiferencasPreco(LinearLayout container, TextView vazio, java.util.List<DiferencaPrecoProdutoModel> produtos) {
        container.removeAllViews();
        container.addView(vazio);

        if (produtos == null || produtos.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (DiferencaPrecoProdutoModel item : produtos) {
            View itemView = inflater.inflate(R.layout.item_diferenca_preco_dashboard, container, false);

            TextView tvNomeProdutoDiferenca = itemView.findViewById(R.id.tvNomeProdutoDiferenca);
            TextView tvValorDiferenca = itemView.findViewById(R.id.tvValorDiferenca);
            TextView tvMercadoMaisBarato = itemView.findViewById(R.id.tvMercadoMaisBarato);
            TextView tvMercadoMaisCaro = itemView.findViewById(R.id.tvMercadoMaisCaro);

            tvNomeProdutoDiferenca.setText(item.getNomeProduto());
            tvValorDiferenca.setText(
                    "Diferença: " +
                            String.format(java.util.Locale.getDefault(), "R$ %.2f", item.getDiferencaPreco())
            );
            tvMercadoMaisBarato.setText(
                    "Mais barato em " +
                            item.getMercadoMaisBarato() +
                            " • " +
                            String.format(java.util.Locale.getDefault(), "R$ %.2f", item.getMenorPreco())
            );
            tvMercadoMaisCaro.setText(
                    "Mais caro em " +
                            item.getMercadoMaisCaro() +
                            " • " +
                            String.format(java.util.Locale.getDefault(), "R$ %.2f", item.getMaiorPreco())
            );

            container.addView(itemView);
        }
    }

    private void abrirTelaOportunidades() {
        if (requireActivity() instanceof HomeActivity) {
            ((HomeActivity) requireActivity()).abrirOportunidadesPrecoMercado();
        }
    }
}
