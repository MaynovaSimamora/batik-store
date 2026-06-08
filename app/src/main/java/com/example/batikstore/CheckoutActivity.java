package com.example.batikstore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.db.CartDao;
import com.example.batikstore.model.CartItem;
import com.example.batikstore.model.Order;
import com.example.batikstore.model.Product;
import com.example.batikstore.util.PriceUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_TOTAL = "extra_total";
    public static final String EXTRA_DIRECT = "extra_direct";
    public static final String EXTRA_PRODUCT = "extra_product";
    public static final String EXTRA_QTY = "extra_qty";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean directMode = false;
    private Product directProduct;
    private int directQty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_checkout);
        toolbar.setNavigationOnClickListener(v -> finish());

        directMode = getIntent().getBooleanExtra(EXTRA_DIRECT, false);

        TextInputEditText etName = findViewById(R.id.et_name);
        TextInputEditText etAddress = findViewById(R.id.et_address);
        TextView tvTotal = findViewById(R.id.tv_checkout_total);
        Spinner spinner = findViewById(R.id.spinner_payment);
        MaterialButton btnOrder = findViewById(R.id.btn_place_order);

        double total;
        if (directMode) {
            directProduct = getIntent().getParcelableExtra(EXTRA_PRODUCT);
            directQty = getIntent().getIntExtra(EXTRA_QTY, 1);
            total = directProduct != null ? directProduct.getPrice() * directQty : 0;
        } else {
            total = getIntent().getDoubleExtra(EXTRA_TOTAL, 0);
        }
        tvTotal.setText(PriceUtil.formatRupiah(total));

        String[] methods = {"Transfer Bank", "COD (Bayar di Tempat)", "E-Wallet", "Kartu Kredit"};
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, methods));

        btnOrder.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
            String payment = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : "-";
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) {
                etName.setError(TextUtils.isEmpty(name) ? "Wajib diisi" : null);
                etAddress.setError(TextUtils.isEmpty(address) ? "Wajib diisi" : null);
                return;
            }
            placeOrder(name, address, payment);
        });
    }

    private void placeOrder(String name, String address, String payment) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Order order = new Order();
            order.setCustomerName(name);
            order.setAddress(address);
            order.setPayment(payment);
            order.setDateMillis(System.currentTimeMillis());

            if (directMode && directProduct != null) {
                // Beli langsung: hanya produk ini, keranjang TIDAK disentuh
                order.setItemTitles(directProduct.getTitle());
                order.setSummary(directQty + " barang");
                order.setTotal(directProduct.getPrice() * directQty);
            } else {
                // Checkout dari keranjang: ambil semua isi keranjang lalu kosongkan
                CartDao cartDao = db.cartDao();
                List<CartItem> items = cartDao.getAll();
                int totalQty = 0;
                double total = 0;
                StringBuilder titles = new StringBuilder();
                for (CartItem c : items) {
                    totalQty += c.getQuantity();
                    total += c.getPrice() * c.getQuantity();
                    if (titles.length() > 0) titles.append(", ");
                    titles.append(c.getTitle());
                }
                order.setItemTitles(titles.toString());
                order.setSummary(totalQty + " barang");
                order.setTotal(total);
                cartDao.clear();
            }

            db.orderDao().insert(order);
            mainHandler.post(this::showSuccess);
        });
    }

    private void showSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("Pesanan Berhasil 🎉")
                .setMessage("Pesanan tersimpan di Riwayat Pesanan (menu Profil). Terima kasih telah berbelanja batik!")
                .setCancelable(false)
                .setPositiveButton("Selesai", (d, w) -> finish())
                .show();
    }
}