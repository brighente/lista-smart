package com.example.listasmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private String databasePath;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
        copyDatabaseIfNotExist();
    }

    private void copyDatabaseIfNotExist() {
        java.io.File dbFile = new java.io.File(databasePath);
        if (!dbFile.exists()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyDatabase() throws IOException {
        InputStream input = context.getAssets().open(DATABASE_NAME);
        OutputStream output = new FileOutputStream(databasePath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // As tabelas já existem no arquivo do asset.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // --- MÉTODOS DE AUTENTICAÇÃO E CADASTRO ---

    // 1. Verificar Login
    public boolean verificarUsuario(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE email = ? AND senha = ?", new String[]{email, senha});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // 2. Verificar se Email existe
    public boolean verificarEmailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE email = ?", new String[]{email});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // 3. Cadastrar Usuário
    public boolean cadastrarUsuario(String nome, String email, String senha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("email", email);
        values.put("senha", senha);
        values.put("tipo_usuario", "COMUM"); // Default baseado no seu banco

        long resultado = db.insert("usuario", null, values);
        return resultado != -1;
    }

    // 4. Atualizar Senha (Esqueci a senha)
    public boolean atualizarSenha(String email, String novaSenha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("senha", novaSenha);

        int linhasAfetadas = db.update("usuario", values, "email = ?", new String[]{email});
        return linhasAfetadas > 0;
    }

    // Retorna o primeiro nome e o ID do usuário após login válido
    public String[] obterDadosUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.id_usuario, u.nome, u.tipo_usuario, m.id_mercado, m.nome_mercado " +
                        "FROM usuario u " +
                        "LEFT JOIN mercado m ON m.id_usuario = u.id_usuario " +
                        "WHERE u.email = ?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            String idUsuario = cursor.getString(0);
            String nomeCompleto = cursor.getString(1);
            String tipoUsuario = cursor.getString(2);
            String idMercado = cursor.getString(3);
            String nomeMercado = cursor.getString(4);

            String primeiroNome = nomeCompleto.split(" ")[0];
            cursor.close();

            return new String[]{
                    idUsuario,
                    primeiroNome,
                    tipoUsuario != null ? tipoUsuario : "COMUM",
                    idMercado != null ? idMercado : "",
                    nomeMercado != null ? nomeMercado : ""
            };
        }

        cursor.close();
        return null;
    }

    // Lista de Categorias para o Filtro
    public java.util.List<String> obterCategorias() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        lista.add("Todas as Categorias");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nome_categoria FROM categoria", null);
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0));
        }
        cursor.close();
        return lista;
    }

    // Lista de Mercados para o Filtro
    public java.util.List<String> obterMercados() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        lista.add("Todos os Mercados");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nome_mercado FROM mercado", null);
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0));
        }
        cursor.close();
        return lista;
    }

    // Busca os produtos aplicando os filtros dinamicamente
    public java.util.List<ProdutoModel> obterProdutosFiltrados(String mercadoSel, String categoriaSel) {
        java.util.List<ProdutoModel> lista = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Mudamos para LEFT JOIN para trazer TODOS os produtos, mesmo sem preço registrado
        StringBuilder query = new StringBuilder(
                "SELECT p.id_produto, p.nome_produto, p.imagem_uri, m.nome_mercado, rp.tipo_registro, rp.preco " +
                        "FROM produto p " +
                        "LEFT JOIN registro_preco rp ON p.id_produto = rp.id_produto " +
                        "LEFT JOIN mercado m ON rp.id_mercado = m.id_mercado " +
                        "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria WHERE 1=1"
        );

        java.util.List<String> args = new java.util.ArrayList<>();

        if (mercadoSel != null && !mercadoSel.equals("Todos os Mercados")) {
            query.append(" AND m.nome_mercado = ?");
            args.add(mercadoSel);
        }
        if (categoriaSel != null && !categoriaSel.equals("Todas as Categorias")) {
            query.append(" AND c.nome_categoria = ?");
            args.add(categoriaSel);
        }

        // Agrupa pelo ID do produto para não duplicar itens na tela
        query.append(" GROUP BY p.id_produto ORDER BY p.nome_produto ASC");

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nome = cursor.getString(1);
            String imagem = cursor.getString(2);

            // Se o mercado for nulo (produto sem preço), colocamos um texto padrão
            String mercado = cursor.getString(3) != null ? cursor.getString(3) : "Não informado";
            String tipo = cursor.getString(4) != null ? cursor.getString(4) : "MANUAL";
            double preco = cursor.getDouble(5); // vai retornar 0.0 se for nulo

            lista.add(new ProdutoModel(id, nome, imagem, mercado, tipo, preco));
        }
        cursor.close();
        return lista;
    }
}