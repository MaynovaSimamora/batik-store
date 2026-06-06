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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.batikstore.LoginActivity;
import com.example.batikstore.R;
import com.example.batikstore.adapter.OrderAdapter;
import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.model.Order;
import com.example.batikstore.util.PrefManager;
import com.example.batikstore.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private OrderAdapter adapter;
    private TextView tvNoOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = view.findViewById(R.id.tv_profile_name);
        SwitchMaterial switchDark = view.findViewById(R.id.switch_dark);
        RecyclerView recycler = view.findViewById(R.id.recycler_orders);
        tvNoOrders = view.findViewById(R.id.tv_no_orders);
        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);

        SessionManager session = new SessionManager(requireContext());
        PrefManager pref = new PrefManager(requireContext());

        name.setText(session.getUsername());

        adapter = new OrderAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        switchDark.setChecked(pref.isNightMode());
        switchDark.setOnCheckedChangeListener((b, checked) -> {
            pref.setNightMode(checked);
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        btnLogout.setOnClickListener(v -> {
            session.logout();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });

        loadOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        executor.execute(() -> {
            List<Order> orders = AppDatabase.getInstance(requireContext()).orderDao().getAll();
            mainHandler.post(() -> {
                adapter.setItems(orders);
                tvNoOrders.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}