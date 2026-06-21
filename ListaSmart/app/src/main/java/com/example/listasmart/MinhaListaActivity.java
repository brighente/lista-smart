package com.example.listasmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class MinhaListaActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId = -1;
    private int listaId = -1;

    private TextView tvNomeLista;
    private TextView tvResumoLista;
    private TextView tvComparacaoVazia;
    private LinearLayout llResultadosComparacao;
    private RecyclerView rvItensLista;
    private Spinner spinnerListasUsuario;
    private Button btnBuscarMelhorPreco;

    private MinhaListaAdapter adapter;
    private ListaCompraModel listaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_lista);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbarMinhaLista);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvNomeLista = findViewById(R.id.tvNomeLista);
        tvResumoLista = findViewById(R.id.tvResumoLista);
        tvComparacaoVazia = findViewById(R.id.tvComparacaoVazia);
        llResultadosComparacao = findViewById(R.id.llResultadosComparacao);
        rvItensLista = findViewById(R.id.rvItensLista);
        spinnerListasUsuario = findViewById(R.id.spinnerListasUsuario);
        btnBuscarMelhorPreco = findViewById(R.id.btnBuscarMelhorPreco);

        String userIdExtra = getIntent().getStringExtra("USER_ID");
        listaId = getIntent().getIntExtra("LISTA_ID", -1);

        if (userIdExtra != null && !userIdExtra.isEmpty()) {
            userId = Integer.parseInt(userIdExtra);
        }

        if (listaId != -1) {
            listaAtual = dbHelper.obterListaPorId(listaId);
        }

        if (listaAtual == null && userId != -1) {
            listaAtual = dbHelper.obterUltimaListaUsuario(userId);
            if (listaAtual != null) {
                listaId = listaAtual.getIdLista();
            }
        }

        rvItensLista.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MinhaListaAdapter(new java.util.ArrayList<>(), new MinhaListaAdapter.OnItemListaActionListener() {
            @Override
            public void onAumentar(ItemListaCompraModel item) {
                dbHelper.atualizarQuantidadeItemLista(item.getIdItem(), item.getQuantidade() + 1);
                carregarItens();
            }

            @Override
            public void onDiminuir(ItemListaCompraModel item) {
                int novaQuantidade = item.getQuantidade() - 1;

                if (novaQuantidade <= 0) {
                    dbHelper.removerItemDaLista(item.getIdItem());
                } else {
                    dbHelper.atualizarQuantidadeItemLista(item.getIdItem(), novaQuantidade);
                }

                carregarItens();
            }

            @Override
            public void onRemover(ItemListaCompraModel item) {
                dbHelper.removerItemDaLista(item.getIdItem());
                carregarItens();
            }
        });
        rvItensLista.setAdapter(adapter);

        btnBuscarMelhorPreco.setOnClickListener(v -> carregarComparacao());

        configurarSpinnerListas();
        carregarItens();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void carregarItens() {
        if (listaAtual == null) {
            tvNomeLista.setText("Nenhuma lista encontrada");
            tvResumoLista.setText("Crie uma lista na tela inicial para começar");
            adapter.atualizarLista(new java.util.ArrayList<>());
            btnBuscarMelhorPreco.setEnabled(false);
            return;
        }

        List<ItemListaCompraModel> itens = dbHelper.obterItensDaLista(listaAtual.getIdLista());

        tvNomeLista.setText(listaAtual.getNomeLista());
        tvResumoLista.setText(itens.size() + (itens.size() == 1 ? " produto diferente na lista" : " produtos diferentes na lista"));
        adapter.atualizarLista(itens);
        btnBuscarMelhorPreco.setEnabled(!itens.isEmpty());

        llResultadosComparacao.removeAllViews();
        tvComparacaoVazia.setText("A comparação entre mercados aparecerá aqui.");
    }

    private void configurarSpinnerListas() {
        if (userId == -1) {
            return;
        }

        java.util.List<ListaCompraModel> listas = dbHelper.listarListasUsuario(userId);
        java.util.List<String> nomesListas = new java.util.ArrayList<>();
        int posicaoSelecionada = 0;

        for (int i = 0; i < listas.size(); i++) {
            nomesListas.add(listas.get(i).getNomeLista());

            if (listaAtual != null && listas.get(i).getIdLista() == listaAtual.getIdLista()) {
                posicaoSelecionada = i;
            }
        }

        ArrayAdapter<String> adapterListas = new ArrayAdapter<>(this, R.layout.spinner_item, nomesListas);
        adapterListas.setDropDownViewResource(R.layout.spinner_item);
        spinnerListasUsuario.setAdapter(adapterListas);

        if (!listas.isEmpty()) {
            spinnerListasUsuario.setSelection(posicaoSelecionada);
        }

        spinnerListasUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listaAtual = listas.get(position);
                listaId = listaAtual.getIdLista();
                carregarItens();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void carregarComparacao() {
        if (listaAtual == null) {
            Toast.makeText(this, "Nenhuma lista encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ResultadoComparacaoMercadoModel> resultados = dbHelper.obterComparacaoMercadosPorLista(listaAtual.getIdLista());

        llResultadosComparacao.removeAllViews();

        if (resultados.isEmpty()) {
            tvComparacaoVazia.setText("Você não consegue comprar essa lista completa em nenhum dos mercados do aplicativo.");
            return;
        }

        tvComparacaoVazia.setText("Mercados ordenados do mais barato para o mais caro.");

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < resultados.size(); i++) {
            ResultadoComparacaoMercadoModel item = resultados.get(i);
            View view = inflater.inflate(R.layout.item_resultado_comparacao, llResultadosComparacao, false);

            TextView tvNomeMercadoComparacao = view.findViewById(R.id.tvNomeMercadoComparacao);
            TextView tvTotalMercadoComparacao = view.findViewById(R.id.tvTotalMercadoComparacao);
            TextView tvPercentualMercadoComparacao = view.findViewById(R.id.tvPercentualMercadoComparacao);

            tvNomeMercadoComparacao.setText((i + 1) + "º " + item.getNomeMercado());
            tvTotalMercadoComparacao.setText(String.format(Locale.getDefault(), "R$ %.2f", item.getTotalLista()));

            if (i == 0) {
                tvPercentualMercadoComparacao.setText("Melhor opção");
            } else {
                tvPercentualMercadoComparacao.setText(
                        String.format(Locale.getDefault(), "+%.1f%% mais caro que o melhor preço", item.getPercentualAcimaMaisBarato())
                );
            }

            llResultadosComparacao.addView(view);
        }
    }
}