package com.example.amalyshopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameTextView, priceTextView, quantityTextView,
            categoryTextView, discountedTextView;
    private Button addToCartButton;
    private Product product;
    private SharedPreferences sharedPreferences;
    private List<Product> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = getIntent().getParcelableExtra("product");

        initViews();
        setupProductDetails();
        setupAddToCartButton();
        loadCartItems();
    }

    private void initViews() {
        productImageView = findViewById(R.id.productDetailImageView);
        productNameTextView = findViewById(R.id.productNameTextView);
        priceTextView = findViewById(R.id.priceTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        discountedTextView = findViewById(R.id.discountedTextView);
        addToCartButton = findViewById(R.id.addToCartButton);

        sharedPreferences = getSharedPreferences("AmalyShoppingPrefs", MODE_PRIVATE);
    }

    private void setupProductDetails() {
        productImageView.setImageResource(product.getImageResource());
        productNameTextView.setText(product.getName());
        priceTextView.setText(String.format("السعر: %.2f $", product.getPrice()));
        quantityTextView.setText(String.format("الكمية المتاحة: %d", product.getQuantity()));
        categoryTextView.setText(String.format("الفئة: %s", product.getCategory()));

        if (product.isDiscounted()) {
            discountedTextView.setText("هذا المنتج مخفض");
            discountedTextView.setVisibility(View.VISIBLE);
        } else {
            discountedTextView.setVisibility(View.GONE);
        }

        if (product.getQuantity() <= 0) {
            addToCartButton.setEnabled(false);
            addToCartButton.setText("غير متوفر");
        }
    }

    private void setupAddToCartButton() {
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getQuantity() > 0) {
                    addToCart();
                    Toast.makeText(ProductDetailActivity.this,
                            "تمت إضافة المنتج إلى سلة التسوق", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void loadCartItems() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cart", "");

        if (json.isEmpty()) {
            cartItems = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Product>>() {}.getType();
            cartItems = gson.fromJson(json, type);
        }
    }

    private void addToCart() {
        product.setQuantity(product.getQuantity() - 1);
        updateProductInList();

        cartItems.add(product);
        saveCartItems();
    }

    private void updateProductInList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("products", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Product>>() {}.getType();
            List<Product> allProducts = gson.fromJson(json, type);

            for (Product p : allProducts) {
                if (p.getId() == product.getId()) {
                    p.setQuantity(product.getQuantity());
                    break;
                }
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("products", gson.toJson(allProducts));
            editor.apply();
        }
    }

    private void saveCartItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        editor.putString("cart", json);
        editor.apply();
    }
}