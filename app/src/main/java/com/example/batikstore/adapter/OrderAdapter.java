package com.example.batikstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.batikstore.R;
import com.example.batikstore.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Order> items = new ArrayList<>();
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));

    public void setItems(List<Order> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Order o = items.get(pos);
        h.date.setText(sdf.format(new Date(o.getDateMillis())));
        h.total.setText(String.format(Locale.US, "$%.2f", o.getTotal()));
        h.items.setText(o.getItemTitles());
        h.payment.setText("Pembayaran: " + o.getPayment() + "  •  " + o.getSummary());
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, total, items, payment;
        ViewHolder(@NonNull View v) {
            super(v);
            date = v.findViewById(R.id.order_date);
            total = v.findViewById(R.id.order_total);
            items = v.findViewById(R.id.order_items);
            payment = v.findViewById(R.id.order_payment);
        }
    }
}