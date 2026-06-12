package com.example.listasmart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterMarketActivity extends AppCompatActivity {

    private EditText etNomeResponsavel;
    private EditText etEmail;
    private EditText etSenha;
    private EditText etNomeMercado;
    private EditText etEndereco;
    private EditText etImagemUri;
    private Button btnSelecionarImagem;
    private Button btnCadastrarMercado;
    private ImageView ivPreviewMercado;
    private DatabaseHelper dbHelper;
    private String imagemUriSelecionada = "";

    private boolean modoEdicao = false;
    private int idMercado = 0;
    private int idUsuario = 0;

    private final ActivityResultLauncher<String[]> selecionarImagemLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);

                    imagemUriSelecionada = uri.toString();
                    etImagemUri.setText(imagemUriSelecionada);
                    ivPreviewMercado.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_market);

        dbHelper = new DatabaseHelper(this);

        etNomeResponsavel = findViewById(R.id.etNomeResponsavel);
        etEmail = findViewById(R.id.etEmailMercado);
        etSenha = findViewById(R.id.etSenhaMercado);
        etNomeMercado = findViewById(R.id.etNomeMercado);
        etEndereco = findViewById(R.id.etEnderecoMercado);
        etImagemUri = findViewById(R.id.etImagemMercado);
        btnSelecionarImagem = findViewById(R.id.btnSelecionarImagem);
        btnCadastrarMercado = findViewById(R.id.btnCadastrarMercado);
        ivPreviewMercado = findViewById(R.id.ivPreviewMercado);

        modoEdicao = getIntent().getBooleanExtra("MODO_EDICAO", false);
        idMercado = getIntent().getIntExtra("ID_MERCADO", 0);
        idUsuario = getIntent().getIntExtra("ID_USUARIO", 0);

        if (modoEdicao) {
            TextView tvTitulo = findViewById(R.id.tvTituloCadastroMercado);
            tvTitulo.setText("Editar Supermercado");
            btnCadastrarMercado.setText("Salvar alterações");

            etNomeResponsavel.setText(getIntent().getStringExtra("NOME_RESPONSAVEL"));
            etEmail.setText(getIntent().getStringExtra("EMAIL"));
            etNomeMercado.setText(getIntent().getStringExtra("NOME_MERCADO"));
            etEndereco.setText(getIntent().getStringExtra("ENDERECO"));

            imagemUriSelecionada = getIntent().getStringExtra("IMAGEM_URI");
            if (imagemUriSelecionada == null) {
                imagemUriSelecionada = "";
            }

            etImagemUri.setText(imagemUriSelecionada);

            if (!imagemUriSelecionada.isEmpty()) {
                ivPreviewMercado.setImageURI(Uri.parse(imagemUriSelecionada));
            }

            etSenha.setHint("Nova senha (opcional)");
        }

        btnSelecionarImagem.setOnClickListener(v ->
                selecionarImagemLauncher.launch(new String[]{"image/*"})
        );

        btnCadastrarMercado.setOnClickListener(v -> {
            String nomeResponsavel = etNomeResponsavel.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();
            String nomeMercado = etNomeMercado.getText().toString().trim();
            String endereco = etEndereco.getText().toString().trim();

            if (nomeResponsavel.isEmpty() || email.isEmpty()
                    || nomeMercado.isEmpty() || endereco.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!modoEdicao && senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (modoEdicao) {
                if (dbHelper.verificarEmailExisteOutroUsuario(email, idUsuario)) {
                    Toast.makeText(this, "Este e-mail já está cadastrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean sucesso = dbHelper.atualizarSupermercado(
                        idMercado,
                        idUsuario,
                        nomeResponsavel,
                        email,
                        senha,
                        nomeMercado,
                        endereco,
                        imagemUriSelecionada
                );

                if (sucesso) {
                    Toast.makeText(this, "Supermercado atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao atualizar supermercado", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dbHelper.verificarEmailExiste(email)) {
                    Toast.makeText(this, "Este e-mail já está cadastrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean sucesso = dbHelper.cadastrarSupermercado(
                        nomeResponsavel,
                        email,
                        senha,
                        nomeMercado,
                        endereco,
                        imagemUriSelecionada
                );

                if (sucesso) {
                    Toast.makeText(this, "Supermercado cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao cadastrar supermercado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}