package com.example.amalyshopping;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        super(context, R.layout.product_item, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
            holder = new ViewHolder();
            holder.productImage = convertView.findViewById(R.id.productImage);
            holder.productName = convertView.findViewById(R.id.productName);
            holder.productPrice = convertView.findViewById(R.id.productPrice);
            holder.productQuantity = convertView.findViewById(R.id.productQuantity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);

        Glide.with(context)
                .load(product.getImageResource())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.productImage);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("السعر: %.2f $", product.getPrice()));

        if (product.getQuantity() <= 0) {
            holder.productQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.productQuantity.setText("غير متوفر");
        } else {
            holder.productQuantity.setTextColor(ContextCompat.getColor(context, R.color.textColorSecondary));
            holder.productQuantity.setText(String.format("المتبقي: %d", product.getQuantity()));
        }

        return convertView;
    }



    static class ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
    }
}