package com.example.batikstore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.db.UserDao;
import com.example.batikstore.model.User;
import com.example.batikstore.util.PasswordUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextInputEditText etUser = findViewById(R.id.et_reg_username);
        TextInputEditText etEmail = findViewById(R.id.et_reg_email);
        TextInputEditText etPass = findViewById(R.id.et_reg_password);
        MaterialButton btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String username = etUser.getText() != null ? etUser.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String pass = etPass.getText() != null ? etPass.getText().toString() : "";

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 4) {
                Toast.makeText(this, "Password minimal 4 karakter", Toast.LENGTH_SHORT).show();
                return;
            }
            register(username, email, pass);
        });
    }

    private void register(String username, String email, String pass) {
        executor.execute(() -> {
            UserDao dao = AppDatabase.getInstance(this).userDao();
            boolean taken = dao.exists(username) > 0;
            if (!taken) {
                User u = new User();
                u.setUsername(username);
                u.setEmail(email);
                u.setPasswordHash(PasswordUtil.hash(pass));
                dao.insert(u);
            }
            boolean finalTaken = taken;
            mainHandler.post(() -> {
                if (finalTaken) {
                    Toast.makeText(this, "Username sudah dipakai", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Pendaftaran berhasil. Silakan login.", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke LoginActivity
                }
            });
        });
    }
}