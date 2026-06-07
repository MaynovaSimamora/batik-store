package com.example.batikstore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.batikstore.CheckoutActivity;
import com.example.batikstore.R;
import com.example.batikstore.adapter.CartAdapter;
import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.db.CartDao;
import com.example.batikstore.model.CartItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartFragment extends Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private CartAdapter adapter;
    private TextView tvEmpty, tvTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recycler_cart);
        tvEmpty = view.findViewById(R.id.tv_empty_cart);
        tvTotal = view.findViewById(R.id.tv_total);
        MaterialButton btnCheckout = view.findViewById(R.id.btn_checkout);

        adapter = new CartAdapter(new CartAdapter.CartListener() {
            @Override public void onIncrease(CartItem item) { changeQty(item, item.getQuantity() + 1); }
            @Override public void onDecrease(CartItem item) {
                if (item.getQuantity() > 1) changeQty(item, item.getQuantity() - 1);
                else remove(item);
            }
            @Override public void onRemove(CartItem item) { remove(item); }
        });
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            double total = currentTotal();
            if (total <= 0) {
                Toast.makeText(requireContext(), "Keranjang masih kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(requireContext(), CheckoutActivity.class);
            i.putExtra(CheckoutActivity.EXTRA_TOTAL, total);
            startActivity(i);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    private double cachedTotal = 0;
    private double currentTotal() { return cachedTotal; }

    private void loadCart() {
        executor.execute(() -> {
            CartDao dao = AppDatabase.getInstance(requireContext()).cartDao();
            List<CartItem> items = dao.getAll();
            double total = 0;
            for (CartItem c : items) total += c.getPrice() * c.getQuantity();
            double finalTotal = total;
            mainHandler.post(() -> {
                cachedTotal = finalTotal;
                adapter.setItems(items);
                tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                tvTotal.setText(PriceUtil.formatRupiah(finalTotal));
            });
        });
    }

    private void changeQty(CartItem item, int qty) {
        executor.execute(() -> {
            AppDatabase.getInstance(requireContext()).cartDao().updateQty(item.getProductId(), qty);
            mainHandler.post(this::loadCart);
        });
    }

    private void remove(CartItem item) {
        executor.execute(() -> {
            AppDatabase.getInstance(requireContext()).cartDao().delete(item.getProductId());
            mainHandler.post(this::loadCart);
        });
    }
}