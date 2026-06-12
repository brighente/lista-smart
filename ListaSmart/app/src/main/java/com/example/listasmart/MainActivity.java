package com.example.listasmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnEntrar, btnIrRegistrar;
    private TextView tvEsqueciSenha;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmailLogin);
        etSenha = findViewById(R.id.etSenhaLogin);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnIrRegistrar = findViewById(R.id.btnIrRegistrar);
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);

        // Ação de Logar
        btnEntrar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if(email.isEmpty() || senha.isEmpty()){
                Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                if(dbHelper.verificarUsuario(email, senha)){
                    Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    // BUSCA OS DADOS DO USUÁRIO LOGADO
                    String[] dadosUser = dbHelper.obterDadosUsuario(email);

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    if (dadosUser != null) {
                        intent.putExtra("USER_ID", dadosUser[0]);
                        intent.putExtra("USER_NAME", dadosUser[1]);
                        intent.putExtra("USER_TYPE", dadosUser[2]);
                        intent.putExtra("MARKET_ID", dadosUser[3]);
                        intent.putExtra("MARKET_NAME", dadosUser[4]);
                    }
                    startActivity(intent);
                    finish(); // Impede o usuário de voltar para o login ao apertar o botão "voltar" do celular
                } else {
                    Toast.makeText(MainActivity.this, "E-mail ou Senha incorretos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ir para Tela de Registro
        btnIrRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Ir para Tela de Esqueci a Senha
        tvEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}