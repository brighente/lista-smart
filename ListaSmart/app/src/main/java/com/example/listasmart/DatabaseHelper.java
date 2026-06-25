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
    // Busca os produtos aplicando os filtros dinamicamente
    public java.util.List<ProdutoModel> obterProdutosFiltrados(String mercadoSel, String categoriaSel) {
        java.util.List<ProdutoModel> lista = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder();
        java.util.List<String> args = new java.util.ArrayList<>();

        if (mercadoSel != null && !mercadoSel.equals("Todos os Mercados")) {
            query.append(
                    "SELECT p.id_produto, p.nome_produto, p.imagem_uri, " +
                            "COALESCE(m.nome_mercado, '").append("Não informado").append("') AS nome_mercado, " +
                            "COALESCE(rp.tipo_registro, 'MANUAL') AS tipo_registro, " +
                            "COALESCE(rp.preco, 0) AS preco " +
                            "FROM produto p " +
                            "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                            "LEFT JOIN mercado m ON m.nome_mercado = ? " +
                            "LEFT JOIN registro_preco rp ON rp.id_registro = (" +
                            "    SELECT rp2.id_registro " +
                            "    FROM registro_preco rp2 " +
                            "    WHERE rp2.id_produto = p.id_produto " +
                            "      AND rp2.id_mercado = m.id_mercado " +
                            "    ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                            "    LIMIT 1" +
                            ") " +
                            "WHERE 1=1 "
            );
            args.add(mercadoSel);
        } else {
            query.append(
                    "SELECT p.id_produto, p.nome_produto, p.imagem_uri, " +
                            "COALESCE(( " +
                            "    SELECT m.nome_mercado " +
                            "    FROM registro_preco rp " +
                            "    JOIN mercado m ON m.id_mercado = rp.id_mercado " +
                            "    WHERE rp.id_registro = ( " +
                            "        SELECT rp2.id_registro " +
                            "        FROM registro_preco rp2 " +
                            "        WHERE rp2.id_produto = rp.id_produto " +
                            "          AND rp2.id_mercado = rp.id_mercado " +
                            "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                            "        LIMIT 1 " +
                            "    ) " +
                            "      AND rp.id_produto = p.id_produto " +
                            "    ORDER BY rp.preco ASC, rp.data_registro DESC, rp.id_registro DESC " +
                            "    LIMIT 1 " +
                            "), 'Não informado') AS nome_mercado, " +
                            "COALESCE(( " +
                            "    SELECT rp.tipo_registro " +
                            "    FROM registro_preco rp " +
                            "    WHERE rp.id_registro = ( " +
                            "        SELECT rp2.id_registro " +
                            "        FROM registro_preco rp2 " +
                            "        WHERE rp2.id_produto = rp.id_produto " +
                            "          AND rp2.id_mercado = rp.id_mercado " +
                            "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                            "        LIMIT 1 " +
                            "    ) " +
                            "      AND rp.id_produto = p.id_produto " +
                            "    ORDER BY rp.preco ASC, rp.data_registro DESC, rp.id_registro DESC " +
                            "    LIMIT 1 " +
                            "), 'MANUAL') AS tipo_registro, " +
                            "COALESCE(( " +
                            "    SELECT rp.preco " +
                            "    FROM registro_preco rp " +
                            "    WHERE rp.id_registro = ( " +
                            "        SELECT rp2.id_registro " +
                            "        FROM registro_preco rp2 " +
                            "        WHERE rp2.id_produto = rp.id_produto " +
                            "          AND rp2.id_mercado = rp.id_mercado " +
                            "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                            "        LIMIT 1 " +
                            "    ) " +
                            "      AND rp.id_produto = p.id_produto " +
                            "    ORDER BY rp.preco ASC, rp.data_registro DESC, rp.id_registro DESC " +
                            "    LIMIT 1 " +
                            "), 0) AS preco " +
                            "FROM produto p " +
                            "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                            "WHERE 1=1 "
            );
        }

        if (categoriaSel != null && !categoriaSel.equals("Todas as Categorias")) {
            query.append(" AND c.nome_categoria = ? ");
            args.add(categoriaSel);
        }

        query.append(" ORDER BY p.nome_produto ASC");

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nome = cursor.getString(1);
            String imagem = cursor.getString(2);
            String mercado = cursor.getString(3) != null ? cursor.getString(3) : "Não informado";
            String tipo = cursor.getString(4) != null ? cursor.getString(4) : "MANUAL";
            double preco = cursor.isNull(5) ? 0.0 : cursor.getDouble(5);

            lista.add(new ProdutoModel(id, nome, imagem, mercado, tipo, preco));
        }

        cursor.close();
        return lista;
    }

    public int obterTotalProdutosComPrecoPorMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(DISTINCT id_produto) FROM registro_preco WHERE id_mercado = ?",
                new String[]{idMercado}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int obterTotalRegistrosPrecoPorMercado(String idMercado) {
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

    public int obterTotalManuaisPorMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM registro_preco WHERE id_mercado = ? AND tipo_registro = 'MANUAL'",
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

    public java.util.List<OportunidadePrecoProdutoModel> obterCestaInteresseMercado(String idMercado, int limite) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<OportunidadePrecoProdutoModel> lista = new java.util.ArrayList<>();
        int limiteNormalizado = Math.max(1, limite);

        Cursor cursor = db.rawQuery(
                "WITH top_buscas AS ( " +
                        "    SELECT h.id_produto, COUNT(*) AS total_buscas " +
                        "    FROM historico_pesquisa h " +
                        "    GROUP BY h.id_produto " +
                        "    ORDER BY total_buscas DESC, h.id_produto ASC " +
                        "    LIMIT " + limiteNormalizado +
                        "), ultimos_precos AS ( " +
                        "    SELECT rp.id_produto, rp.id_mercado, rp.preco " +
                        "    FROM registro_preco rp " +
                        "    WHERE rp.id_registro = ( " +
                        "        SELECT rp2.id_registro " +
                        "        FROM registro_preco rp2 " +
                        "        WHERE rp2.id_produto = rp.id_produto " +
                        "          AND rp2.id_mercado = rp.id_mercado " +
                        "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                        "        LIMIT 1 " +
                        "    ) " +
                        "), metricas AS ( " +
                        "    SELECT tb.id_produto, p.nome_produto, COALESCE(c.nome_categoria, 'Sem categoria') AS nome_categoria, tb.total_buscas, " +
                        "           MAX(CASE WHEN up.id_mercado = ? THEN up.preco END) AS preco_mercado, " +
                        "           MIN(up.preco) AS menor_preco, " +
                        "           COUNT(up.id_mercado) AS mercados_com_preco, " +
                        "           SUM(CASE " +
                        "                   WHEN up.preco = ( " +
                        "                       SELECT MIN(up2.preco) " +
                        "                       FROM ultimos_precos up2 " +
                        "                       WHERE up2.id_produto = tb.id_produto " +
                        "                   ) THEN 1 " +
                        "                   ELSE 0 " +
                        "               END) AS mercados_no_menor_preco " +
                        "    FROM top_buscas tb " +
                        "    JOIN produto p ON p.id_produto = tb.id_produto " +
                        "    LEFT JOIN categoria c ON c.id_categoria = p.id_categoria " +
                        "    LEFT JOIN ultimos_precos up ON up.id_produto = tb.id_produto " +
                        "    GROUP BY tb.id_produto, p.nome_produto, c.nome_categoria, tb.total_buscas " +
                        ") " +
                        "SELECT metricas.nome_produto, metricas.nome_categoria, metricas.total_buscas, " +
                        "       metricas.preco_mercado, metricas.menor_preco, metricas.mercados_com_preco, metricas.mercados_no_menor_preco, " +
                        "       COALESCE(( " +
                        "           SELECT m.nome_mercado " +
                        "           FROM ultimos_precos up " +
                        "           JOIN mercado m ON m.id_mercado = up.id_mercado " +
                        "           WHERE up.id_produto = metricas.id_produto " +
                        "             AND up.preco = metricas.menor_preco " +
                        "           ORDER BY m.nome_mercado ASC " +
                        "           LIMIT 1 " +
                        "       ), 'Sem mercado') AS mercado_referencia " +
                        "FROM metricas " +
                        "ORDER BY metricas.total_buscas DESC, metricas.nome_produto ASC",
                new String[]{idMercado}
        );

        while (cursor.moveToNext()) {
            Double precoMercado = cursor.isNull(3) ? null : cursor.getDouble(3);
            Double menorPreco = cursor.isNull(4) ? null : cursor.getDouble(4);
            int mercadosComparaveis = cursor.getInt(5);
            int mercadosNoMenorPreco = cursor.getInt(6);
            boolean mercadoTemPreco = precoMercado != null;
            boolean possuiBaseComparavel = mercadosComparaveis >= 2 && menorPreco != null;
            boolean mercadoVence = possuiBaseComparavel && precoMercado != null && Double.compare(precoMercado, menorPreco) == 0;
            boolean empateNoMenorPreco = mercadoVence && mercadosNoMenorPreco > 1;

            lista.add(new OportunidadePrecoProdutoModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(7),
                    cursor.getInt(2),
                    mercadosComparaveis,
                    mercadosNoMenorPreco,
                    precoMercado,
                    menorPreco,
                    mercadoTemPreco,
                    possuiBaseComparavel,
                    mercadoVence,
                    empateNoMenorPreco
            ));
        }

        cursor.close();
        return lista;
    }

    public java.util.List<OportunidadePrecoProdutoModel> obterProdutosVencedoresMercado(String idMercado, int limite) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<OportunidadePrecoProdutoModel> lista = new java.util.ArrayList<>();
        int limiteNormalizado = Math.max(1, limite);

        Cursor cursor = db.rawQuery(
                "WITH buscas AS ( " +
                        "    SELECT h.id_produto, COUNT(*) AS total_buscas " +
                        "    FROM historico_pesquisa h " +
                        "    GROUP BY h.id_produto " +
                        "), ultimos_precos AS ( " +
                        "    SELECT rp.id_produto, rp.id_mercado, rp.preco " +
                        "    FROM registro_preco rp " +
                        "    WHERE rp.id_registro = ( " +
                        "        SELECT rp2.id_registro " +
                        "        FROM registro_preco rp2 " +
                        "        WHERE rp2.id_produto = rp.id_produto " +
                        "          AND rp2.id_mercado = rp.id_mercado " +
                        "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                        "        LIMIT 1 " +
                        "    ) " +
                        "), produtos_comparaveis AS ( " +
                        "    SELECT id_produto " +
                        "    FROM ultimos_precos " +
                        "    GROUP BY id_produto " +
                        "    HAVING COUNT(*) >= 2 " +
                        "), metricas AS ( " +
                        "    SELECT up.id_produto, " +
                        "           MAX(CASE WHEN up.id_mercado = ? THEN up.preco END) AS preco_mercado, " +
                        "           MIN(up.preco) AS menor_preco, " +
                        "           COUNT(*) AS mercados_com_preco, " +
                        "           SUM(CASE " +
                        "                   WHEN up.preco = ( " +
                        "                       SELECT MIN(up2.preco) " +
                        "                       FROM ultimos_precos up2 " +
                        "                       WHERE up2.id_produto = up.id_produto " +
                        "                   ) THEN 1 " +
                        "                   ELSE 0 " +
                        "               END) AS mercados_no_menor_preco " +
                        "    FROM ultimos_precos up " +
                        "    JOIN produtos_comparaveis pc ON pc.id_produto = up.id_produto " +
                        "    GROUP BY up.id_produto " +
                        ") " +
                        "SELECT p.nome_produto, COALESCE(c.nome_categoria, 'Sem categoria') AS nome_categoria, COALESCE(b.total_buscas, 0) AS total_buscas, " +
                        "       metricas.preco_mercado, metricas.menor_preco, metricas.mercados_com_preco, metricas.mercados_no_menor_preco " +
                        "FROM metricas " +
                        "JOIN produto p ON p.id_produto = metricas.id_produto " +
                        "LEFT JOIN categoria c ON c.id_categoria = p.id_categoria " +
                        "LEFT JOIN buscas b ON b.id_produto = metricas.id_produto " +
                        "WHERE metricas.preco_mercado IS NOT NULL " +
                        "  AND metricas.preco_mercado = metricas.menor_preco " +
                        "ORDER BY total_buscas DESC, p.nome_produto ASC " +
                        "LIMIT " + limiteNormalizado,
                new String[]{idMercado}
        );

        while (cursor.moveToNext()) {
            Double precoMercado = cursor.isNull(3) ? null : cursor.getDouble(3);
            Double menorPreco = cursor.isNull(4) ? null : cursor.getDouble(4);
            int mercadosComparaveis = cursor.getInt(5);
            int mercadosNoMenorPreco = cursor.getInt(6);

            lista.add(new OportunidadePrecoProdutoModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    "",
                    cursor.getInt(2),
                    mercadosComparaveis,
                    mercadosNoMenorPreco,
                    precoMercado,
                    menorPreco,
                    precoMercado != null,
                    mercadosComparaveis >= 2 && menorPreco != null,
                    true,
                    mercadosNoMenorPreco > 1
            ));
        }

        cursor.close();
        return lista;
    }

    public CompetitividadeMercadoModel obterCompetitividadeMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "WITH ultimos_precos AS ( " +
                        "    SELECT rp.id_produto, rp.id_mercado, rp.preco " +
                        "    FROM registro_preco rp " +
                        "    WHERE rp.id_registro = ( " +
                        "        SELECT rp2.id_registro " +
                        "        FROM registro_preco rp2 " +
                        "        WHERE rp2.id_produto = rp.id_produto " +
                        "          AND rp2.id_mercado = rp.id_mercado " +
                        "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                        "        LIMIT 1 " +
                        "    ) " +
                        "), produtos_comparaveis AS ( " +
                        "    SELECT id_produto " +
                        "    FROM ultimos_precos " +
                        "    GROUP BY id_produto " +
                        "    HAVING COUNT(*) >= 2 " +
                        "), metricas AS ( " +
                        "    SELECT up.id_mercado, " +
                        "           COUNT(*) AS produtos_comparaveis, " +
                        "           SUM(CASE " +
                        "                   WHEN up.preco = ( " +
                        "                       SELECT MIN(up2.preco) " +
                        "                       FROM ultimos_precos up2 " +
                        "                       WHERE up2.id_produto = up.id_produto " +
                        "                   ) THEN 1 " +
                        "                   ELSE 0 " +
                        "               END) AS produtos_vencidos " +
                        "    FROM ultimos_precos up " +
                        "    JOIN produtos_comparaveis pc ON pc.id_produto = up.id_produto " +
                        "    GROUP BY up.id_mercado " +
                        ") " +
                        "SELECT m.id_mercado, m.nome_mercado, mt.produtos_comparaveis, mt.produtos_vencidos, " +
                        "       ROUND((mt.produtos_vencidos * 100.0) / mt.produtos_comparaveis, 1) AS percentual_vitorias " +
                        "FROM metricas mt " +
                        "JOIN mercado m ON m.id_mercado = mt.id_mercado " +
                        "WHERE mt.produtos_comparaveis > 0 " +
                        "ORDER BY percentual_vitorias DESC, mt.produtos_vencidos DESC, mt.produtos_comparaveis DESC, m.nome_mercado ASC",
                null
        );

        int posicao = 1;
        int totalMercadosCompetitivos = 0;
        int posicaoMercado = 0;
        int produtosComparaveisMercado = 0;
        int produtosVencidosMercado = 0;
        double percentualVitoriasMercado = 0.0;

        while (cursor.moveToNext()) {
            int idMercadoAtual = cursor.getInt(0);
            int produtosComparaveis = cursor.getInt(2);
            int produtosVencidos = cursor.getInt(3);
            double percentualVitorias = cursor.getDouble(4);

            if (String.valueOf(idMercadoAtual).equals(idMercado)) {
                posicaoMercado = posicao;
                produtosComparaveisMercado = produtosComparaveis;
                produtosVencidosMercado = produtosVencidos;
                percentualVitoriasMercado = percentualVitorias;
            }

            totalMercadosCompetitivos++;
            posicao++;
        }
        cursor.close();

        if (totalMercadosCompetitivos == 0) {
            return new CompetitividadeMercadoModel(0, 0, 0, 0, 0.0, "Sem base comparável suficiente", false);
        }

        if (produtosComparaveisMercado == 0) {
            return new CompetitividadeMercadoModel(0, totalMercadosCompetitivos, 0, 0, 0.0, "Sem base comparável suficiente", false);
        }

        return new CompetitividadeMercadoModel(
                posicaoMercado,
                totalMercadosCompetitivos,
                produtosComparaveisMercado,
                produtosVencidosMercado,
                percentualVitoriasMercado,
                obterFaixaResumoCompetitividade(posicaoMercado, percentualVitoriasMercado),
                true
        );
    }

    private String obterFaixaResumoCompetitividade(int posicao, double percentualVitorias) {
        if (posicao <= 0) {
            return "Sem base comparável suficiente";
        }

        if (posicao == 1) {
            return "Seu mercado lidera em menor preço nos produtos comparáveis";
        }

        if (percentualVitorias >= 50.0) {
            return "Seu mercado disputa a liderança em menor preço";
        }

        if (percentualVitorias >= 30.0) {
            return "Seu mercado tem competitividade relevante em preço";
        }

        return "Há espaço para ganhar mais vitórias por menor preço";
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

    public java.util.List<HistoricoPrecoModel> obterHistoricoMediaPrecoMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<HistoricoPrecoModel> lista = new java.util.ArrayList<>();

        Cursor datasCursor = db.rawQuery(
                "SELECT DISTINCT substr(data_registro, 1, 10) AS data_base " +
                        "FROM registro_preco " +
                        "WHERE id_mercado = ? " +
                        "ORDER BY data_base DESC " +
                        "LIMIT 5",
                new String[]{idMercado}
        );

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

                lista.add(
                        0,
                        new HistoricoPrecoModel(
                                formatarData(dataBase),
                                String.format(java.util.Locale.getDefault(), "R$ %.2f", media),
                                media,
                                quantidadeProdutos
                        )
                );
            }

            resumoCursor.close();
        }

        datasCursor.close();
        return lista;
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

    public java.util.List<TopProdutoDashboardModel> obterTopProdutosRecentesMercado(String idMercado) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<TopProdutoDashboardModel> lista = new java.util.ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT p.nome_produto, COUNT(*) AS total " +
                        "FROM (" +
                        "    SELECT id_produto " +
                        "    FROM registro_preco " +
                        "    WHERE id_mercado = ? " +
                        "    ORDER BY id_registro DESC " +
                        "    LIMIT 12" +
                        ") recentes " +
                        "JOIN produto p ON p.id_produto = recentes.id_produto " +
                        "GROUP BY p.id_produto, p.nome_produto " +
                        "ORDER BY total DESC, p.nome_produto ASC " +
                        "LIMIT 3",
                new String[]{idMercado}
        );

        while (cursor.moveToNext()) {
            lista.add(new TopProdutoDashboardModel(
                    cursor.getString(0),
                    cursor.getInt(1)
            ));
        }

        cursor.close();
        return lista;
    }

    public java.util.List<DiferencaPrecoProdutoModel> obterProdutosMaiorDiferencaPreco() {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<DiferencaPrecoProdutoModel> lista = new java.util.ArrayList<>();

        Cursor cursor = db.rawQuery(
                "WITH ultimos_precos AS ( " +
                        "    SELECT rp.id_produto, rp.id_mercado, rp.preco " +
                        "    FROM registro_preco rp " +
                        "    WHERE rp.id_registro = ( " +
                        "        SELECT rp2.id_registro " +
                        "        FROM registro_preco rp2 " +
                        "        WHERE rp2.id_produto = rp.id_produto " +
                        "          AND rp2.id_mercado = rp.id_mercado " +
                        "        ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                        "        LIMIT 1 " +
                        "    ) " +
                        "), diferencas AS ( " +
                        "    SELECT up.id_produto, MIN(up.preco) AS menor_preco, MAX(up.preco) AS maior_preco " +
                        "    FROM ultimos_precos up " +
                        "    GROUP BY up.id_produto " +
                        "    HAVING COUNT(*) > 1 " +
                        ") " +
                        "SELECT p.nome_produto, " +
                        "       mb.nome_mercado AS mercado_mais_barato, " +
                        "       d.menor_preco, " +
                        "       ma.nome_mercado AS mercado_mais_caro, " +
                        "       d.maior_preco, " +
                        "       (d.maior_preco - d.menor_preco) AS diferenca_preco " +
                        "FROM diferencas d " +
                        "JOIN produto p ON p.id_produto = d.id_produto " +
                        "JOIN ultimos_precos upb ON upb.id_produto = d.id_produto AND upb.preco = d.menor_preco " +
                        "JOIN mercado mb ON mb.id_mercado = upb.id_mercado " +
                        "JOIN ultimos_precos upa ON upa.id_produto = d.id_produto AND upa.preco = d.maior_preco " +
                        "JOIN mercado ma ON ma.id_mercado = upa.id_mercado " +
                        "GROUP BY p.id_produto, p.nome_produto, mb.nome_mercado, d.menor_preco, ma.nome_mercado, d.maior_preco " +
                        "ORDER BY diferenca_preco DESC, p.nome_produto ASC " +
                        "LIMIT 3",
                null
        );

        while (cursor.moveToNext()) {
            lista.add(new DiferencaPrecoProdutoModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5)
            ));
        }

        cursor.close();
        return lista;
    }

    public int criarListaCompra(int idUsuario, String nomeLista, String dataCriacao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_usuario", idUsuario);
        values.put("nome_lista", nomeLista);
        values.put("data_criacao", dataCriacao);

        long resultado = db.insert("lista_compras", null, values);
        return resultado != -1 ? (int) resultado : -1;
    }

    public boolean itemJaExisteNaLista(int idLista, int idProduto) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id_item FROM item_lista WHERE id_lista = ? AND id_produto = ?",
                new String[]{String.valueOf(idLista), String.valueOf(idProduto)}
        );

        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    public boolean adicionarProdutoNaLista(int idLista, int idProduto, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (itemJaExisteNaLista(idLista, idProduto)) {
            Cursor cursor = db.rawQuery(
                    "SELECT quantidade FROM item_lista WHERE id_lista = ? AND id_produto = ?",
                    new String[]{String.valueOf(idLista), String.valueOf(idProduto)}
            );

            int quantidadeAtual = 0;
            if (cursor.moveToFirst()) {
                quantidadeAtual = cursor.getInt(0);
            }
            cursor.close();

            ContentValues updateValues = new ContentValues();
            updateValues.put("quantidade", quantidadeAtual + quantidade);

            int linhas = db.update(
                    "item_lista",
                    updateValues,
                    "id_lista = ? AND id_produto = ?",
                    new String[]{String.valueOf(idLista), String.valueOf(idProduto)}
            );

            return linhas > 0;
        }

        ContentValues values = new ContentValues();
        values.put("id_lista", idLista);
        values.put("id_produto", idProduto);
        values.put("quantidade", quantidade);

        long resultado = db.insert("item_lista", null, values);
        return resultado != -1;
    }

    public ListaCompraModel obterUltimaListaUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id_lista, nome_lista, data_criacao " +
                        "FROM lista_compras " +
                        "WHERE id_usuario = ? " +
                        "ORDER BY id_lista DESC " +
                        "LIMIT 1",
                new String[]{String.valueOf(idUsuario)}
        );

        ListaCompraModel lista = null;
        if (cursor.moveToFirst()) {
            lista = new ListaCompraModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
        }

        cursor.close();
        return lista;
    }

    public java.util.List<ListaCompraModel> listarListasUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<ListaCompraModel> listas = new java.util.ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT id_lista, nome_lista, data_criacao " +
                        "FROM lista_compras " +
                        "WHERE id_usuario = ? " +
                        "ORDER BY id_lista DESC",
                new String[]{String.valueOf(idUsuario)}
        );

        while (cursor.moveToNext()) {
            listas.add(new ListaCompraModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            ));
        }

        cursor.close();
        return listas;
    }

    public ListaCompraModel obterListaPorId(int idLista) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id_lista, nome_lista, data_criacao " +
                        "FROM lista_compras " +
                        "WHERE id_lista = ?",
                new String[]{String.valueOf(idLista)}
        );

        ListaCompraModel lista = null;
        if (cursor.moveToFirst()) {
            lista = new ListaCompraModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
        }

        cursor.close();
        return lista;
    }

    public java.util.List<ItemListaCompraModel> obterItensDaLista(int idLista) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<ItemListaCompraModel> lista = new java.util.ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT il.id_item, il.id_produto, p.nome_produto, il.quantidade, (" +
                        "    SELECT rp.preco " +
                        "    FROM registro_preco rp " +
                        "    WHERE rp.id_produto = il.id_produto " +
                        "    ORDER BY rp.data_registro DESC, rp.id_registro DESC " +
                        "    LIMIT 1" +
                        ") AS preco_referencia " +
                        "FROM item_lista il " +
                        "JOIN produto p ON p.id_produto = il.id_produto " +
                        "WHERE il.id_lista = ? " +
                        "ORDER BY p.nome_produto ASC",
                new String[]{String.valueOf(idLista)}
        );

        while (cursor.moveToNext()) {
            lista.add(new ItemListaCompraModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.isNull(4) ? 0.0 : cursor.getDouble(4)
            ));
        }

        cursor.close();
        return lista;
    }

    public boolean atualizarQuantidadeItemLista(int idItem, int novaQuantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantidade", novaQuantidade);

        int linhas = db.update(
                "item_lista",
                values,
                "id_item = ?",
                new String[]{String.valueOf(idItem)}
        );

        return linhas > 0;
    }

    public boolean removerItemDaLista(int idItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhas = db.delete(
                "item_lista",
                "id_item = ?",
                new String[]{String.valueOf(idItem)}
        );

        return linhas > 0;
    }

    public boolean excluirListaCompra(int idLista) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(
                    "item_lista",
                    "id_lista = ?",
                    new String[]{String.valueOf(idLista)}
            );

            int linhasLista = db.delete(
                    "lista_compras",
                    "id_lista = ?",
                    new String[]{String.valueOf(idLista)}
            );

            if (linhasLista <= 0) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public java.util.List<ResultadoComparacaoMercadoModel> obterComparacaoMercadosPorLista(int idLista) {
        SQLiteDatabase db = this.getReadableDatabase();
        java.util.List<ResultadoComparacaoMercadoModel> lista = new java.util.ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT m.nome_mercado, SUM(base.quantidade * base.preco_mercado) AS total_lista " +
                        "FROM mercado m " +
                        "JOIN (" +
                        "    SELECT rp.id_mercado, il.id_produto, il.quantidade, rp.preco AS preco_mercado " +
                        "    FROM item_lista il " +
                        "    JOIN registro_preco rp ON rp.id_produto = il.id_produto " +
                        "    WHERE il.id_lista = ? " +
                        "      AND rp.id_registro = (" +
                        "          SELECT rp2.id_registro " +
                        "          FROM registro_preco rp2 " +
                        "          WHERE rp2.id_produto = il.id_produto " +
                        "            AND rp2.id_mercado = rp.id_mercado " +
                        "          ORDER BY rp2.data_registro DESC, rp2.id_registro DESC " +
                        "          LIMIT 1" +
                        "      )" +
                        ") base ON base.id_mercado = m.id_mercado " +
                        "GROUP BY m.id_mercado, m.nome_mercado " +
                        "HAVING COUNT(DISTINCT base.id_produto) = (" +
                        "    SELECT COUNT(DISTINCT id_produto) FROM item_lista WHERE id_lista = ?" +
                        ") " +
                        "ORDER BY total_lista ASC, m.nome_mercado ASC",
                new String[]{String.valueOf(idLista), String.valueOf(idLista)}
        );

        java.util.List<String> nomes = new java.util.ArrayList<>();
        java.util.List<Double> totais = new java.util.ArrayList<>();

        while (cursor.moveToNext()) {
            nomes.add(cursor.getString(0));
            totais.add(cursor.getDouble(1));
        }

        cursor.close();

        if (totais.isEmpty()) {
            return lista;
        }

        double menorTotal = totais.get(0);

        for (int i = 0; i < totais.size(); i++) {
            double percentual = menorTotal > 0
                    ? ((totais.get(i) - menorTotal) * 100.0) / menorTotal
                    : 0.0;

            lista.add(new ResultadoComparacaoMercadoModel(
                    nomes.get(i),
                    totais.get(i),
                    percentual
            ));
        }

        return lista;
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
