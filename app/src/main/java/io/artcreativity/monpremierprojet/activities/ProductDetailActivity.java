package io.artcreativity.monpremierprojet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.artcreativity.monpremierprojet.R;
import io.artcreativity.monpremierprojet.entities.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productDesignation;
    private TextView productDescription;
    private TextView productPrice;
    private TextView productQuantity;
    private TextView productAlertQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productDesignation = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        productQuantity = findViewById(R.id.product_quantity);
        productAlertQuantity = findViewById(R.id.product_alert_quantity);
        Product product = (Product) getIntent().getSerializableExtra("MY_PROD");
        Log.e("PRODUCT", ""+product.serverId);
        productDesignation.setText(product.name);
        productDescription.setText(product.description);
        productPrice.setText("XOF " + product.price);
        productQuantity.setText(product.quantityInStock + " disponible" +
                (product.quantityInStock>1 ? "s" : ""));
        productAlertQuantity.setText(" " + product.alertQuantity);
        Log.e("TAG", "onCreate: " + product);
    }
}