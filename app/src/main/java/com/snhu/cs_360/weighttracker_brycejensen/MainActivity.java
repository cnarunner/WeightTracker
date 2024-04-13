package com.snhu.cs_360.weighttracker_brycejensen;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // For add weight popup
    Dialog addWeightDialog;
    MaterialButton closeDialog_addWeight;
    FloatingActionButton FAB_addWeight;

    // Calendar Stuff
    Calendar initialDate;
    TextView tvDate;

    // For fake data
    ArrayList<WeightModel> weightModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerWeightData);

        setUpWeightModels();
        // must be after setUpWeightModels()
        Weight_RecyclerViewAdapter adapter = new Weight_RecyclerViewAdapter(this, weightModels);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // For add weight popup
        FAB_addWeight = findViewById(R.id.FABaddWeight);
        FAB_addWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWeightDialog();
            }
        });

    }

    public void showAddWeightDialog() {
        addWeightDialog = new Dialog(this, R.style.DialogStyle);
        addWeightDialog.setContentView(R.layout.fragment_add_weight_popup);

        // Create a SimpleDateFormat object with the desired date format pattern "MM-dd-yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM—dd—yyyy", Locale.getDefault());
        // Get the current date
        String currentDate = dateFormat.format(new Date());

        tvDate = addWeightDialog.findViewById(R.id.tvDate);
        // Set the text of the tvDate TextView to display the current date
        tvDate.setText(currentDate);

        closeDialog_addWeight = addWeightDialog.findViewById(R.id.button_addWeightCancel);
        closeDialog_addWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeightDialog.dismiss();
            }
        });

        MaterialButton btn_openCalendar = addWeightDialog.findViewById(R.id.button_selectDate);
        btn_openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarDialog();
            }
        });

        MaterialButton btn_addWeightSubmit = addWeightDialog.findViewById(R.id.button_addWeightSubmit);
        EditText etWeight = addWeightDialog.findViewById(R.id.etWeight);
        btn_addWeightSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addWeight();
                addWeightDialog.dismiss();
                String weightText = etWeight.getText().toString();
                Toast.makeText(MainActivity.this, "Weight Added: " + weightText, Toast.LENGTH_SHORT).show();
            }
        });


        addWeightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addWeightDialog.show();
    }

    private void openCalendarDialog() {
        initialDate = Calendar.getInstance();

        DatePickerDialog dialogDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                initialDate.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM—dd—yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(initialDate.getTime());
                tvDate.setText(formattedDate);
            }
        }, initialDate.get(Calendar.YEAR), initialDate.get(Calendar.MONTH), initialDate.get(Calendar.DAY_OF_MONTH));

        dialogDate.show();
    }

   


    private void setUpWeightModels() {
        String[] weights = getResources().getStringArray(R.array.weights);
        String[] dates = getResources().getStringArray(R.array.dates);
        String[] PlusMinus = getResources().getStringArray(R.array.PlusMinus);

        for (int i = 0; i<weights.length; i++) {
            weightModels.add(new WeightModel(weights[i],
                    dates[i],
                    PlusMinus[i]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
