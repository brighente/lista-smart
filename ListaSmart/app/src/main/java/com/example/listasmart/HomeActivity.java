package com.example.listasmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout layoutConteudoProdutos;
    private LinearLayout layoutAdmin;
    private LinearLayout layoutFiltrosProdutos;
    private TextView tvNomeUsuario;
    private TextView tvTotalMercadosAdmin;
    private TextView tvTotalConsumidoresAdmin;
    private Spinner spinnerMercado, spinnerCategoria;
    private RecyclerView rvProdutos;
    private Button btnAdminCadastrarMercado;
    private Button btnAdminGerenciarMercados;
    private Button btnNovaLista;
    private DatabaseHelper dbHelper;
    private ProdutoAdapter adapter;
    private String tipoUsuario;
    private String nomeMercado;
    private String userId;
    private int listaAtivaId = -1;
    private String listaAtivaNome = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        // Inicializar componentes do Menu Suspenso
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tipoUsuario = getIntent().getStringExtra("USER_TYPE");
        configurarMenuPorPerfil(navigationView);

        // Criar o botão animado de "três risquinhos" na Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Inicializar componentes da listagem de produtos
        layoutConteudoProdutos = findViewById(R.id.layout_conteudo_produtos);
        layoutAdmin = findViewById(R.id.layoutAdmin);
        layoutFiltrosProdutos = findViewById(R.id.layoutFiltrosProdutos);
        tvNomeUsuario = findViewById(R.id.tvNomeUsuario);
        tvTotalMercadosAdmin = findViewById(R.id.tvTotalMercadosAdmin);
        tvTotalConsumidoresAdmin = findViewById(R.id.tvTotalConsumidoresAdmin);
        spinnerMercado = findViewById(R.id.spinnerMercado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        rvProdutos = findViewById(R.id.rvProdutos);
        btnAdminCadastrarMercado = findViewById(R.id.btnAdminCadastrarMercado);
        btnAdminGerenciarMercados = findViewById(R.id.btnAdminGerenciarMercados);
        btnNovaLista = findViewById(R.id.btnNovaLista);

        // Recupera dados vindos da Intent de Login
        String primeiroNome = getIntent().getStringExtra("USER_NAME");
        nomeMercado = getIntent().getStringExtra("MARKET_NAME");
        userId = getIntent().getStringExtra("USER_ID");

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
        } else if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
            if (primeiroNome != null) {
                tvNomeUsuario.setText("Olá, " + primeiroNome);
            } else {
                tvNomeUsuario.setText("Administrador");
            }

            layoutAdmin.setVisibility(View.VISIBLE);
            layoutFiltrosProdutos.setVisibility(View.GONE);
            rvProdutos.setVisibility(View.GONE);

            atualizarResumoAdmin();

            btnAdminCadastrarMercado.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RegisterMarketActivity.class);
                intent.putExtra("USER_NAME", primeiroNome);
                startActivity(intent);
            });

            btnAdminGerenciarMercados.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AdminMarketsActivity.class);
                intent.putExtra("USER_NAME", primeiroNome);
                startActivity(intent);
            });
        } else {
            if (primeiroNome != null) {
                tvNomeUsuario.setText("Olá, " + primeiroNome);
            }

            layoutAdmin.setVisibility(View.GONE);
            layoutFiltrosProdutos.setVisibility(View.VISIBLE);
            rvProdutos.setVisibility(View.VISIBLE);

            configurarFiltros();
            rvProdutos.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new ProdutoAdapter(
                    dbHelper.obterProdutosFiltrados("Todos os Mercados", "Todas as Categorias"),
                    this::adicionarProdutoNaListaAtiva
            );
            rvProdutos.setAdapter(adapter);

            TextView tvListaAtiva = findViewById(R.id.tvListaAtiva);
            carregarUltimaListaAtiva(tvListaAtiva);
            btnNovaLista.setOnClickListener(v -> mostrarDialogCriarLista(tvListaAtiva));

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

    private void configurarMenuPorPerfil(NavigationView navigationView) {
        MenuItem itemInicio = navigationView.getMenu().findItem(R.id.nav_inicio);
        MenuItem itemDashboard = navigationView.getMenu().findItem(R.id.nav_dashboard);
        MenuItem itemMinhaLista = navigationView.getMenu().findItem(R.id.nav_minha_lista);
        MenuItem itemCadastrarMercado = navigationView.getMenu().findItem(R.id.nav_cadastrar_mercado);
        MenuItem itemListarMercados = navigationView.getMenu().findItem(R.id.nav_listar_mercados);

        if ("MERCADO".equalsIgnoreCase(tipoUsuario)) {
            itemInicio.setVisible(false);
            itemDashboard.setVisible(true);
            itemMinhaLista.setVisible(false);
            itemCadastrarMercado.setVisible(false);
            itemListarMercados.setVisible(false);
        } else if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
            itemInicio.setVisible(true);
            itemDashboard.setVisible(false);
            itemMinhaLista.setVisible(false);
            itemCadastrarMercado.setVisible(true);
            itemListarMercados.setVisible(true);
        } else {
            itemInicio.setVisible(true);
            itemDashboard.setVisible(false);
            itemMinhaLista.setVisible(true);
            itemCadastrarMercado.setVisible(false);
            itemListarMercados.setVisible(false);
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
            } else if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
                removerFragmentDoContainer();
                layoutConteudoProdutos.setVisibility(View.VISIBLE);
                layoutAdmin.setVisibility(View.VISIBLE);
                layoutFiltrosProdutos.setVisibility(View.GONE);
                rvProdutos.setVisibility(View.GONE);
            } else {
                removerFragmentDoContainer();
                layoutConteudoProdutos.setVisibility(View.VISIBLE);
                layoutAdmin.setVisibility(View.GONE);
                layoutFiltrosProdutos.setVisibility(View.VISIBLE);
                rvProdutos.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.nav_dashboard) {
            if ("MERCADO".equalsIgnoreCase(tipoUsuario)) {
                // Esconde os produtos e infla o Fragment do Dashboard
                layoutConteudoProdutos.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
            } else {
                removerFragmentDoContainer();
                layoutConteudoProdutos.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.nav_minha_lista) {
            if (!"ADMIN".equalsIgnoreCase(tipoUsuario) && !"MERCADO".equalsIgnoreCase(tipoUsuario)) {
                Intent intent = new Intent(HomeActivity.this, MinhaListaActivity.class);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("LISTA_ID", listaAtivaId);
                startActivity(intent);
            }
        } else if (id == R.id.nav_cadastrar_mercado) {
            if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
                Intent intent = new Intent(HomeActivity.this, RegisterMarketActivity.class);
                intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                startActivity(intent);
            }
        } else if (id == R.id.nav_listar_mercados) {
            if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
                Intent intent = new Intent(HomeActivity.this, AdminMarketsActivity.class);
                intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                startActivity(intent);
            }
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

    private void atualizarResumoAdmin() {
        if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
            tvTotalMercadosAdmin.setText(String.valueOf(dbHelper.obterTotalMercadosCadastrados()));
            tvTotalConsumidoresAdmin.setText(String.valueOf(dbHelper.obterTotalConsumidoresCadastrados()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarResumoAdmin();
        atualizarItemMenuSelecionado();
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

    private void mostrarDialogCriarLista(TextView tvListaAtiva) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nome da lista");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Nova Lista de Compras")
                .setView(input)
                .setPositiveButton("Criar", (dialog, which) -> {
                    String nomeLista = input.getText().toString().trim();

                    if (nomeLista.isEmpty()) {
                        android.widget.Toast.makeText(this, "Informe um nome para a lista", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (userId == null || userId.isEmpty()) {
                        android.widget.Toast.makeText(this, "Usuário inválido", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String dataCriacao = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            .format(new java.util.Date());

                    int idGerado = dbHelper.criarListaCompra(Integer.parseInt(userId), nomeLista, dataCriacao);

                    if (idGerado != -1) {
                        listaAtivaId = idGerado;
                        listaAtivaNome = nomeLista;
                        tvListaAtiva.setText("Lista ativa: " + listaAtivaNome);
                        android.widget.Toast.makeText(this, "Lista criada com sucesso", android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        android.widget.Toast.makeText(this, "Erro ao criar lista", android.widget.Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void adicionarProdutoNaListaAtiva(ProdutoModel produto) {
        if (listaAtivaId == -1) {
            android.widget.Toast.makeText(this, "Crie uma lista antes de adicionar produtos", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        boolean sucesso = dbHelper.adicionarProdutoNaLista(listaAtivaId, produto.getIdProduto(), 1);

        if (sucesso) {
            android.widget.Toast.makeText(this, produto.getNome() + " adicionado à lista", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            android.widget.Toast.makeText(this, "Erro ao adicionar produto", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarUltimaListaAtiva(TextView tvListaAtiva) {
        if (userId == null || userId.isEmpty()) {
            tvListaAtiva.setText("Nenhuma lista selecionada");
            return;
        }

        ListaCompraModel ultimaLista = dbHelper.obterUltimaListaUsuario(Integer.parseInt(userId));

        if (ultimaLista != null) {
            listaAtivaId = ultimaLista.getIdLista();
            listaAtivaNome = ultimaLista.getNomeLista();
            tvListaAtiva.setText("Lista ativa: " + listaAtivaNome);
        } else {
            tvListaAtiva.setText("Nenhuma lista selecionada");
        }
    }

    private void atualizarItemMenuSelecionado() {
        if (navigationView == null) {
            return;
        }

        if ("MERCADO".equalsIgnoreCase(tipoUsuario)) {
            navigationView.setCheckedItem(R.id.nav_dashboard);
        } else if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
            navigationView.setCheckedItem(R.id.nav_inicio);
        } else {
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }
}