package com.example.batikstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.model.User;
import com.example.batikstore.util.PasswordUtil;
import com.example.batikstore.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);

        // Auto-login jika sudah pernah login
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        TextInputEditText etUser = findViewById(R.id.et_username);
        TextInputEditText etPass = findViewById(R.id.et_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        MaterialButton btnToRegister = findViewById(R.id.btn_to_register);

        btnLogin.setOnClickListener(v -> {
            String username = etUser.getText() != null ? etUser.getText().toString().trim() : "";
            String password = etPass.getText() != null ? etPass.getText().toString() : "";
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Username & password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            doLogin(username, password);
        });

        btnToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin(String username, String password) {
        executor.execute(() -> {
            User user = AppDatabase.getInstance(this).userDao().findByUsername(username);
            boolean ok = user != null
                    && user.getPasswordHash().equals(PasswordUtil.hash(password));
            mainHandler.post(() -> {
                if (ok) {
                    session.login(username);
                    goToMain();
                } else {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}