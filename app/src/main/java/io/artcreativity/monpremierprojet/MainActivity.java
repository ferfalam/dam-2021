package io.artcreativity.monpremierprojet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import io.artcreativity.monpremierprojet.dao.ProductRoomDao;
import io.artcreativity.monpremierprojet.entities.Product;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getCanonicalName();
    private ProductRoomDao productRoomDao;
    private boolean modify = false;
    private Product product;

    private TextInputEditText designationEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText priceEditText;
    private TextInputEditText quantityInStockEditText;
    private TextInputEditText alertQuantityEditText;

    private TextView designationError;
    private TextView descriptionError;
    private TextView priceError;
    private TextView quantityInStockError;
    private TextView alertQuantityError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "saveProduct: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        designationEditText = findViewById(R.id.name);
        descriptionEditText = findViewById(R.id.description);
        priceEditText = findViewById(R.id.price);
        quantityInStockEditText = findViewById(R.id.quantity_in_stock);
        alertQuantityEditText = findViewById(R.id.alert_quantity);
        designationError = findViewById(R.id.name_error);
        descriptionError = findViewById(R.id.description_error);
        priceError = findViewById(R.id.price_error);
        quantityInStockError = findViewById(R.id.quantity_in_stock_error);
        alertQuantityError = findViewById(R.id.alert_quantity_error);

        Product p = (Product) getIntent().getSerializableExtra("THE_PROD");
        if (p != null){
            modify = true;
            product = p;
            designationEditText.setText(product.name);
            descriptionEditText.setText(product.description);
            priceEditText.setText(Double.toString(product.price));
            quantityInStockEditText.setText(Double.toString(product.quantityInStock));
            alertQuantityEditText.setText(Double.toString(product.alertQuantity));
        }
//        findViewById(R.id.my_btn).setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                saveProduct(view);
//            }
//        });
        findViewById(R.id.my_btn).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void saveProduct(View view) {
        Log.e(TAG, "saveProduct: ");
        if (designationEditText.getText().toString().isEmpty() || descriptionEditText.getText().toString().isEmpty() || priceEditText.getText().toString().isEmpty() || quantityInStockEditText.getText().toString().isEmpty() || alertQuantityEditText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            designationError.setText("");
            descriptionError.setText("");
            priceError.setText("");
            quantityInStockError.setText("");
            alertQuantityError.setText("");
            if (designationEditText.getText().toString().isEmpty()){
                designationError.setText(R.string.name_error_text);
            }
            if (descriptionEditText.getText().toString().isEmpty()){
                descriptionError.setText(R.string.description_error_text);
            }
            if (priceEditText.getText().toString().isEmpty()){
                priceError.setText(R.string.price_error_text);
            }
            if (quantityInStockEditText.getText().toString().isEmpty()){
                quantityInStockError.setText(R.string.quantity_in_stock_error_text);
            }
            if (alertQuantityEditText.getText().toString().isEmpty()){
                alertQuantityError.setText(R.string.alert_quantity_error_text);
            }
        } else {
            designationError.setText("");
            descriptionError.setText("");
            priceError.setText("");
            quantityInStockError.setText("");
            alertQuantityError.setText("");
            if (!modify){
                product = new Product();
            }
            product.name = designationEditText.getText().toString();
            product.description = descriptionEditText.getText().toString();
            product.price = Double.parseDouble(priceEditText.getText().toString());
            product.quantityInStock = Double.parseDouble(quantityInStockEditText.getText().toString());
            product.alertQuantity = Double.parseDouble(alertQuantityEditText.getText().toString());

            Intent intent = getIntent();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!modify){
                        productRoomDao.insert(product);
                        intent.putExtra("NEW_PROD", product);
                    } else {
                        productRoomDao.update(product);
                        intent.putExtra("MODIFY_PROD", product);
                    }
                }
            });
            thread.start();
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        saveProduct(view);
    }
}