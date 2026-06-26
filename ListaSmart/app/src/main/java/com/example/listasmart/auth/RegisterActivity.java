package com.example.listasmart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btnCadastrar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etNome = findViewById(R.id.etNomeCadastro);
        etEmail = findViewById(R.id.etEmailCadastro);
        etSenha = findViewById(R.id.etSenhaCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.verificarEmailExiste(email)) {
                Toast.makeText(this, "Este e-mail já está cadastrado!", Toast.LENGTH_SHORT).show();
            } else {
                boolean sucesso = dbHelper.cadastrarUsuario(nome, email, senha);
                if (sucesso) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a tela de registro e volta para o Login
                } else {
                    Toast.makeText(this, "Erro ao cadastrar usuário.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}