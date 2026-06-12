package com.example.listasmart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail, etNovaSenha, etConfirmarSenha;
    private Button btnRedefinir;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmailRecuperar);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarNovaSenha);
        btnRedefinir = findViewById(R.id.btnRedefinirSenha);

        btnRedefinir.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String novaSenha = etNovaSenha.getText().toString().trim();
            String confirmarSenha = etConfirmarSenha.getText().toString().trim();

            if (email.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else if (!novaSenha.equals(confirmarSenha)) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
            } else if (!dbHelper.verificarEmailExiste(email)) {
                Toast.makeText(this, "E-mail não cadastrado no sistema!", Toast.LENGTH_SHORT).show();
            } else {
                boolean sucesso = dbHelper.atualizarSenha(email, novaSenha);
                if (sucesso) {
                    Toast.makeText(this, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Retorna ao Login
                } else {
                    Toast.makeText(this, "Erro ao atualizar senha. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}