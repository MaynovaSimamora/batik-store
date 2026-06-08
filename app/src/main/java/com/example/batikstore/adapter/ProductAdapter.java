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
import com.example.batikstore.model.Product;
import com.example.batikstore.util.PriceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final List<Product> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ProductAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Product> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = items.get(position);
        holder.title.setText(p.getTitle());
        holder.category.setText(p.getCategory());
        holder.price.setText(PriceUtil.formatRupiah(p.getPrice()));

        if (p.getRating() != null) {
            holder.rating.setText(String.format(Locale.US, "★ %.1f (%d)",
                    p.getRating().getRate(), p.getRating().getCount()));
        } else {
            holder.rating.setText("★ -");
        }

        Glide.with(holder.itemView.getContext())
                .load(p.getImage())
                .centerInside()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(p);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, category, price, rating;

        ViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.img_product);
            title = v.findViewById(R.id.tv_title);
            category = v.findViewById(R.id.tv_category);
            price = v.findViewById(R.id.tv_price);
            rating = v.findViewById(R.id.tv_rating);
        }
    }
}