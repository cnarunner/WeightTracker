package com.snhu.cs_360.weighttracker_brycejensen;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

    // For add weight popup
    private Dialog addWeightDialog;
    private MaterialButton closeDialog_addWeight;
    private FloatingActionButton FAB_addWeight;
    private TextView tvDate;

    // For edit weight popup
    private Dialog editWeightDialog;
    private TextView edit_tvDate;
    private MaterialButton closeDialog_editWeight;
    private MaterialButton btn_openEditCalendar;
    private MaterialButton btn_editWeightSubmit;
    private MaterialButton btn_deleteWeight;
    private Calendar dateToEdit;

    // Calendar Stuff
    private Calendar initialDate;

    // For fake data
    private ArrayList<WeightModel> weightModels = new ArrayList<>();
    private Weight_RecyclerViewAdapter adapter;
    private WeightDAO weightDAO;
    private String loggedInUsername;
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

        // Get the logged-in username from the Intent or SharedPreferences
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            loggedInUsername = intent.getStringExtra("username");
        }


        weightDAO = new WeightDAO(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerWeightData);

        // setUpWeightModels();
        // must be after setUpWeightModels()
        adapter = new Weight_RecyclerViewAdapter(this, weightModels, this);

        loadWeightsFromDatabase();

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

        // For addWeight and editWeight
        initialDate = Calendar.getInstance();
    }

    public void showAddWeightDialog() {
        addWeightDialog = new Dialog(this, R.style.DialogStyle);
        addWeightDialog.setContentView(R.layout.fragment_add_weight_popup);

        // Create a SimpleDateFormat object with the desired date format pattern "MM-dd-yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
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
                openInitialCalendarDialog();
            }
        });

        MaterialButton btn_addWeightSubmit = addWeightDialog.findViewById(R.id.button_addWeightSubmit);
        EditText etWeight = addWeightDialog.findViewById(R.id.etWeight);
        btn_addWeightSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightText = etWeight.getText().toString();
                String dateText = tvDate.getText().toString();
                String plusMinus = "";
                WeightModel newWeight = new WeightModel(weightText, dateText, plusMinus, loggedInUsername);
                addWeight(newWeight);
                addWeightDialog.dismiss();
                weightText = etWeight.getText().toString();
                Toast.makeText(MainActivity.this, "Weight Added: " + weightText, Toast.LENGTH_LONG).show();
            }
        });


        addWeightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addWeightDialog.show();
    }

    private void addWeight(WeightModel newWeight) {
        newWeight.setWeight_Username(loggedInUsername);

        // Add the new weight to the list
        weightDAO.insert(newWeight);
        weightModels.add(newWeight);

        // Calculate the plusMinus value
        newWeight.calculatePlusMinus(weightModels, weightModels.size() - 1);
        for (int i = weightModels.size() - 1; i < weightModels.size(); i++) {
            weightModels.get(i).calculatePlusMinus(weightModels, i);
        }

        // Update the RecyclerView adapter with the new list
        adapter.notifyItemInserted(weightModels.size() - 1);
        loadWeightsFromDatabase();
    }

    private void openCalendarDialog(Calendar dateToSet, TextView textView) {
        DatePickerDialog dialogDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateToSet.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(dateToSet.getTime());
                textView.setText(formattedDate);
            }
        }, dateToSet.get(Calendar.YEAR), dateToSet.get(Calendar.MONTH), dateToSet.get(Calendar.DAY_OF_MONTH));
    
        dialogDate.show();
    }
    
    
    private void openInitialCalendarDialog() {
        openCalendarDialog(initialDate, tvDate);
    }

    public void showEditWeightDialog(WeightModel weightModel, int position) {
        editWeightDialog = new Dialog(this, R.style.DialogStyle);
        editWeightDialog.setContentView(R.layout.fragment_edit_weight_popup);

        edit_tvDate = editWeightDialog.findViewById(R.id.edit_tvDate);
        EditText edit_etWeight = editWeightDialog.findViewById(R.id.edit_etWeight);
        btn_deleteWeight = editWeightDialog.findViewById(R.id.button_deleteWeightConfirm);


        WeightModel weight = weightModels.get(position);

        // Set the values from the WeightModel object
        edit_tvDate.setText(weightModel.getWeight_Date());
        edit_etWeight.setText(weightModel.getWeight_Weight());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(weightModel.getWeight_Date());
            dateToEdit = Calendar.getInstance();
            dateToEdit.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateToEdit == null) {
            // Handle the case where dateToEdit is null
            // For example, you could use the current date as a fallback
            dateToEdit = Calendar.getInstance();
        }

        closeDialog_editWeight = editWeightDialog.findViewById(R.id.button_editWeightCancel);
        closeDialog_editWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWeightDialog.dismiss();
            }
        });

        btn_openEditCalendar = editWeightDialog.findViewById(R.id.edit_date_button);
        btn_openEditCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditCalendarDialog(dateToEdit);
            }
        });

        btn_editWeightSubmit = editWeightDialog.findViewById(R.id.button_editWeightConfirm);
        btn_editWeightSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the weight data in the dataset
                weightModel.setWeight_Weight(Double.parseDouble(edit_etWeight.getText().toString()));

                // Update the date data in the dataset
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String newDateString = dateFormat.format(dateToEdit.getTime());
                weightModel.setWeight_Date(newDateString);

                // Update the weight entry in the database
                weightDAO.update(weightModel);

                // Calculate the "plus minus" value for the updated weight and subsequent weights
                int updatedIndex = weightModels.indexOf(weightModel);
                weightModel.calculatePlusMinus(weightModels, updatedIndex);
                for (int i = updatedIndex + 1; i < weightModels.size(); i++) {
                    weightModels.get(i).calculatePlusMinus(weightModels, i);
                }

                loadWeightsFromDatabase();

                // Notify the RecyclerView adapter about the data change
                //adapter.notifyItemChanged(position);
                editWeightDialog.dismiss();
                String weightText = edit_etWeight.getText().toString();
                Toast.makeText(MainActivity.this, "Weight Updated: " + weightText, Toast.LENGTH_LONG).show();
            }
        });

         btn_deleteWeight = editWeightDialog.findViewById(R.id.button_deleteWeightConfirm);
         btn_deleteWeight.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 int deletedIndex = weightModels.indexOf(weightModel);

                 weightDAO.delete(weightModel.getWeight_Id());

                 weightModels.remove(position);
                 editWeightDialog.dismiss();
                 adapter.notifyItemRemoved(position);

                 // Update the "plus minus" values for the remaining weights
                 for (int i = deletedIndex; i < weightModels.size(); i++) {
                     weightModels.get(i).calculatePlusMinus(weightModels, i);
                 }

                 loadWeightsFromDatabase();

                 String deleteText = "Deleted Weight";
                 Toast.makeText(MainActivity.this, deleteText, Toast.LENGTH_LONG).show();
             }
         });
        editWeightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editWeightDialog.show();
    }


    private void openEditCalendarDialog(Calendar dateToEdit) {
        openCalendarDialog(dateToEdit, edit_tvDate);
    }

//    private void setUpWeightModels() {
//        String[] weights = getResources().getStringArray(R.array.weights);
//        String[] dates = getResources().getStringArray(R.array.dates);
//        List<WeightModel> weightModelList = new ArrayList<>();
//
//        for (int i = 0; i < weights.length; i++) {
//            String weight = weights[i];
//            String date = dates[i];
//            WeightModel weightModel = new WeightModel(weight, date);
//
//            if (i > 0) {
//                double currentWeight = Double.parseDouble(weight);
//                double previousWeight = Double.parseDouble(weightModels.get(i - 1).getWeight_Weight());
//                double difference = currentWeight - previousWeight;
//                String plusMinus = difference > 0 ? "+" + String.format("%.1f", difference) : String.format("%.1f", difference);
//                weightModel.setWeight_PlusMinus(plusMinus);
//            } else {
//                weightModel.setWeight_PlusMinus("+0.0");
//            }
//
//            weightModels.add(weightModel);
//
//        }
//    }

    private void loadWeightsFromDatabase() {
        weightModels.clear(); // Clear the existing list
        List<WeightModel> weights = weightDAO.getAllWeights();
        //weightModels.addAll(weights);

        // Filter the weights by the logged-in username
        for (WeightModel weight : weights) {
            if (weight.getWeight_Username().equals(loggedInUsername)) {
                weightModels.add(weight);
            }
        }

        adapter.notifyDataSetChanged();

        // Show or hide the empty message based on the size of the weightModels list
        TextView tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        if (weightModels.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onItemClick(int position) {
        String toEditText = "Hold on a weight to edit.";
        Toast.makeText(MainActivity.this, toEditText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position, WeightModel weightModel) {
        showEditWeightDialog(weightModel, position);
    }
}

