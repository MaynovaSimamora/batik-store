package com.example.batikstore.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.batikstore.model.CartItem;
import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartItem item);

    @Query("SELECT * FROM cart")
    List<CartItem> getAll();

    @Query("SELECT * FROM cart WHERE productId = :id LIMIT 1")
    CartItem getById(int id);

    @Query("UPDATE cart SET quantity = :qty WHERE productId = :id")
    void updateQty(int id, int qty);

    @Query("DELETE FROM cart WHERE productId = :id")
    void delete(int id);

    @Query("DELETE FROM cart")
    void clear();
}