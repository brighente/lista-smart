package com.example.listasmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import java.util.Locale;

public class InteligenciaBuscaFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private String periodoSelecionado = "SEMANA";

    private TextView tvTituloResumoPeriodo;
    private TextView tvProdutoMaisBuscadoPeriodo;
    private TextView tvCategoriaMaisBuscadaPeriodo;
    private TextView tvDiaMaisFortePeriodo;
    private TextView tvResumoProdutoMaisBuscadoPeriodo;
    private TextView tvResumoCategoriaMaisBuscadaPeriodo;
    private TextView tvResumoDiaMaisFortePeriodo;
    private TextView tvProdutosAltaVazio;
    private TextView tvProdutosBaixaVazio;
    private TextView tvDiasFortesBuscaVazio;
    private TextView tvProdutosListaVazio;
    private TextView tvPeriodoSemana;
    private TextView tvPeriodoMes;
    private LinearLayout llProdutosAltaPeriodo;
    private LinearLayout llProdutosBaixaPeriodo;
    private LinearLayout llDiasFortesBuscaPeriodo;
    private LinearLayout llProdutosMaisAdicionadosLista;

    public InteligenciaBuscaFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inteligencia_busca, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        tvTituloResumoPeriodo = view.findViewById(R.id.tvTituloResumoPeriodo);
        tvProdutoMaisBuscadoPeriodo = view.findViewById(R.id.tvProdutoMaisBuscadoPeriodo);
        tvCategoriaMaisBuscadaPeriodo = view.findViewById(R.id.tvCategoriaMaisBuscadaPeriodo);
        tvDiaMaisFortePeriodo = view.findViewById(R.id.tvDiaMaisFortePeriodo);
        tvResumoProdutoMaisBuscadoPeriodo = view.findViewById(R.id.tvResumoProdutoMaisBuscadoPeriodo);
        tvResumoCategoriaMaisBuscadaPeriodo = view.findViewById(R.id.tvResumoCategoriaMaisBuscadaPeriodo);
        tvResumoDiaMaisFortePeriodo = view.findViewById(R.id.tvResumoDiaMaisFortePeriodo);
        tvProdutosAltaVazio = view.findViewById(R.id.tvProdutosAltaVazio);
        tvProdutosBaixaVazio = view.findViewById(R.id.tvProdutosBaixaVazio);
        tvDiasFortesBuscaVazio = view.findViewById(R.id.tvDiasFortesBuscaVazio);
        tvProdutosListaVazio = view.findViewById(R.id.tvProdutosListaVazio);
        tvPeriodoSemana = view.findViewById(R.id.tvPeriodoSemana);
        tvPeriodoMes = view.findViewById(R.id.tvPeriodoMes);
        llProdutosAltaPeriodo = view.findViewById(R.id.llProdutosAltaPeriodo);
        llProdutosBaixaPeriodo = view.findViewById(R.id.llProdutosBaixaPeriodo);
        llDiasFortesBuscaPeriodo = view.findViewById(R.id.llDiasFortesBuscaPeriodo);
        llProdutosMaisAdicionadosLista = view.findViewById(R.id.llProdutosMaisAdicionadosLista);

        tvPeriodoSemana.setOnClickListener(v -> {
            periodoSelecionado = "SEMANA";
            carregarConteudo();
        });

        tvPeriodoMes.setOnClickListener(v -> {
            periodoSelecionado = "MES";
            carregarConteudo();
        });

        carregarConteudo();
        return view;
    }

    private void carregarConteudo() {
        aplicarEstadoPeriodo();

        ResumoInteressePeriodoModel resumo = dbHelper.obterResumoInteressePeriodo(periodoSelecionado);
        List<TendenciaBuscaProdutoModel> produtosAlta = dbHelper.obterProdutosEmAltaBusca(6, periodoSelecionado);
        List<TendenciaBuscaProdutoModel> produtosBaixa = dbHelper.obterProdutosEmBaixaBusca(6, periodoSelecionado);
        List<DiaForteBuscaModel> diasFortes = dbHelper.obterDiasMaisFortesBusca(periodoSelecionado, 3);
        List<ProdutoMaisAdicionadoListaModel> produtosMaisAdicionados = dbHelper.obterProdutosMaisAdicionadosLista(5);

        String tituloPeriodo = "SEMANA".equals(periodoSelecionado) ? "Nesta semana" : "Neste mês";
        tvTituloResumoPeriodo.setText(
                tituloPeriodo + ", esta tela aprofunda o comportamento de busca dos usuários no app e já soma " +
                        resumo.getTotalBuscas() +
                        (resumo.getTotalBuscas() == 1 ? " busca registrada." : " buscas registradas.")
        );

        tvProdutoMaisBuscadoPeriodo.setText(resumo.getProdutoMaisBuscado());
        tvCategoriaMaisBuscadaPeriodo.setText(resumo.getCategoriaMaisBuscada());
        tvDiaMaisFortePeriodo.setText(resumo.getDiaMaisForte());

        atualizarResumoTopo(resumo, produtosAlta, produtosBaixa);

        renderizarTendencias(llProdutosAltaPeriodo, tvProdutosAltaVazio, produtosAlta, true);
        renderizarTendencias(llProdutosBaixaPeriodo, tvProdutosBaixaVazio, produtosBaixa, false);
        renderizarDiasFortes(llDiasFortesBuscaPeriodo, tvDiasFortesBuscaVazio, diasFortes);
        renderizarProdutosMaisAdicionados(llProdutosMaisAdicionadosLista, tvProdutosListaVazio, produtosMaisAdicionados);
    }

    private void aplicarEstadoPeriodo() {
        boolean semanaAtiva = "SEMANA".equals(periodoSelecionado);

        tvPeriodoSemana.setBackgroundResource(semanaAtiva ? R.drawable.bg_chip_warning : R.drawable.bg_chip_neutral);
        tvPeriodoSemana.setTextColor(ContextCompat.getColor(requireContext(), semanaAtiva ? R.color.brand_dark_gray : R.color.text_light));

        tvPeriodoMes.setBackgroundResource(semanaAtiva ? R.drawable.bg_chip_neutral : R.drawable.bg_chip_warning);
        tvPeriodoMes.setTextColor(ContextCompat.getColor(requireContext(), semanaAtiva ? R.color.text_light : R.color.brand_dark_gray));
    }

    private void atualizarResumoTopo(
            ResumoInteressePeriodoModel resumo,
            List<TendenciaBuscaProdutoModel> produtosAlta,
            List<TendenciaBuscaProdutoModel> produtosBaixa
    ) {
        String periodoTexto = "SEMANA".equals(periodoSelecionado) ? "semana" : "mês";

        TendenciaBuscaProdutoModel tendenciaProduto = encontrarTendenciaProduto(
                resumo.getProdutoMaisBuscado(),
                produtosAlta,
                produtosBaixa
        );

        if (tendenciaProduto == null) {
            tvResumoProdutoMaisBuscadoPeriodo.setText(
                    "Produto com maior volume de buscas no app neste " + periodoTexto + "."
            );
        } else if (tendenciaProduto.isNovoNoRadar()) {
            tvResumoProdutoMaisBuscadoPeriodo.setText(
                    "Entrou agora no radar e já lidera as buscas neste " + periodoTexto + "."
            );
        } else if (tendenciaProduto.isEmAlta()) {
            tvResumoProdutoMaisBuscadoPeriodo.setText(
                    "Também aparece entre os produtos em alta neste " + periodoTexto + "."
            );
        } else {
            tvResumoProdutoMaisBuscadoPeriodo.setText(
                    "Ainda lidera as buscas, mesmo com perda de ritmo neste " + periodoTexto + "."
            );
        }

        tvResumoCategoriaMaisBuscadaPeriodo.setText(
                "Categoria que mais concentrou interesse dos usuários neste " + periodoTexto + "."
        );

        tvResumoDiaMaisFortePeriodo.setText(
                "Dia da semana com maior pico de buscas registrado neste " + periodoTexto + "."
        );
    }

    @Nullable
    private TendenciaBuscaProdutoModel encontrarTendenciaProduto(
            String nomeProduto,
            List<TendenciaBuscaProdutoModel> produtosAlta,
            List<TendenciaBuscaProdutoModel> produtosBaixa
    ) {
        if (nomeProduto == null || nomeProduto.trim().isEmpty() || "Sem dados".equals(nomeProduto)) {
            return null;
        }

        for (TendenciaBuscaProdutoModel item : produtosAlta) {
            if (nomeProduto.equals(item.getNomeProduto())) {
                return item;
            }
        }

        for (TendenciaBuscaProdutoModel item : produtosBaixa) {
            if (nomeProduto.equals(item.getNomeProduto())) {
                return item;
            }
        }

        return null;
    }

    private void renderizarTendencias(
            LinearLayout container,
            TextView vazio,
            List<TendenciaBuscaProdutoModel> produtos,
            boolean emAlta
    ) {
        container.removeAllViews();
        container.addView(vazio);

        if (produtos == null || produtos.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (TendenciaBuscaProdutoModel item : produtos) {
            View itemView = inflater.inflate(R.layout.item_tendencia_busca, container, false);

            TextView tvNomeTendenciaBusca = itemView.findViewById(R.id.tvNomeTendenciaBusca);
            TextView tvVariacaoTendenciaBusca = itemView.findViewById(R.id.tvVariacaoTendenciaBusca);
            TextView tvCategoriaTendenciaBusca = itemView.findViewById(R.id.tvCategoriaTendenciaBusca);
            TextView tvResumoTendenciaBusca = itemView.findViewById(R.id.tvResumoTendenciaBusca);
            TextView tvLegendaTendenciaBusca = itemView.findViewById(R.id.tvLegendaTendenciaBusca);
            View viewIndicadorTendenciaBusca = itemView.findViewById(R.id.viewIndicadorTendenciaBusca);

            tvNomeTendenciaBusca.setText(item.getNomeProduto());
            tvCategoriaTendenciaBusca.setText(item.getNomeCategoria());
            tvVariacaoTendenciaBusca.setText(item.getResumoVariacao());

            if (item.isNovoNoRadar()) {
                int corNovo = ContextCompat.getColor(requireContext(), R.color.dashboard_warning);
                tvVariacaoTendenciaBusca.setBackgroundResource(R.drawable.bg_chip_warning);
                tvVariacaoTendenciaBusca.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_dark_gray));
                tvResumoTendenciaBusca.setTextColor(corNovo);
                viewIndicadorTendenciaBusca.setBackgroundColor(corNovo);
                tvLegendaTendenciaBusca.setText("Produto novo no radar de buscas. Toque para ver o melhor dia.");
            } else if (emAlta) {
                int corAlta = ContextCompat.getColor(requireContext(), R.color.dashboard_success);
                tvVariacaoTendenciaBusca.setBackgroundResource(R.drawable.bg_chip_success);
                tvVariacaoTendenciaBusca.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                tvResumoTendenciaBusca.setTextColor(corAlta);
                viewIndicadorTendenciaBusca.setBackgroundColor(corAlta);
                tvLegendaTendenciaBusca.setText("Interesse em crescimento. Toque para ver o melhor dia.");
            } else {
                int corQueda = ContextCompat.getColor(requireContext(), R.color.dashboard_danger);
                tvVariacaoTendenciaBusca.setBackgroundResource(R.drawable.bg_chip_danger);
                tvVariacaoTendenciaBusca.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                tvResumoTendenciaBusca.setTextColor(corQueda);
                viewIndicadorTendenciaBusca.setBackgroundColor(corQueda);
                tvLegendaTendenciaBusca.setText("Interesse em queda. Toque para entender o melhor dia.");
            }

            tvResumoTendenciaBusca.setText(
                    String.format(
                            Locale.getDefault(),
                            "%d buscas no período atual • %d no anterior",
                            item.getBuscasPeriodoAtual(),
                            item.getBuscasPeriodoAnterior()
                    )
            );

            itemView.setOnClickListener(v -> mostrarDiaForteProduto(item));

            container.addView(itemView);
        }
    }

    private void renderizarDiasFortes(
            LinearLayout container,
            TextView vazio,
            List<DiaForteBuscaModel> dias
    ) {
        container.removeAllViews();
        container.addView(vazio);

        if (dias == null || dias.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (DiaForteBuscaModel item : dias) {
            View itemView = inflater.inflate(R.layout.item_dia_forte_busca, container, false);

            TextView tvNomeDiaForteBusca = itemView.findViewById(R.id.tvNomeDiaForteBusca);
            TextView tvResumoDiaForteBusca = itemView.findViewById(R.id.tvResumoDiaForteBusca);
            TextView tvTotalDiaForteBusca = itemView.findViewById(R.id.tvTotalDiaForteBusca);

            tvNomeDiaForteBusca.setText(item.getNomeDia());
            tvResumoDiaForteBusca.setText("Dia com mais buscas registradas neste período.");
            tvTotalDiaForteBusca.setText(
                    item.getTotalBuscas() +
                            (item.getTotalBuscas() == 1 ? " busca" : " buscas")
            );

            container.addView(itemView);
        }
    }

    private void renderizarProdutosMaisAdicionados(
        LinearLayout container,
        TextView vazio,
        List<ProdutoMaisAdicionadoListaModel> produtos
    ) {
        container.removeAllViews();
        container.addView(vazio);

        if (produtos == null || produtos.isEmpty()) {
            vazio.setVisibility(View.VISIBLE);
            return;
        }

        vazio.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < produtos.size(); i++) {
            ProdutoMaisAdicionadoListaModel item = produtos.get(i);
            View itemView = inflater.inflate(R.layout.item_produto_lista_insight, container, false);

            TextView tvPosicaoProdutoListaInsight = itemView.findViewById(R.id.tvPosicaoProdutoListaInsight);
            TextView tvNomeProdutoListaInsight = itemView.findViewById(R.id.tvNomeProdutoListaInsight);
            TextView tvCategoriaProdutoListaInsight = itemView.findViewById(R.id.tvCategoriaProdutoListaInsight);
            TextView tvValorPrincipalProdutoListaInsight = itemView.findViewById(R.id.tvValorPrincipalProdutoListaInsight);
            TextView tvRotuloPrincipalProdutoListaInsight = itemView.findViewById(R.id.tvRotuloPrincipalProdutoListaInsight);
            TextView tvResumoProdutoListaInsight = itemView.findViewById(R.id.tvResumoProdutoListaInsight);

            tvPosicaoProdutoListaInsight.setText((i + 1) + "º");
            tvNomeProdutoListaInsight.setText(item.getNomeProduto());
            tvCategoriaProdutoListaInsight.setText(item.getNomeCategoria());

            tvValorPrincipalProdutoListaInsight.setText(String.valueOf(item.getTotalListas()));
            tvRotuloPrincipalProdutoListaInsight.setText(
                    item.getTotalListas() == 1 ? "lista com este produto" : "listas com este produto"
            );

            tvResumoProdutoListaInsight.setText(
                    "Também soma " +
                            item.getTotalAdicoes() +
                            (item.getTotalAdicoes() == 1 ? " unidade adicionada" : " unidades adicionadas") +
                            " no total."
            );

            container.addView(itemView);
        }
    }

    private void mostrarDiaForteProduto(TendenciaBuscaProdutoModel item) {
        String diaForte = dbHelper.obterDiaFortePorProduto(item.getIdProduto(), periodoSelecionado);

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dia_forte_produto, null, false);

        TextView tvTituloSheetProduto = sheetView.findViewById(R.id.tvTituloSheetProduto);
        TextView tvCategoriaSheetProduto = sheetView.findViewById(R.id.tvCategoriaSheetProduto);
        TextView tvBuscasAtuaisSheetProduto = sheetView.findViewById(R.id.tvBuscasAtuaisSheetProduto);
        TextView tvBuscasAnterioresSheetProduto = sheetView.findViewById(R.id.tvBuscasAnterioresSheetProduto);
        TextView tvDiaForteSheetProduto = sheetView.findViewById(R.id.tvDiaForteSheetProduto);
        TextView tvResumoSheetProduto = sheetView.findViewById(R.id.tvResumoSheetProduto);

        tvTituloSheetProduto.setText(item.getNomeProduto());
        tvCategoriaSheetProduto.setText(item.getNomeCategoria());
        tvBuscasAtuaisSheetProduto.setText(
                item.getBuscasPeriodoAtual() +
                        (item.getBuscasPeriodoAtual() == 1 ? " busca" : " buscas")
        );
        tvBuscasAnterioresSheetProduto.setText(
                item.getBuscasPeriodoAnterior() +
                        (item.getBuscasPeriodoAnterior() == 1 ? " busca" : " buscas")
        );
        tvDiaForteSheetProduto.setText(diaForte);

        String nomePeriodo = "SEMANA".equals(periodoSelecionado) ? "semana" : "mês";
        String direcaoInteresse;
        if (item.isNovoNoRadar()) {
            direcaoInteresse = "chegou agora ao radar";
        } else if (item.isEmAlta()) {
            direcaoInteresse = "está em crescimento";
        } else {
            direcaoInteresse = "perdeu força";
        }

        tvResumoSheetProduto.setText(
                "Esse produto " +
                        direcaoInteresse +
                        " e concentrou mais interesse em " +
                        diaForte +
                        " dentro do recorte de " +
                        nomePeriodo +
                        "."
        );

        dialog.setContentView(sheetView);
        dialog.show();
    }
}