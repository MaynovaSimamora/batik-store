package com.example.batikstore.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.batikstore.model.Order;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void insert(Order order);

    @Query("SELECT * FROM orders ORDER BY dateMillis DESC")
    List<Order> getAll();
}