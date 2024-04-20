package com.snhu.cs_360.weighttracker_brycejensen;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.Manifest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

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

    // For data
    private ArrayList<WeightModel> weightModels = new ArrayList<>();
    private Weight_RecyclerViewAdapter adapter;
    private WeightDAO weightDAO;
    private String loggedInUsername;

    // For settings
    private SwitchMaterial switchSMS;
    private EditText etPhoneNum;
    private NotificationsSMS notificationsSMS;
    private static final int REQUEST_SEND_SMS_PERMISSION = 1;

    // For goal
    private EditText etTargetWeight;
    private MaterialButton btn_submitGoal;
    private String targetWeight;

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

        DBHelper dbHelper = new DBHelper(this);
        targetWeight = dbHelper.getTargetWeight(loggedInUsername);

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

        notificationsSMS = new NotificationsSMS(this);
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

        if (Double.parseDouble(newWeight.getWeight_Weight()) <= Double.parseDouble(targetWeight)) {
            DBHelper dbHelper = new DBHelper(this);
            String phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
            String message = "Congratulations! You have reached your weight goal of " + targetWeight + " lbs.";
            sendSMSNotification(phoneNumber, message);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemTitle = item.getTitle().toString();
        switch (itemTitle) {
            case "Log Out":
                // Perform logout operation
                logoutUser();
                return true;

            case "Settings":
                // Open settings dialog
                showSettingsDialog();
                return true;

            case "Goal Weight":
                showGoalDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showGoalDialog() {
        Dialog goalDialog = new Dialog(this, R.style.DialogStyle);
        goalDialog.setContentView(R.layout.fragment_goal_weight);

        etTargetWeight = goalDialog.findViewById(R.id.editText_GoalWeight);

        // Get the target weight from the database and set it in the EditText
        DBHelper dbHelper = new DBHelper(this);
        String targetWeightFromDB = dbHelper.getTargetWeight(loggedInUsername);
        if (targetWeightFromDB != null) {
            etTargetWeight.setText(targetWeightFromDB);
        }

        btn_submitGoal = goalDialog.findViewById(R.id.button_SubmitGoal);
        btn_submitGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGoalWeight(etTargetWeight.getText().toString());
                goalDialog.dismiss();
            }
        });
        goalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        goalDialog.show();
    }

    private void submitGoalWeight(String targetWeight) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.updateTargetWeight(loggedInUsername, targetWeight);
        // Update the local targetWeight variable
        this.targetWeight = targetWeight;
    }

    private void logoutUser() {
        // Start the LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showSettingsDialog() {
        Dialog settingsDialog = new Dialog(this, R.style.DialogStyle);
        settingsDialog.setContentView(R.layout.fragment_settings);

        // Initialize the EditText view
        etPhoneNum = settingsDialog.findViewById(R.id.editPhoneNum);

        // Get the phone number from the database and set it in the EditText
        DBHelper dbHelper = new DBHelper(this);
        String phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
        etPhoneNum.setText(phoneNumber);

        // Get the text from the EditText (if needed)
        phoneNumber = etPhoneNum.getText().toString();
        dbHelper.updatePhoneNumber(loggedInUsername, phoneNumber);

        // Initialize the SwitchMaterial view
        switchSMS = settingsDialog.findViewById(R.id.switch_SMS);
        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchChecked(isChecked);
            }
        });

        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        settingsDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send SMS
                switchSMS.setChecked(true); // Enable the switch
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                switchSMS.setChecked(false); // Disable the switch
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sendSMSNotification(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (switchSMS.isChecked()) {
                String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
                notificationsSMS.sendSMS(formattedPhoneNumber, message);
            }
        } else {
            // Request the SMS permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters from the phone number
        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

        // Check if the phone number starts with a country code (e.g., +1)
        if (phoneNumber.length() > 10) {
            // Phone number already has a country code, return it as is
            return phoneNumber;
        } else {
            // Prepend the US country code (+1) to the phone number
            return "+1" + phoneNumber;
        }
    }

    public void onSwitchChecked(boolean isChecked) {
        if (isChecked) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted, you can send SMS
                DBHelper dbHelper = new DBHelper(this);
                String phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
                String message = "You just subscribed to Goals for WeightTracker! Welcome! \n We will text you when you reach your goal! \n Good luck!";
                sendSMSNotification(phoneNumber, message);
            } else {
                // Request the SMS permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);

            }
        }
    }

//    private void onUpdatePhoneNumber(String phoneNumber) {
//        DBHelper dbHelper = new DBHelper(this);
//        phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
//        String message = "You just subscribed to Goals for WeightTracker! Welcome! \n We will text you when you reach your goal! \n Good luck!";
//        sendSMSNotification(phoneNumber, message);
//    }


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

