package com.example.listasmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "lista_smart_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_MARKET_ID = "market_id";
    private static final String KEY_MARKET_NAME = "market_name";
    private static final String KEY_MARKET_IMAGE = "market_image";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void salvarSessao(String[] dadosUser) {
        if (dadosUser == null || dadosUser.length < 6) {
            return;
        }

        preferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, dadosUser[0])
                .putString(KEY_USER_NAME, dadosUser[1])
                .putString(KEY_USER_TYPE, dadosUser[2])
                .putString(KEY_MARKET_ID, dadosUser[3])
                .putString(KEY_MARKET_NAME, dadosUser[4])
                .putString(KEY_MARKET_IMAGE, dadosUser[5])
                .apply();
    }

    public boolean estaLogado() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public Intent criarIntentHome(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("USER_ID", preferences.getString(KEY_USER_ID, ""));
        intent.putExtra("USER_NAME", preferences.getString(KEY_USER_NAME, ""));
        intent.putExtra("USER_TYPE", preferences.getString(KEY_USER_TYPE, ""));
        intent.putExtra("MARKET_ID", preferences.getString(KEY_MARKET_ID, ""));
        intent.putExtra("MARKET_NAME", preferences.getString(KEY_MARKET_NAME, ""));
        intent.putExtra("MARKET_IMAGE", preferences.getString(KEY_MARKET_IMAGE, ""));
        return intent;
    }

    public void limparSessao() {
        preferences.edit().clear().apply();
    }
}
