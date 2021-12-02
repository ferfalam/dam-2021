package io.artcreativity.monpremierprojet.activities;

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

import io.artcreativity.monpremierprojet.dao.DataBaseRoom;
import io.artcreativity.monpremierprojet.dao.ProductRoomDao;
import io.artcreativity.monpremierprojet.R;
import io.artcreativity.monpremierprojet.entities.Product;
import io.artcreativity.monpremierprojet.webservices.ProductWebService;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        productRoomDao = DataBaseRoom.getInstance(getApplicationContext()).productRoomDao();
        Log.e(TAG, "saveProduct: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        designationEditText = findViewById(R.id.name);
        descriptionEditText = findViewById(R.id.description);
        priceEditText = findViewById(R.id.price);
        quantityInStockEditText = findViewById(R.id.quantity_in_stock);
        alertQuantityEditText = findViewById(R.id.alert_quantity);

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
        boolean des = checkEmpty(designationEditText);
        boolean desc = checkEmpty(descriptionEditText);
        boolean pri = checkEmpty(priceEditText);
        boolean qis = checkEmpty(quantityInStockEditText);
        boolean al = checkEmpty(alertQuantityEditText);

        if (des && desc && pri && qis && al)
        {

            if (!modify){
                product = new Product();
            }
            product.name = designationEditText.getText().toString();
            product.description = descriptionEditText.getText().toString();
            product.price = Double.parseDouble(priceEditText.getText().toString());
            product.quantityInStock = Double.parseDouble(quantityInStockEditText.getText().toString());
            product.alertQuantity = Double.parseDouble(alertQuantityEditText.getText().toString());

            new Thread(
                ()->{
                    ProductWebService productWebService = new ProductWebService();
                    if (!modify){
                        Product save = productWebService.createProduct(product);
                        System.out.println("save :: " + save);
                        if (save != null) {
                            productRoomDao.insert(product);
                        }
                        runOnUiThread(()->{
//                        Intent intent = getIntent();
//                        intent.putExtra("MY_PROD", save);
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
                        });
                    }else{
                        Product save = productWebService.updateProduct(product);
                        System.out.println("update :: " + save);
                        if (save != null) {
                            productRoomDao.update(product);
                        }
                    }
                }
            ).start();
            Intent intent = getIntent();
            intent.putExtra("PROD", product);
            intent.putExtra("MODIFY", modify);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private boolean checkEmpty(TextInputEditText editText) {
        if (editText.getText().toString().isEmpty()){
            editText.setError("Champ obligatoire");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        saveProduct(view);
    }
}