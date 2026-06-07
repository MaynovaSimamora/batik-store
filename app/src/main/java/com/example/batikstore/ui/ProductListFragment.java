package com.example.batikstore.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.batikstore.DetailActivity;
import com.example.batikstore.R;
import com.example.batikstore.adapter.ProductAdapter;
import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.db.ProductDao;
import com.example.batikstore.model.Product;
import com.example.batikstore.network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment {

    private static final String ALL = "Semua";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ProductAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private View layoutError;
    private ChipGroup chipGroup;

    private final List<Product> allProducts = new ArrayList<>();
    private String currentQuery = "";
    private String selectedCategory = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_products);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutError = view.findViewById(R.id.layout_error);
        chipGroup = view.findViewById(R.id.chip_group_category);
        MaterialButton btnRefresh = view.findViewById(R.id.btn_refresh);
        EditText etSearch = view.findViewById(R.id.et_search);

        adapter = new ProductAdapter(this::openDetail);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadProducts);
        btnRefresh.setOnClickListener(v -> { showLoading(true); loadProducts(); });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                currentQuery = s.toString();
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = "";
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                String text = chip != null ? chip.getText().toString() : ALL;
                selectedCategory = text.equals(ALL) ? "" : text;
            }
            applyFilter();
        });

        showLoading(true);
        loadProducts();
    }

    private void buildCategoryChips() {
        chipGroup.removeAllViews();
        Set<String> cats = new LinkedHashSet<>();
        cats.add(ALL);
        for (Product p : allProducts) if (p.getCategory() != null) cats.add(p.getCategory());

        boolean first = true;
        for (String c : cats) {
            Chip chip = new Chip(requireContext());
            chip.setText(c);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            if (first) { chip.setChecked(true); first = false; }
            chipGroup.addView(chip);
        }
    }

    private void applyFilter() {
        String q = currentQuery.toLowerCase(Locale.getDefault()).trim();
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchCat = selectedCategory.isEmpty()
                    || (p.getCategory() != null && p.getCategory().equalsIgnoreCase(selectedCategory));
            boolean matchQuery = q.isEmpty()
                    || (p.getTitle() != null && p.getTitle().toLowerCase(Locale.getDefault()).contains(q))
                    || (p.getCategory() != null && p.getCategory().toLowerCase(Locale.getDefault()).contains(q));
            if (matchCat && matchQuery) result.add(p);
        }
        adapter.setItems(result);
    }

    private void openDetail(Product product) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    private void loadProducts() {
        layoutError.setVisibility(View.GONE);
        if (!isOnline()) { loadFromCache(true); return; }

        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call,
                                   @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cacheAndShow(response.body());
                } else { loadFromCache(true); }
            }
            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                loadFromCache(true);
            }
        });
    }

    private void cacheAndShow(List<Product> apiProducts) {
        executor.execute(() -> {
            ProductDao dao = AppDatabase.getInstance(requireContext()).productDao();
            Set<Integer> favIds = new HashSet<>(dao.getFavoriteIds());
            dao.deleteAll(); // hapus data lama supaya tidak tercampur
            for (Product p : apiProducts) if (favIds.contains(p.getId())) p.setFavorite(true);
            dao.insertAll(apiProducts);
            List<Product> fromDb = dao.getAll();
            mainHandler.post(() -> {
                allProducts.clear();
                allProducts.addAll(fromDb);
                buildCategoryChips();
                applyFilter();
                showLoading(false);
            });
        });
    }

    private void loadFromCache(boolean showErrorIfEmpty) {
        executor.execute(() -> {
            ProductDao dao = AppDatabase.getInstance(requireContext()).productDao();
            List<Product> cached = dao.getAll();
            mainHandler.post(() -> {
                showLoading(false);
                if (cached != null && !cached.isEmpty()) {
                    allProducts.clear();
                    allProducts.addAll(cached);
                    buildCategoryChips();
                    applyFilter();
                    layoutError.setVisibility(View.GONE);
                } else if (showErrorIfEmpty) {
                    layoutError.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void showLoading(boolean loading) {
        if (loading) {
            if (!swipeRefresh.isRefreshing()) progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}