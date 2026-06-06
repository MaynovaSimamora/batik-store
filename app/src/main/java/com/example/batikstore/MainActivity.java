package com.example.batikstore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.batikstore.db.AppDatabase;
import com.example.batikstore.model.CartItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private NavController navController;
    private AppBarConfiguration appBarConfig;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        appBarConfig = new AppBarConfiguration.Builder(
                R.id.productListFragment, R.id.cartFragment,
                R.id.favoriteFragment, R.id.profileFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);

        bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        executor.execute(() -> {
            List<CartItem> items = AppDatabase.getInstance(this).cartDao().getAll();
            int totalQty = 0;
            for (CartItem c : items) totalQty += c.getQuantity();
            int finalQty = totalQty;
            mainHandler.post(() -> {
                BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.cartFragment);
                if (finalQty > 0) {
                    badge.setVisible(true);
                    badge.setNumber(finalQty);
                } else {
                    badge.setVisible(false);
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }
}