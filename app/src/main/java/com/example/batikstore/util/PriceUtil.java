package com.example.batikstore.util;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtil {
    public static String formatRupiah(double price) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("in", "ID"));
        nf.setMaximumFractionDigits(0);
        return "Rp " + nf.format(price);
    }
}