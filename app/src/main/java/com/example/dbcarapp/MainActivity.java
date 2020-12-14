package com.example.dbcarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_CAR_REQ_CODE =1 ;
    private static final int EDIT_CAR_REQ_CODE =2 ;
    public static final String CAR_KEY="car_key" ;

    private RecyclerView rv;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private CarRecyclerViewAdapter adapter;
    private DatabaseAccess db;
    private static final int PERMISSION_REQ_CODE=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Because the version we are working on is up to date, we will create code that gives us access to the images
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQ_CODE);
        }
        toolbar=findViewById(R.id.main_toolbar);
        // جعل toolbar اللي في التصميم هوا الاساسي تبع المشروع فهنا حيكتب اسم المشروع عليه
        setSupportActionBar(toolbar);
        rv=findViewById(R.id.main_rv);
        fab=findViewById(R.id.main_fab);
        db=DatabaseAccess.getInstance(this);
        //open database and send data to array and then send to adapter
        db.open();
        ArrayList<Car>cars=db.getAllCars();
        db.close();

        adapter=new CarRecyclerViewAdapter(cars, new onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int carId) {
                Intent i=new Intent(getBaseContext(),ViewCarActivity.class);
                i.putExtra(CAR_KEY,carId);
                startActivityForResult(i,EDIT_CAR_REQ_CODE);
            }
        });
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm=new GridLayoutManager(this,2);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),ViewCarActivity.class);
                startActivityForResult(intent,ADD_CAR_REQ_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        //inflate for search view
        SearchView searchView= (SearchView) menu.findItem(R.id.main_search).getActionView();
        // تفعيل السهم تبع الذهاب
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //when clicked submit ,return to the things that we search about it.
            public boolean onQueryTextSubmit(String query) {
                //All you enter a character starts with the search
                db.open();
                ArrayList<Car>cars=db.getCars(query);
                db.close();
                adapter.setCars(cars);
                adapter.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                db.open();
                ArrayList<Car>cars=db.getCars(newText);
                db.close();
                adapter.setCars(cars);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        //when clicked on close (x) , we return the main activity that it contain all cars
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                db.open();
                ArrayList<Car>cars=db.getAllCars();
                db.close();
                adapter.setCars(cars);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ADD_CAR_REQ_CODE ){
            db.open();
            ArrayList<Car>cars=db.getAllCars();
            db.close();
            adapter.setCars(cars);
            //this method for update list
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQ_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //تم الحصول على الصلاحية
                }
        }
    }
}
