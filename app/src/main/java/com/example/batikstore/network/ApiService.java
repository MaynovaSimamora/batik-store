package com.example.batikstore.network;

import com.example.batikstore.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("products.json")
    Call<List<Product>> getProducts();
}