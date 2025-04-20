package com.example.amalyshopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private TextView totalPriceTextView, emptyCartTextView;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        loadCartItems();
        setupListView();
        calculateTotal();
        setupCheckoutButton();
    }

    private void initViews() {
        cartListView = findViewById(R.id.cartListView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        emptyCartTextView = findViewById(R.id.emptyCartTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        sharedPreferences = getSharedPreferences("AmalyShoppingPrefs", MODE_PRIVATE);
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

    private void setupListView() {
        if (cartItems.isEmpty()) {
            emptyCartTextView.setVisibility(View.VISIBLE);
            cartListView.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
        } else {
            emptyCartTextView.setVisibility(View.GONE);
            cartListView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(true);

            cartAdapter = new CartAdapter(this, cartItems);
            cartListView.setAdapter(cartAdapter);
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (Product product : cartItems) {
            total += product.getPrice();
        }

        DecimalFormat df = new DecimalFormat("#.00");
        totalPriceTextView.setText(String.format("المجموع: %s $", df.format(total)));
    }

    private void setupCheckoutButton() {
        checkoutButton.setOnClickListener(v -> checkout());
    }

    private void checkout() {
        saveOrder();
        clearCart();
        Toast.makeText(this, "تم تأكيد الطلب بنجاح!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveOrder() {
        Gson gson = new Gson();
        String ordersJson = sharedPreferences.getString("orders", "");
        List<List<Product>> orders;

        if (ordersJson.isEmpty()) {
            orders = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<List<Product>>>() {}.getType();
            orders = gson.fromJson(ordersJson, type);
        }

        orders.add(new ArrayList<>(cartItems));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("orders", gson.toJson(orders));
        editor.apply();
    }

    private void clearCart() {
        cartItems.clear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cart", "");
        editor.apply();

        setupListView();
        calculateTotal();
    }
}