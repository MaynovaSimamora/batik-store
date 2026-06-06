package com.example.batikstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.batikstore.R;
import com.example.batikstore.model.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface CartListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final CartListener listener;

    public CartAdapter(CartListener listener) { this.listener = listener; }

    public void setItems(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        CartItem c = items.get(pos);
        h.title.setText(c.getTitle());
        h.price.setText(String.format(Locale.US, "$%.2f", c.getPrice()));
        h.qty.setText(String.valueOf(c.getQuantity()));
        Glide.with(h.itemView.getContext()).load(c.getImage()).centerInside().into(h.image);

        h.btnPlus.setOnClickListener(v -> listener.onIncrease(c));
        h.btnMinus.setOnClickListener(v -> listener.onDecrease(c));
        h.btnRemove.setOnClickListener(v -> listener.onRemove(c));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image; TextView title, price, qty;
        View btnPlus, btnMinus, btnRemove;
        ViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.cart_image);
            title = v.findViewById(R.id.cart_title);
            price = v.findViewById(R.id.cart_price);
            qty = v.findViewById(R.id.cart_qty);
            btnPlus = v.findViewById(R.id.btn_plus);
            btnMinus = v.findViewById(R.id.btn_minus);
            btnRemove = v.findViewById(R.id.btn_remove);
        }
    }
}