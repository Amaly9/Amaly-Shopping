package com.example.amalyshopping;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int imageResource;
    private String category;
    private boolean available;
    private boolean discounted;

    public Product(int id, String name, double price, int quantity, int imageResource,
                   String category, boolean available, boolean discounted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResource = imageResource;
        this.category = category;
        this.available = available;
        this.discounted = discounted;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        imageResource = in.readInt();
        category = in.readString();
        available = in.readByte() != 0;
        discounted = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getImageResource() { return imageResource; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public boolean isDiscounted() { return discounted; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeInt(imageResource);
        dest.writeString(category);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeByte((byte) (discounted ? 1 : 0));
    }
}