package com.example.batikstore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.db.ProductDao;
import com.example.batikstore.model.Product;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.example.batikstore.util.PriceUtil;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.Toast;
import com.example.batikstore.db.CartDao;
import com.example.batikstore.model.CartItem;
import com.google.android.material.button.MaterialButton;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Product product;
    private MaterialButton btnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);
        if (product == null) { finish(); return; }

        ImageView image = findViewById(R.id.detail_image);
        TextView title = findViewById(R.id.detail_title);
        TextView category = findViewById(R.id.detail_category);
        TextView price = findViewById(R.id.detail_price);
        TextView rating = findViewById(R.id.detail_rating);
        TextView description = findViewById(R.id.detail_description);
        btnFavorite = findViewById(R.id.btn_favorite);

        title.setText(product.getTitle());
        category.setText(product.getCategory());
        price.setText(PriceUtil.formatRupiah(product.getPrice()));
        description.setText(product.getDescription());
        if (product.getRating() != null) {
            rating.setText(String.format(Locale.US, "★ %.1f / 5  (%d ulasan)",
                    product.getRating().getRate(), product.getRating().getCount()));
        }
        Glide.with(this).load(product.getImage()).centerInside().into(image);

        updateFavoriteButton();

        btnFavorite.setOnClickListener(v -> toggleFavorite());
        MaterialButton btnAddCart = findViewById(R.id.btn_add_cart);
        btnAddCart.setOnClickListener(v -> addToCart());
        MaterialButton btnBuyNow = findViewById(R.id.btn_buy_now);
        btnBuyNow.setOnClickListener(v -> buyNow());
    }

    private void addToCart() {
        executor.execute(() -> {
            CartDao dao = AppDatabase.getInstance(this).cartDao();
            CartItem existing = dao.getById(product.getId());
            if (existing != null) {
                dao.updateQty(product.getId(), existing.getQuantity() + 1);
            } else {
                CartItem item = new CartItem();
                item.setProductId(product.getId());
                item.setTitle(product.getTitle());
                item.setPrice(product.getPrice());
                item.setImage(product.getImage());
                item.setQuantity(1);
                dao.insert(item);
            }
            mainHandler.post(() ->
                    Toast.makeText(this, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show());
        });
    }

    private void buyNow() {
        executor.execute(() -> {
            CartDao dao = AppDatabase.getInstance(this).cartDao();
            CartItem existing = dao.getById(product.getId());
            if (existing == null) {
                CartItem item = new CartItem();
                item.setProductId(product.getId());
                item.setTitle(product.getTitle());
                item.setPrice(product.getPrice());
                item.setImage(product.getImage());
                item.setQuantity(1);
                dao.insert(item);
            }
            // hitung total seluruh keranjang saat ini untuk dikirim ke checkout
            double total = 0;
            for (CartItem c : dao.getAll()) total += c.getPrice() * c.getQuantity();
            double finalTotal = total;
            mainHandler.post(() -> openCheckout(finalTotal));
        });
    }

    private void openCheckout(double total) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.EXTRA_TOTAL, total);
        startActivity(intent);
    }

    private void toggleFavorite() {
        boolean newState = !product.isFavorite();
        product.setFavorite(newState);
        // Operasi DB di background thread (Executor)
        executor.execute(() -> {
            ProductDao dao = AppDatabase.getInstance(this).productDao();
            dao.setFavorite(product.getId(), newState);
            mainHandler.post(this::updateFavoriteButton);
        });
    }

    private void updateFavoriteButton() {
        if (product.isFavorite()) {
            btnFavorite.setText("Hapus dari Favorit");
            btnFavorite.setIconResource(R.drawable.ic_favorite);
        } else {
            btnFavorite.setText("Tambah ke Favorit");
            btnFavorite.setIconResource(R.drawable.ic_favorite_border);
        }
    }
}