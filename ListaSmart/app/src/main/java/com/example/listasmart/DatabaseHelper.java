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

    public boolean verificarEmailExisteOutroUsuario(String email, int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuario WHERE email = ? AND id_usuario <> ?",
                new String[]{email, String.valueOf(idUsuario)}
        );
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
                    "SELECT u.id_usuario, u.nome, u.tipo_usuario, m.id_mercado, m.nome_mercado, m.imagem_uri " +
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
            String imagemMercado = cursor.getString(5);

            String primeiroNome = nomeCompleto.split(" ")[0];
            cursor.close();

            return new String[]{
                    idUsuario,
                    primeiroNome,
                    tipoUsuario != null ? tipoUsuario : "COMUM",
                    idMercado != null ? idMercado : "",
                    nomeMercado != null ? nomeMercado : "",
                    imagemMercado != null ? imagemMercado : ""
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

    public boolean cadastrarSupermercado(String nomeResponsavel, String email, String senha,
                                         String nomeMercado, String endereco, String imagemUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues usuarioValues = new ContentValues();
            usuarioValues.put("nome", nomeResponsavel);
            usuarioValues.put("email", email);
            usuarioValues.put("senha", senha);
            usuarioValues.put("tipo_usuario", "MERCADO");

            long idUsuario = db.insert("usuario", null, usuarioValues);
            if (idUsuario == -1) {
                return false;
            }

            ContentValues mercadoValues = new ContentValues();
            mercadoValues.put("id_usuario", idUsuario);
            mercadoValues.put("nome_mercado", nomeMercado);
            mercadoValues.put("endereco", endereco);
            mercadoValues.put("imagem_uri", imagemUri);

            long idMercado = db.insert("mercado", null, mercadoValues);
            if (idMercado == -1) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public boolean atualizarSupermercado(int idMercado, int idUsuario, String nomeResponsavel,
                                         String email, String senha, String nomeMercado,
                                         String endereco, String imagemUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues usuarioValues = new ContentValues();
            usuarioValues.put("nome", nomeResponsavel);
            usuarioValues.put("email", email);

            if (senha != null && !senha.trim().isEmpty()) {
                usuarioValues.put("senha", senha);
            }

            int usuarioAtualizado = db.update(
                    "usuario",
                    usuarioValues,
                    "id_usuario = ?",
                    new String[]{String.valueOf(idUsuario)}
            );

            ContentValues mercadoValues = new ContentValues();
            mercadoValues.put("nome_mercado", nomeMercado);
            mercadoValues.put("endereco", endereco);
            mercadoValues.put("imagem_uri", imagemUri);

            int mercadoAtualizado = db.update(
                    "mercado",
                    mercadoValues,
                    "id_mercado = ?",
                    new String[]{String.valueOf(idMercado)}
            );

            if (usuarioAtualizado <= 0 || mercadoAtualizado <= 0) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
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

    public java.util.List<MercadoAdminModel> listarSupermercadosAdmin() {
        java.util.List<MercadoAdminModel> lista = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT m.id_mercado, m.id_usuario, m.nome_mercado, m.endereco, m.imagem_uri, u.nome, u.email " +
                        "FROM mercado m " +
                        "JOIN usuario u ON u.id_usuario = m.id_usuario " +
                        "ORDER BY m.nome_mercado ASC",
                null
        );

        while (cursor.moveToNext()) {
            lista.add(new MercadoAdminModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(4)
            ));
        }

        cursor.close();
        return lista;
    }

    public int obterTotalMercadosCadastrados() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM mercado", null);

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int obterTotalConsumidoresCadastrados() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM usuario WHERE tipo_usuario = 'COMUM'",
                null
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public boolean mercadoPossuiRegistrosPreco(int idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM registro_preco WHERE id_mercado = ?",
                new String[]{String.valueOf(idMercado)}
        );

        boolean possuiRegistros = false;
        if (cursor.moveToFirst()) {
            possuiRegistros = cursor.getInt(0) > 0;
        }
        cursor.close();
        return possuiRegistros;
    }

    public boolean excluirSupermercado(int idMercado, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            if (mercadoPossuiRegistrosPreco(idMercado)) {
                return false;
            }

            db.delete("mercado", "id_mercado = ?", new String[]{String.valueOf(idMercado)});
            db.delete("usuario", "id_usuario = ?", new String[]{String.valueOf(idUsuario)});
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
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

    public int obterTotalPrecosPorMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM registro_preco WHERE id_mercado = ?",
                new String[]{idMercado}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int obterTotalCuponsPorMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM registro_preco WHERE id_mercado = ? AND tipo_registro = 'CUPOM'",
                new String[]{idMercado}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public String obterProdutoMaisPesquisado() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p.nome_produto, COUNT(*) AS total " +
                        "FROM historico_pesquisa h " +
                        "JOIN produto p ON p.id_produto = h.id_produto " +
                        "GROUP BY p.id_produto " +
                        "ORDER BY total DESC, p.nome_produto ASC " +
                        "LIMIT 1",
                null
        );

        String resultado = "Sem dados";
        if (cursor.moveToFirst()) {
            resultado = cursor.getString(0);
        }
        cursor.close();
        return resultado;
    }

    public String obterCategoriaMaisPesquisada() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.nome_categoria, COUNT(*) AS total " +
                        "FROM historico_pesquisa h " +
                        "JOIN produto p ON p.id_produto = h.id_produto " +
                        "JOIN categoria c ON c.id_categoria = p.id_categoria " +
                        "GROUP BY c.id_categoria " +
                        "ORDER BY total DESC, c.nome_categoria ASC " +
                        "LIMIT 1",
                null
        );

        String resultado = "Sem dados";
        if (cursor.moveToFirst()) {
            resultado = cursor.getString(0);
        }
        cursor.close();
        return resultado;
    }

    public String obterPosicaoRankingMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor mediaCursor = db.rawQuery(
                "SELECT AVG(preco) FROM registro_preco WHERE id_mercado = ?",
                new String[]{idMercado}
        );

        if (!mediaCursor.moveToFirst() || mediaCursor.isNull(0)) {
            mediaCursor.close();
            return "Sem ranking";
        }

        double mediaMercado = mediaCursor.getDouble(0);
        mediaCursor.close();

        Cursor rankingCursor = db.rawQuery(
                "SELECT COUNT(*) " +
                        "FROM (" +
                        "SELECT id_mercado " +
                        "FROM registro_preco " +
                        "GROUP BY id_mercado " +
                        "HAVING AVG(preco) < CAST(? AS REAL)" +
                        ")",
                new String[]{String.valueOf(mediaMercado)}
        );

        int posicao = 1;
        if (rankingCursor.moveToFirst()) {
            posicao = rankingCursor.getInt(0) + 1;
        }
        rankingCursor.close();

        return posicao + "º lugar";
    }

    public String obterMediaPrecoMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT AVG(preco) FROM registro_preco WHERE id_mercado = ?",
                new String[]{idMercado}
        );

        String resultado = "Sem dados";
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            resultado = String.format(java.util.Locale.getDefault(), "R$ %.2f", cursor.getDouble(0));
        }
        cursor.close();
        return resultado;
    }

    public String obterHistoricoMediaPrecoMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor datasCursor = db.rawQuery(
                "SELECT DISTINCT substr(data_registro, 1, 10) AS data_base " +
                        "FROM registro_preco " +
                        "WHERE id_mercado = ? " +
                        "ORDER BY data_base DESC " +
                        "LIMIT 5",
                new String[]{idMercado}
        );

        StringBuilder historico = new StringBuilder();

        while (datasCursor.moveToNext()) {
            String dataBase = datasCursor.getString(0);

            Cursor resumoCursor = db.rawQuery(
                    "SELECT AVG(ultimo_preco), COUNT(*) " +
                            "FROM (" +
                            "SELECT rp1.id_produto, rp1.preco AS ultimo_preco " +
                            "FROM registro_preco rp1 " +
                            "WHERE rp1.id_mercado = ? " +
                            "AND substr(rp1.data_registro, 1, 10) <= ? " +
                            "AND rp1.data_registro = (" +
                            "SELECT MAX(rp2.data_registro) " +
                            "FROM registro_preco rp2 " +
                            "WHERE rp2.id_mercado = rp1.id_mercado " +
                            "AND rp2.id_produto = rp1.id_produto " +
                            "AND substr(rp2.data_registro, 1, 10) <= ?" +
                            ")" +
                            ")",
                    new String[]{idMercado, dataBase, dataBase}
            );

            if (resumoCursor.moveToFirst() && !resumoCursor.isNull(0)) {
                double media = resumoCursor.getDouble(0);
                int quantidadeProdutos = resumoCursor.getInt(1);

                if (historico.length() > 0) {
                    historico.append("\n");
                }

                historico.append(formatarData(dataBase))
                        .append(" - ")
                        .append(String.format(java.util.Locale.getDefault(), "R$ %.2f", media))
                        .append(" - ")
                        .append(quantidadeProdutos)
                        .append(quantidadeProdutos == 1 ? " produto" : " produtos");
            }

            resumoCursor.close();
        }

        datasCursor.close();

        if (historico.length() == 0) {
            return "Sem histórico disponível.";
        }

        return historico.toString();
    }

    public String obterUltimosRegistrosMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p.nome_produto, rp.preco, rp.tipo_registro, substr(rp.data_registro, 1, 10) " +
                        "FROM registro_preco rp " +
                        "JOIN produto p ON p.id_produto = rp.id_produto " +
                        "WHERE rp.id_registro IN (" +
                        "    SELECT MAX(rp2.id_registro) " +
                        "    FROM registro_preco rp2 " +
                        "    WHERE rp2.id_mercado = ? " +
                        "    GROUP BY rp2.id_produto" +
                        ") " +
                        "ORDER BY rp.data_registro DESC " +
                        "LIMIT 4",
                new String[]{idMercado}
        );

        StringBuilder registros = new StringBuilder();

        while (cursor.moveToNext()) {
            String produto = cursor.getString(0);
            double preco = cursor.getDouble(1);
            String tipo = cursor.getString(2);
            String data = formatarData(cursor.getString(3));

            if (registros.length() > 0) {
                registros.append("\n\n");
            }

            registros.append(produto)
                    .append(" - ")
                    .append(String.format(java.util.Locale.getDefault(), "R$ %.2f", preco))
                    .append(" (")
                    .append(tipo)
                    .append(")")
                    .append("\n")
                    .append(data);
        }

        cursor.close();

        if (registros.length() == 0) {
            return "Sem registros recentes.";
        }

        return registros.toString();
    }

    private String formatarData(String dataOriginal) {
        if (dataOriginal == null || dataOriginal.length() < 10) {
            return dataOriginal != null ? dataOriginal : "";
        }

        String ano = dataOriginal.substring(0, 4);
        String mes = dataOriginal.substring(5, 7);
        String dia = dataOriginal.substring(8, 10);

        return dia + "/" + mes + "/" + ano;
    }
}