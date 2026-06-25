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
import java.util.List;
import java.util.Locale;

public class OportunidadesPrecoFragment extends Fragment {

    private DatabaseHelper dbHelper;

    public OportunidadesPrecoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oportunidades_preco, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        TextView tvTituloOportunidades = view.findViewById(R.id.tvTituloOportunidades);
        TextView tvResumoOportunidades = view.findViewById(R.id.tvResumoOportunidades);
        TextView tvCestaInteresseVazia = view.findViewById(R.id.tvCestaInteresseVazia);
        TextView tvProdutosVencedoresVazio = view.findViewById(R.id.tvProdutosVencedoresVazio);
        TextView tvOportunidadesLiderVazio = view.findViewById(R.id.tvOportunidadesLiderVazio);
        LinearLayout llCestaInteresse = view.findViewById(R.id.llCestaInteresse);
        LinearLayout llProdutosVencedores = view.findViewById(R.id.llProdutosVencedores);
        LinearLayout llOportunidadesLider = view.findViewById(R.id.llOportunidadesLider);

        String marketId = requireActivity().getIntent().getStringExtra("MARKET_ID");
        String marketName = requireActivity().getIntent().getStringExtra("MARKET_NAME");

        if (marketName != null && !marketName.trim().isEmpty()) {
            tvTituloOportunidades.setText("Oportunidades de " + marketName);
        }

        if (marketId == null || marketId.trim().isEmpty()) {
            tvResumoOportunidades.setText("Não foi possível identificar o mercado para gerar as oportunidades.");
            renderizarProdutos(llCestaInteresse, tvCestaInteresseVazia, new java.util.ArrayList<>(), true);
            renderizarProdutos(llProdutosVencedores, tvProdutosVencedoresVazio, new java.util.ArrayList<>(), false);
            renderizarProdutos(llOportunidadesLider, tvOportunidadesLiderVazio, new java.util.ArrayList<>(), false);
            return view;
        }

        List<OportunidadePrecoProdutoModel> cestaInteresse = dbHelper.obterCestaInteresseMercado(marketId, 6);
        List<OportunidadePrecoProdutoModel> produtosVencedores = dbHelper.obterProdutosVencedoresMercado(marketId, 6);
        List<OportunidadePrecoProdutoModel> oportunidadesLider = dbHelper.obterOportunidadesVirarLiderMercado(marketId, 6);

        int produtosComparaveisNaCesta = 0;
        int produtosVencidosNaCesta = 0;

        for (OportunidadePrecoProdutoModel item : cestaInteresse) {
            if (item.isPossuiBaseComparavel()) {
                produtosComparaveisNaCesta++;
            }

            if (item.isMercadoVence()) {
                produtosVencidosNaCesta++;
            }
        }

        if (cestaInteresse.isEmpty()) {
            tvResumoOportunidades.setText("Ainda não há buscas suficientes no app para montar a cesta de interesse.");
        } else if (produtosComparaveisNaCesta == 0) {
            tvResumoOportunidades.setText("Os produtos mais buscados já estão mapeados, mas ainda falta base comparável para medir a competitividade do seu mercado.");
        } else {
            tvResumoOportunidades.setText(
                    "Na cesta de interesse do app, seu mercado lidera em " +
                            produtosVencidosNaCesta +
                            (produtosVencidosNaCesta == 1 ? " item" : " itens") +
                            " entre " +
                            produtosComparaveisNaCesta +
                            (produtosComparaveisNaCesta == 1 ? " produto comparável." : " produtos comparáveis.")
            );
        }

        renderizarProdutos(llCestaInteresse, tvCestaInteresseVazia, cestaInteresse, true);
        renderizarProdutos(llProdutosVencedores, tvProdutosVencedoresVazio, produtosVencedores, false);
        tvOportunidadesLiderVazio.setText("Sem oportunidades imediatas para ganhar a lideranca agora.");
        renderizarProdutos(llOportunidadesLider, tvOportunidadesLiderVazio, oportunidadesLider, false);

        return view;
    }

    private void renderizarProdutos(
            LinearLayout container,
            TextView vazio,
            List<OportunidadePrecoProdutoModel> produtos,
            boolean exibirBuscas
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
            View itemView = inflater.inflate(R.layout.item_oportunidade_preco, container, false);

            TextView tvNomeProdutoOportunidade = itemView.findViewById(R.id.tvNomeProdutoOportunidade);
            TextView tvStatusOportunidade = itemView.findViewById(R.id.tvStatusOportunidade);
            TextView tvMetaOportunidade = itemView.findViewById(R.id.tvMetaOportunidade);
            TextView tvPrecoMercadoOportunidade = itemView.findViewById(R.id.tvPrecoMercadoOportunidade);
            TextView tvReferenciaOportunidade = itemView.findViewById(R.id.tvReferenciaOportunidade);
            TextView tvResumoStatusOportunidade = itemView.findViewById(R.id.tvResumoStatusOportunidade);

            tvNomeProdutoOportunidade.setText(item.getNomeProduto());
            tvStatusOportunidade.setText(item.getStatusLabel());

            if (exibirBuscas) {
                tvMetaOportunidade.setText(
                        item.getNomeCategoria() +
                                " • " +
                                item.getTotalBuscas() +
                                (item.getTotalBuscas() == 1 ? " busca no app" : " buscas no app")
                );
            } else {
                tvMetaOportunidade.setText(
                        item.getNomeCategoria() +
                                " • " +
                                item.getTotalBuscas() +
                                (item.getTotalBuscas() == 1 ? " busca relacionada" : " buscas relacionadas")
                );
            }

            if (item.isMercadoTemPreco() && item.getPrecoMercado() != null) {
                tvPrecoMercadoOportunidade.setText(
                        "Seu preço atual: " +
                                String.format(Locale.getDefault(), "R$ %.2f", item.getPrecoMercado())
                );
            } else {
                tvPrecoMercadoOportunidade.setText("Seu mercado ainda não tem preço atual para este produto.");
            }

            if (!item.isPossuiBaseComparavel() || item.getMenorPreco() == null) {
                tvReferenciaOportunidade.setText("Ainda não existem pelo menos 2 mercados com preço atual para comparar este produto.");
            } else if (item.isMercadoVence()) {
                if (item.isEmpateNoMenorPreco()) {
                    tvReferenciaOportunidade.setText(
                            "Menor preço compartilhado com " +
                                    item.getMercadosNoMenorPreco() +
                                    (item.getMercadosNoMenorPreco() == 1 ? " mercado." : " mercados.")
                    );
                } else {
                    tvReferenciaOportunidade.setText(
                            "Seu mercado lidera sozinho com " +
                                    String.format(Locale.getDefault(), "R$ %.2f", item.getMenorPreco()) +
                                    "."
                    );
                }
            } else {
                tvReferenciaOportunidade.setText(
                        "Melhor preço atual: " +
                                String.format(Locale.getDefault(), "R$ %.2f", item.getMenorPreco()) +
                                " em " +
                                item.getMercadoReferencia() +
                                "."
                );
            }

            if (!item.isMercadoTemPreco()) {
                tvResumoStatusOportunidade.setText("Sem acao competitiva possivel por enquanto, porque o mercado ainda nao cadastrou um preco atual.");
            } else if (!item.isPossuiBaseComparavel()) {
                tvResumoStatusOportunidade.setText("O produto já está no radar do app, mas ainda falta comparação suficiente entre mercados.");
            } else if (item.isMercadoVence()) {
                tvResumoStatusOportunidade.setText("Esse produto já reforça a competitividade do seu mercado na cesta de interesse.");
            } else {
                tvResumoStatusOportunidade.setText(
                        "Faltam " +
                                String.format(Locale.getDefault(), "R$ %.2f", item.getDiferencaParaLider()) +
                                " para seu mercado alcançar o menor preço atual."
                );
            }

            aplicarStatusVisual(tvStatusOportunidade, item);
            container.addView(itemView);
        }
    }

    private void aplicarStatusVisual(TextView statusView, OportunidadePrecoProdutoModel item) {
        int backgroundRes;
        int textColor;

        if (!item.isMercadoTemPreco()) {
            backgroundRes = R.drawable.bg_chip_neutral;
            textColor = ContextCompat.getColor(requireContext(), R.color.text_light);
        } else if (!item.isPossuiBaseComparavel()) {
            backgroundRes = R.drawable.bg_chip_warning;
            textColor = ContextCompat.getColor(requireContext(), R.color.brand_dark_gray);
        } else if (item.isMercadoVence()) {
            backgroundRes = R.drawable.bg_chip_success;
            textColor = ContextCompat.getColor(requireContext(), R.color.white);
        } else {
            backgroundRes = R.drawable.bg_chip_warning;
            textColor = ContextCompat.getColor(requireContext(), R.color.brand_dark_gray);
        }

        statusView.setBackgroundResource(backgroundRes);
        statusView.setTextColor(textColor);
    }
}
