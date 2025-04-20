package com.example.amalyshopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> cartItems;

    public CartAdapter(Context context, List<Product> cartItems) {
        super(context, R.layout.cart_item, cartItems);
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.cart_item, parent, false);

        Product product = cartItems.get(position);

        ImageView productImage = rowView.findViewById(R.id.cartProductImage);
        TextView productName = rowView.findViewById(R.id.cartProductName);
        TextView productPrice = rowView.findViewById(R.id.cartProductPrice);

        Glide.with(context)
                .load(product.getImageResource())

                .into(productImage);
        productName.setText(product.getName());
        productPrice.setText(String.format("%.2f $", product.getPrice()));

        return rowView;
    }
}