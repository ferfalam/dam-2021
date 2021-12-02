package io.artcreativity.monpremierprojet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.artcreativity.monpremierprojet.R;
import io.artcreativity.monpremierprojet.adapters.ProductAdapter;
import io.artcreativity.monpremierprojet.dao.DataBaseRoom;
import io.artcreativity.monpremierprojet.dao.ProductDao;
import io.artcreativity.monpremierprojet.dao.ProductRoomDao;
import io.artcreativity.monpremierprojet.databinding.ActivityProductBinding;
import io.artcreativity.monpremierprojet.entities.Product;

public class ProductActivity extends AppCompatActivity {

    private ActivityProductBinding binding;
    private List<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    final static int MAIN_CALL = 120;
    private ProductDao productDao;
    private ProductRoomDao productRoomDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object p = binding.ourListView.getItemAtPosition(position);
                Product product = (Product) p;
                Intent intent = new Intent(ProductActivity.this, ProductDetailActivity.class);
                intent.putExtra("MY_PROD", product);
                startActivityIfNeeded(intent, MAIN_CALL);
            }
        });

        setSupportActionBar(binding.toolbar);



        // productDao = new ProductDao(this);
        productRoomDao = DataBaseRoom.getInstance(getApplicationContext()).productRoomDao();
        generateProducts();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivityIfNeeded(intent, MAIN_CALL);
            }
        });

//        binding.ourListView.setAdapter(new ArrayAdapter<Product>(this, R.layout.simple_product_item, products.toArray(new Product[]{})));
//        buildSimpleAdapterData();

        buildCustomAdapter();
        registerForContextMenu(binding.ourListView);
    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MAIN_CALL) {
            if(resultCode== Activity.RESULT_OK) {
                Product new_product = (Product) data.getSerializableExtra("NEW_PROD");
                Product modify_product = (Product) data.getSerializableExtra("MODIFY_PROD");

                if (new_product != null){
                    products.add(new_product);
                } else if ( modify_product != null ){
                    products.set(products.indexOf(modify_product), modify_product);
                }
                productAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.product_action, menu);
        int positionOfMenuItem = 1; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString(getString(R.string.del));
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
    }



    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId()==R.id.del){
            Toast.makeText(getApplicationContext(),"Supprimer",Toast.LENGTH_LONG).show();
            Object p = binding.ourListView.getItemAtPosition(info.position);
            Product product = (Product) p;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    productRoomDao.delete(product);
                    runOnUiThread(()->{
                        products.remove(product);
                        productAdapter.notifyDataSetChanged();
                    });
                }
            });
            thread.start();
        }
        else if(item.getItemId()==R.id.upd){
            Toast.makeText(getApplicationContext(),"Modifier",Toast.LENGTH_LONG).show();
            Object p = binding.ourListView.getItemAtPosition(info.position);
            Product product = (Product) p;
            Intent intent = new Intent(ProductActivity.this, MainActivity.class);
            intent.putExtra("THE_PROD", product);
            startActivityIfNeeded(intent, MAIN_CALL);
        }else{
            return false;
        }
        return true;
    }

    private void buildCustomAdapter() {
        productAdapter = new ProductAdapter(this, products);
        binding.ourListView.setAdapter(productAdapter);
    }

    private void buildSimpleAdapterData() {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Product product :
                products) {
            Map<String, String> map = new HashMap<>();
            map.put("name", product.name);
            map.put("price", "XOF " + product.price);
            map.put("quantity",  product.quantityInStock + " disponible" +
                    (product.quantityInStock>1 ? "s" : ""));
            mapList.add(map);
        }
        binding.ourListView.setAdapter(new SimpleAdapter(this, mapList, R.layout.regular_product_item,
                new String[]{"name", "quantity", "price"}, new int[]{R.id.name, R.id.quantity_in_stock, R.id.price}));
    }

    private void generateProducts() {
//        products = productDao.findAll();
//        if(products.isEmpty()) {
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
//            productDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//
//            products = productDao.findAll();
//        }

        Thread thread = new Thread(new Runnable() {
            final List<Product> localProducts = new ArrayList<>();
            @Override
            public void run() {
                localProducts.addAll(productRoomDao.findAll());
                if(localProducts.isEmpty()) {
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
                    productRoomDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));

                    localProducts.addAll(productRoomDao.findAll());
                }
                runOnUiThread(()->{
                    products.addAll(localProducts);
                });
            }
        });
        thread.start();
//        products = productRoomDao.findAll();
//        if(products.isEmpty()) {
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
//            productRoomDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//
//            products = productRoomDao.findAll();
//        }


    }

}