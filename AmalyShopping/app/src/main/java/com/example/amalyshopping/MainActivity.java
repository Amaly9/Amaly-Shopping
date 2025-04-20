package com.example.amalyshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AmalyShoppingPrefs";
    private static final String PRODUCTS_KEY = "products";
    private static final String CART_KEY = "cart";

    private ListView productsListView;
    private ProductAdapter productAdapter;
    private ImageView logoImageView, cartIcon;
    private EditText searchEditText;
    private Spinner categorySpinner;
    private RadioGroup priceRadioGroup;
    private CheckBox availableCheckBox;
    private Switch discountSwitch;
    private Button searchButton, resetButton;
    private TextView cartBadge;

    private List<Product> productList = new ArrayList<>();
    private List<Product> filteredList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSharedPreferences();
        setupUI();
        loadInitialData();
    }

    private void initializeViews() {
        productsListView = findViewById(R.id.productsListView);
        logoImageView = findViewById(R.id.logoImageView);
        cartIcon = findViewById(R.id.cartIcon);
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        priceRadioGroup = findViewById(R.id.priceRadioGroup);
        availableCheckBox = findViewById(R.id.availableCheckBox);
        discountSwitch = findViewById(R.id.discountSwitch);
        searchButton = findViewById(R.id.searchButton);
        resetButton = findViewById(R.id.resetButton);
        cartBadge = findViewById(R.id.cartBadge);
    }

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupUI() {
        setupLogo();
        setupSpinner();
        setupListView();
        setupClickListeners();
    }

    private void setupLogo() {
        logoImageView.setImageResource(R.drawable.amaly_logo);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.product_categories,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupListView() {
        productAdapter = new ProductAdapter(this, filteredList);
        productsListView.setAdapter(productAdapter);

        productsListView.setOnItemClickListener(this::onProductItemClick);
    }

    private void onProductItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product selectedProduct = filteredList.get(position);
        navigateToProductDetail(selectedProduct);
    }

    private void navigateToProductDetail(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void setupClickListeners() {
        cartIcon.setOnClickListener(v -> navigateToCart());
        searchButton.setOnClickListener(v -> filterProducts());
        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void navigateToCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    private void loadInitialData() {
        loadProducts();
        setupCartBadge();
    }

    private void loadProducts() {
        String productsJson = sharedPreferences.getString(PRODUCTS_KEY, "");

        if (productsJson.isEmpty()) {
            createDefaultProducts();
        } else {
            productList = fromJson(productsJson);
        }

        updateFilteredList();
    }

    @NonNull
    private List<Product> fromJson(String json) {
        Type type = new TypeToken<List<Product>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    private void createDefaultProducts() {
        productList.clear();

        productList.add(new Product(1, "أحمر شفاه مات", 45.0, 10, R.drawable.lipstick, "مكياج", true, true));
        productList.add(new Product(2, "كونسيلر عالي التغطية", 60.0, 5, R.drawable.concealer, "مكياج", true, false));
        productList.add(new Product(5, "ماسكرا طويلة العمر", 55.0, 7, R.drawable.mascara, "مكياج", true, true));
        productList.add(new Product(6, "ظلال عيون لامعة", 40.0, 0, R.drawable.eyeshadow, "مكياج", false, false));
        productList.add(new Product(7, "كريم أساس", 85.0, 4, R.drawable.foundation, "مكياج", true, false));
        productList.add(new Product(8, "بلاشر وردي", 50.0, 6, R.drawable.blush, "مكياج", true, true));

        productList.add(new Product(3, "باودر تثبيت المكياج", 75.0, 8, R.drawable.powder, "بشرة", true, true));
        productList.add(new Product(4, "سيروم فيتامين سي", 120.0, 3, R.drawable.serum, "بشرة", true, false));

        saveProductsToPrefs();
    }

    private void saveProductsToPrefs() {
        sharedPreferences.edit()
                .putString(PRODUCTS_KEY, new Gson().toJson(productList))
                .apply();
    }

    private void setupCartBadge() {
        updateCartBadge();
        sharedPreferences.registerOnSharedPreferenceChangeListener((prefs, key) -> {
            if (CART_KEY.equals(key)) {
                runOnUiThread(this::updateCartBadge);
            }
        });
    }

    private void updateCartBadge() {
        String cartJson = sharedPreferences.getString(CART_KEY, "");
        List<Product> cartItems = cartJson.isEmpty() ?
                new ArrayList<>() : fromJson(cartJson);

        if (cartItems.isEmpty()) {
            cartBadge.setVisibility(View.GONE);
        } else {
            cartBadge.setText(String.valueOf(cartItems.size()));
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.bringToFront();
        }
    }

    private void filterProducts() {
        String searchQuery = searchEditText.getText().toString().toLowerCase(Locale.getDefault());
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        int selectedPriceId = priceRadioGroup.getCheckedRadioButtonId();
        boolean onlyAvailable = availableCheckBox.isChecked();
        boolean onlyDiscounted = discountSwitch.isChecked();

        filteredList.clear();

        for (Product product : productList) {
            if (matchesSearchCriteria(product, searchQuery, selectedCategory,
                    selectedPriceId, onlyAvailable, onlyDiscounted)) {
                filteredList.add(product);
            }
        }

        updateProductList();
        showEmptyListMessageIfNeeded();
    }

    private boolean matchesSearchCriteria(Product product, String searchQuery,
                                          String category, int priceId, boolean onlyAvailable, boolean onlyDiscounted) {

        return matchesSearchText(product, searchQuery) &&
                matchesCategory(product, category) &&
                matchesPriceRange(product, priceId) &&
                matchesAvailability(product, onlyAvailable) &&
                matchesDiscountStatus(product, onlyDiscounted);
    }

    private boolean matchesSearchText(Product product, String query) {
        return query.isEmpty() ||
                product.getName().toLowerCase(Locale.getDefault()).contains(query);
    }

    private boolean matchesCategory(Product product, String category) {
        return category.equals("الكل") ||
                product.getCategory().equals(category);
    }

    private boolean matchesPriceRange(Product product, int priceId) {
        if (priceId == -1) return true;

        RadioButton selectedRadio = findViewById(priceId);
        String range = selectedRadio.getText().toString();

        switch (range) {
            case "أقل من 50": return product.getPrice() < 50;
            case "50 - 100": return product.getPrice() >= 50 && product.getPrice() <= 100;
            case "أكثر من 100": return product.getPrice() > 100;
            default: return true;
        }
    }

    private boolean matchesAvailability(Product product, boolean onlyAvailable) {
        return !onlyAvailable || product.getQuantity() > 0;
    }

    private boolean matchesDiscountStatus(Product product, boolean onlyDiscounted) {
        return !onlyDiscounted || product.isDiscounted();
    }

    private void updateProductList() {
        productAdapter.notifyDataSetChanged();
    }

    private void showEmptyListMessageIfNeeded() {
        if (filteredList.isEmpty()) {
            Toast.makeText(this,
                    "لا توجد منتجات تطابق معايير البحث",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFilters() {
        searchEditText.setText("");
        categorySpinner.setSelection(0);
        priceRadioGroup.clearCheck();
        availableCheckBox.setChecked(false);
        discountSwitch.setChecked(false);

        updateFilteredList();
    }

    private void updateFilteredList() {
        filteredList.clear();
        filteredList.addAll(productList);
        updateProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}