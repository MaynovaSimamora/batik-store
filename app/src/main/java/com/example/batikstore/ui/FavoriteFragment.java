package com.example.batikstore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.batikstore.DetailActivity;
import com.example.batikstore.R;
import com.example.batikstore.adapter.ProductAdapter;
import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.db.ProductDao;
import com.example.batikstore.model.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ProductAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_favorite);
        tvEmpty = view.findViewById(R.id.tv_empty);

        adapter = new ProductAdapter(this::openDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(); // refresh setiap kali fragment tampil
    }

    private void openDetail(Product product) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    private void loadFavorites() {
        executor.execute(() -> {
            ProductDao dao = AppDatabase.getInstance(requireContext()).productDao();
            List<Product> favorites = dao.getFavorites();
            mainHandler.post(() -> {
                adapter.setItems(favorites);
                tvEmpty.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}