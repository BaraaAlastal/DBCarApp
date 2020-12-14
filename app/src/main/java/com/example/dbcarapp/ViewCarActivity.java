package com.example.dbcarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class ViewCarActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText et_model, et_color, et_dpl, et_description;
    private ImageView iv;
    private DatabaseAccess db;

    private int carId = -1;
    private int PICK_IMAGE_REQ_CODE = 1;
    private Uri imageUri;
    public static final int ADD_CAR_RESULT_CODE = 1;
    public static final int EDIT_CAR_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_car);

        toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        et_model = findViewById(R.id.et_details_model);
        et_color = findViewById(R.id.et_details_color);
        et_dpl = findViewById(R.id.et_details_dpl);
        et_description = findViewById(R.id.et_details_description);
        iv = findViewById(R.id.details_iv);

        //الان لما اضغط على ال fab وعلى العنصر في ال recycler حيوديني عنفس شاشة التفاصيل لكن بدي اميز بحيث لما اضغط على ال fab بدي اضيف في حين لما اضغط على النصر بدي اعدّل
        Intent intent = getIntent();
        carId = intent.getIntExtra(MainActivity.CAR_KEY, -1);

        db = DatabaseAccess.getInstance(this);
        if (carId == -1) {
            enableFields();
            clearFields();
            //add process by fab button
            //here the fields(editTexts) will be Enabled because i want add car

        } else {
            //show(edit) process by clicked in item in main activity
            //here the fields(editTexts) will be Disabled
            disableFields();
            db.open();
            Car c = db.getCar(carId);
            db.close();
            if (c != null) {
                fillCarToFields(c);
            }

        }
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EXTERNAL_CONTENT_URI USE TO TAKE DATA(URI) ABOUT IMAGE TO STORE IN DB(IMAGE PATH)
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, PICK_IMAGE_REQ_CODE);
            }
        });

    }

    private void fillCarToFields(Car c) {
        if (c.getImage() != null && !c.getImage().equals(""))
            iv.setImageURI(Uri.parse(c.getImage()));
        et_model.setText(c.getModel());
        et_color.setText(c.getColor());
        et_dpl.setText(String.valueOf(c.getDpl()));
        et_description.setText(c.getDescription());
    }

    private void disableFields() {
        iv.setEnabled(false);
        et_model.setEnabled(false);
        et_color.setEnabled(false);
        et_dpl.setEnabled(false);
        et_description.setEnabled(false);
    }

    private void enableFields() {
        iv.setEnabled(true);
        et_model.setEnabled(true);
        et_color.setEnabled(true);
        et_dpl.setEnabled(true);
        et_description.setEnabled(true);
    }

    private void clearFields() {
        //after add process the edits are clear
        iv.setImageURI(null);
        et_model.setText("");
        et_color.setText("");
        ;
        et_dpl.setText("");
        ;
        et_description.setText("");
        ;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        MenuItem save = menu.findItem(R.id.details_menu_save);
        MenuItem edit = menu.findItem(R.id.details_menu_edit);
        MenuItem delete = menu.findItem(R.id.details_menu_delete);
        if (carId == -1) {
            //add car by fab
            save.setVisible(true);
            edit.setVisible(false);
            delete.setVisible(false);
        } else {
            save.setVisible(false);
            edit.setVisible(true);
            delete.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //بما اني استعملت return فمش لازم اعمل break لأنه بطلع من اللوب بعد return
        String model, color, desc, image = "";
        double dpl;

        db.open();
        switch (item.getItemId()) {
            case R.id.details_menu_save:
                model = et_model.getText().toString();
                color = et_color.getText().toString();
                desc = et_description.getText().toString();
                dpl = Double.parseDouble(et_dpl.getText().toString());
                if (imageUri != null)
                    image = imageUri.toString();
                Car c = new Car(carId, model, color, dpl, image, desc);
                boolean res = true;
                if (carId == -1) {
                    res = db.insertCar(c);
                    if (res) {
                        Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show();
                        setResult(ADD_CAR_RESULT_CODE);
                        finish();
                    }
                } else {
                    res = db.updateCar(c);
                    if (res) {
                        Toast.makeText(this, "Car modified successfully", Toast.LENGTH_SHORT).show();
                        setResult(EDIT_CAR_RESULT_CODE);
                        finish();
                    }
                }
                return true;
            case R.id.details_menu_edit:
                enableFields();
                MenuItem save = toolbar.getMenu().findItem(R.id.details_menu_save);
                MenuItem edit = toolbar.getMenu().findItem(R.id.details_menu_edit);
                MenuItem delete = toolbar.getMenu().findItem(R.id.details_menu_delete);
                delete.setVisible(false);
                edit.setVisible(false);
                save.setVisible(true);
                return true;
            case R.id.details_menu_delete:
                c = new Car(carId, null, null, 0, null, null);
                res = db.deletCar(c);
                if (res) {
                    Toast.makeText(this, "Car deleted successfully", Toast.LENGTH_SHORT).show();
                    setResult(EDIT_CAR_RESULT_CODE, null);
                    finish();
                }
                return true;
        }
        db.close();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                iv.setImageURI(imageUri);
            }
        }
    }
}
