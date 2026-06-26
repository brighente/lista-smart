package com.example.listasmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

public class AdminMarketsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView rvAdminMarkets;
    private DatabaseHelper dbHelper;
    private AdminMarketsAdapter adapter;

    private final ActivityResultLauncher<Intent> editarMercadoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                recarregarLista();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_markets);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_admin_markets);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_admin_markets);
        NavigationView navigationView = findViewById(R.id.nav_view_admin_markets);
        navigationView.setNavigationItemSelectedListener(this);
        configurarMenuAdmin(navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        rvAdminMarkets = findViewById(R.id.rvAdminMarkets);
        rvAdminMarkets.setLayoutManager(new LinearLayoutManager(this));

        List<MercadoAdminModel> lista = dbHelper.listarSupermercadosAdmin();
        adapter = new AdminMarketsAdapter(
                lista,
                mercado -> {
                    Intent intent = new Intent(this, RegisterMarketActivity.class);
                    intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                    intent.putExtra("MODO_EDICAO", true);
                    intent.putExtra("ID_MERCADO", mercado.getIdMercado());
                    intent.putExtra("ID_USUARIO", mercado.getIdUsuario());
                    intent.putExtra("NOME_RESPONSAVEL", mercado.getNomeResponsavel());
                    intent.putExtra("EMAIL", mercado.getEmail());
                    intent.putExtra("NOME_MERCADO", mercado.getNomeMercado());
                    intent.putExtra("ENDERECO", mercado.getEndereco());
                    intent.putExtra("IMAGEM_URI", mercado.getImagemUri());
                    editarMercadoLauncher.launch(intent);
                },
                mercado -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Excluir supermercado")
                            .setMessage("Deseja excluir " + mercado.getNomeMercado() + "?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                boolean sucesso = dbHelper.excluirSupermercado(
                                        mercado.getIdMercado(),
                                        mercado.getIdUsuario()
                                );

                                if (sucesso) {
                                    Toast.makeText(this, "Supermercado excluído com sucesso", Toast.LENGTH_SHORT).show();
                                    recarregarLista();
                                } else {
                                    Toast.makeText(this, "Não é possível excluir um mercado que já possui preços cadastrados", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
        );

        rvAdminMarkets.setAdapter(adapter);
    }

    private void configurarMenuAdmin(NavigationView navigationView) {
        MenuItem itemDashboard = navigationView.getMenu().findItem(R.id.nav_dashboard);
        MenuItem itemOportunidadesPreco = navigationView.getMenu().findItem(R.id.nav_oportunidades_preco);
        MenuItem itemInteligenciaBusca = navigationView.getMenu().findItem(R.id.nav_inteligencia_busca);
        MenuItem itemMinhaLista = navigationView.getMenu().findItem(R.id.nav_minha_lista);
        MenuItem itemCadastrarMercado = navigationView.getMenu().findItem(R.id.nav_cadastrar_mercado);
        MenuItem itemListarMercados = navigationView.getMenu().findItem(R.id.nav_listar_mercados);

        itemDashboard.setVisible(false);
        itemOportunidadesPreco.setVisible(false);
        itemInteligenciaBusca.setVisible(false);
        itemMinhaLista.setVisible(false);
        itemCadastrarMercado.setVisible(true);
        itemListarMercados.setVisible(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("USER_TYPE", "ADMIN");
            intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_cadastrar_mercado) {
            Intent intent = new Intent(this, RegisterMarketActivity.class);
            intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_listar_mercados) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_sair) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void recarregarLista() {
        adapter.atualizarLista(dbHelper.listarSupermercadosAdmin());
    }
}