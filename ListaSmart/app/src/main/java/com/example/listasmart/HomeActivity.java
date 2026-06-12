package com.example.listasmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private LinearLayout layoutConteudoProdutos;
    private TextView tvNomeUsuario;
    private Spinner spinnerMercado, spinnerCategoria;
    private RecyclerView rvProdutos;
    private DatabaseHelper dbHelper;
    private ProdutoAdapter adapter;
    private String tipoUsuario;
    private String nomeMercado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        // Inicializar componentes do Menu Suspenso
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Criar o botão animado de "três risquinhos" na Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Inicializar componentes da listagem de produtos
        layoutConteudoProdutos = findViewById(R.id.layout_conteudo_produtos);
        tvNomeUsuario = findViewById(R.id.tvNomeUsuario);
        spinnerMercado = findViewById(R.id.spinnerMercado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        rvProdutos = findViewById(R.id.rvProdutos);

        // Recupera dados vindos da Intent de Login
        String primeiroNome = getIntent().getStringExtra("USER_NAME");
        tipoUsuario = getIntent().getStringExtra("USER_TYPE");
        nomeMercado = getIntent().getStringExtra("MARKET_NAME");

        if ("MERCADO".equalsIgnoreCase(tipoUsuario)) {
            if (nomeMercado != null && !nomeMercado.isEmpty()) {
                tvNomeUsuario.setText(nomeMercado);
            } else {
                tvNomeUsuario.setText("Painel do Mercado");
            }

            layoutConteudoProdutos.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        } else {
            if (primeiroNome != null) {
                tvNomeUsuario.setText("Olá, " + primeiroNome);
            }

            // Configura os Filtros e Grade de Produtos
            configurarFiltros();
            rvProdutos.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new ProdutoAdapter(dbHelper.obterProdutosFiltrados("Todos os Mercados", "Todas as Categorias"));
            rvProdutos.setAdapter(adapter);

            // Listener dos Filtros
            AdapterView.OnItemSelectedListener filtroListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filtrar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            };

            spinnerMercado.setOnItemSelectedListener(filtroListener);
            spinnerCategoria.setOnItemSelectedListener(filtroListener);
        }
    }

    // Gerencia os cliques nos itens do Menu Suspenso
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            if ("MERCADO".equalsIgnoreCase(tipoUsuario)) {
                layoutConteudoProdutos.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
            } else {
                // Volta para a tela de produtos, removendo qualquer Fragment por cima
                removerFragmentDoContainer();
                layoutConteudoProdutos.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.nav_dashboard) {
            // Esconde os produtos e infla o Fragment do Dashboard
            layoutConteudoProdutos.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        } else if (id == R.id.nav_sair) {
            // Executa o fluxo de logout que limpa a pilha e volta para a tela de Login
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        // Fecha o menu lateral automaticamente após o clique
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void removerFragmentDoContainer() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        // AJUSTADO: Lógica simplificada para verificar se o fragmento atual é o DashboardFragment
        if (currentFragment instanceof DashboardFragment) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        }
    }

    // Controla o botão físico "voltar" do celular para fechar o menu se ele estiver aberto
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configurarFiltros() {
        List<String> mercados = dbHelper.obterMercados();
        // CORRIGIDO: Agora usa o nosso R.layout.spinner_item customizado com texto amarelo
        ArrayAdapter<String> adapterMercado = new ArrayAdapter<>(this, R.layout.spinner_item, mercados);
        adapterMercado.setDropDownViewResource(R.layout.spinner_item); // Aplica o mesmo visual na lista suspensa
        spinnerMercado.setAdapter(adapterMercado);

        List<String> categorias = dbHelper.obterCategorias();
        // CORRIGIDO: Agora usa o nosso R.layout.spinner_item customizado com texto amarelo
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, R.layout.spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(R.layout.spinner_item); // Aplica o mesmo visual na lista suspensa
        spinnerCategoria.setAdapter(adapterCategoria);
    }

    private void filtrar() {
        String mercadoSel = spinnerMercado.getSelectedItem().toString();
        String categoriaSel = spinnerCategoria.getSelectedItem().toString();

        List<ProdutoModel> listaFiltrada = dbHelper.obterProdutosFiltrados(mercadoSel, categoriaSel);
        adapter.atualizarLista(listaFiltrada);
    }
}