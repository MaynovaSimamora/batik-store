package com.example.batikstore.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.batikstore.model.Product;
import java.util.List;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Query("SELECT * FROM products")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE favorite = 1")
    List<Product> getFavorites();

    @Query("SELECT id FROM products WHERE favorite = 1")
    List<Integer> getFavoriteIds();

    @Query("UPDATE products SET favorite = :fav WHERE id = :id")
    void setFavorite(int id, boolean fav);

    @Query("SELECT COUNT(*) FROM products")
    int count();
}