package com.example.batikstore.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product implements Parcelable {

    @PrimaryKey
    private int id;
    private String title;
    private double price;
    private String description;
    private String category;
    private String image;

    @Embedded
    private Rating rating;

    private boolean favorite;

    // Wajib ada untuk Room
    public Product() {}

    // Diabaikan Room agar tidak ambigu dengan konstruktor kosong
    @Ignore
    protected Product(Parcel in) {
        id = in.readInt();
        title = in.readString();
        price = in.readDouble();
        description = in.readString();
        category = in.readString();
        image = in.readString();
        rating = new Rating();
        rating.setRate(in.readDouble());
        rating.setCount(in.readInt());
        favorite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeDouble(price);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(image);
        dest.writeDouble(rating != null ? rating.getRate() : 0);
        dest.writeInt(rating != null ? rating.getCount() : 0);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override public Product createFromParcel(Parcel in) { return new Product(in); }
        @Override public Product[] newArray(int size) { return new Product[size]; }
    };

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Rating getRating() { return rating; }
    public void setRating(Rating rating) { this.rating = rating; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}