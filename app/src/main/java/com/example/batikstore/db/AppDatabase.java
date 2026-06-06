package com.example.batikstore.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.batikstore.model.CartItem;
import com.example.batikstore.model.Order;
import com.example.batikstore.model.Product;
import com.example.batikstore.model.User;

@Database(entities = {Product.class, User.class, CartItem.class, Order.class},
        version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ProductDao productDao();
    public abstract UserDao userDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class, "batik_store.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}