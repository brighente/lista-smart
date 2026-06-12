package com.example.listasmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminMarketsActivity extends AppCompatActivity {

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
        rvAdminMarkets = findViewById(R.id.rvAdminMarkets);

        rvAdminMarkets.setLayoutManager(new LinearLayoutManager(this));

        List<MercadoAdminModel> lista = dbHelper.listarSupermercadosAdmin();
        adapter = new AdminMarketsAdapter(
                lista,
                mercado -> {
                    Intent intent = new Intent(this, RegisterMarketActivity.class);
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

    private void recarregarLista() {
        adapter.atualizarLista(dbHelper.listarSupermercadosAdmin());
    }
}