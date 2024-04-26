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
    private MaterialButton deleteUser;
    private static final int REQUEST_SEND_SMS_PERMISSION = 1;

    // For goal
    private EditText etTargetWeight;
    private MaterialButton btn_submitGoal;
    private String targetWeight;

    /**
     * This method is called when the activity is first created.
     * It sets up the main user interface and initializes various components.
     *
     * @param savedInstanceState The saved instance state bundle, containing data from a previous instance of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge mode for the activity
        EdgeToEdge.enable(this);
        
        // Set the content view to the activity_main layout
        setContentView(R.layout.activity_main);
        
        // Find the Toolbar view and set it as the app's action bar
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);
        
        // Set padding for the main view based on the system bars (status bar, navigation bar)
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

        // Initialize the WeightDAO instance
        weightDAO = new WeightDAO(this);

        // Get the target weight for the logged-in user from the database
        DBHelper dbHelper = new DBHelper(this);
        targetWeight = dbHelper.getTargetWeight(loggedInUsername);

        // Find the RecyclerView and set up the adapter and layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerWeightData);
        adapter = new Weight_RecyclerViewAdapter(this, weightModels, this);
        loadWeightsFromDatabase();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the FAB (Floating Action Button) for adding a new weight
        FAB_addWeight = findViewById(R.id.FABaddWeight);
        FAB_addWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWeightDialog();
            }
        });

        // Initialize the initial date for the add weight and edit weight dialogs
        initialDate = Calendar.getInstance();

        // Initialize the NotificationsSMS instance
        notificationsSMS = new NotificationsSMS(this);
    }

    /**
     * This method shows a dialog for adding a new weight entry.
     * It creates a new Dialog instance and sets its content view to the fragment_add_weight_popup layout.
     * It initializes various UI elements and sets up their click listeners.
     * The method also handles displaying the current date and allowing the user to select a different date.
     * When the user submits the new weight, it creates a WeightModel object and calls the addWeight() method to add it to the database and update the UI.
     */
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
                // Close the dialog when the cancel button is clicked
                addWeightDialog.dismiss();
            }
        });

        MaterialButton btn_openCalendar = addWeightDialog.findViewById(R.id.button_selectDate);
        btn_openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the calendar dialog to select a date when the button is clicked
                openInitialCalendarDialog();
            }
        });

        MaterialButton btn_addWeightSubmit = addWeightDialog.findViewById(R.id.button_addWeightSubmit);
        EditText etWeight = addWeightDialog.findViewById(R.id.etWeight);
        btn_addWeightSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the weight and date values from the input fields
                String weightText = etWeight.getText().toString();
                String dateText = tvDate.getText().toString();
                String plusMinus = "";
                // Create a new WeightModel object with the entered data
                WeightModel newWeight = new WeightModel(weightText, dateText, plusMinus, loggedInUsername);
                // Add the new weight to the database and update the UI
                addWeight(newWeight);
                addWeightDialog.dismiss();
                weightText = etWeight.getText().toString();
                Toast.makeText(MainActivity.this, "Weight Added: " + weightText, Toast.LENGTH_LONG).show();
            }
        });

        // Set the background of the dialog to be transparent
        addWeightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addWeightDialog.show();
    }

    /**
     * Adds a new weight entry to the database and updates the UI.
     *
     * @param newWeight The WeightModel object representing the new weight entry.
     */
    private void addWeight(WeightModel newWeight) {
        // Set the username for the new weight entry
        newWeight.setWeight_Username(loggedInUsername);

        // Insert the new weight entry into the database
        weightDAO.insert(newWeight);
        // Add the new weight entry to the list
        weightModels.add(newWeight);

        // Calculate the "plus minus" value for the new weight entry
//        newWeight.calculatePlusMinus(weightModels, weightModels.size() - 1);
        // Set the calculated "plus minus" value in the WeightModel object
        //newWeight.setWeight_PlusMinus(newWeight.getWeight_PlusMinus());

//        // Update the "plus minus" values for subsequent weight entries
//        for (int i = weightModels.size() - 1; i < weightModels.size(); i++) {
//            WeightModel weight = weightModels.get(i);
//            weight.calculatePlusMinus(weightModels, i);
//            // Set the calculated "plus minus" value in the WeightModel object
//            weight.setWeight_PlusMinus(weight.getWeight_PlusMinus());
//            // Update the weight entry in the database
//            weightDAO.update(weight);
//        }

        // Check if the new weight is less than or equal to the target weight
        if (Double.parseDouble(newWeight.getWeight_Weight()) <= Double.parseDouble(targetWeight)) {
            // If so, retrieve the user's phone number from the database
            DBHelper dbHelper = new DBHelper(this);
            String phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
            // Construct a congratulatory message
            String message = "Congratulations! You have reached your weight goal of " + targetWeight + " lbs.";
            // Send an SMS notification with the congratulatory message
            sendSMSNotification(phoneNumber, message);
        }

        // Notify the RecyclerView adapter that a new item has been inserted
        adapter.notifyItemInserted(weightModels.size() - 1);
        // Reload the weight entries from the database
        loadWeightsFromDatabase();
    }

    /**
     * Opens a calendar dialog to allow the user to select a date.
     *
     * @param dateToSet The Calendar object representing the initial date to be displayed.
     * @param textView  The TextView to display the selected date.
     */
    private void openCalendarDialog(Calendar dateToSet, TextView textView) {
        // Create a new DatePickerDialog instance
        DatePickerDialog dialogDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set the selected date in the Calendar object
                dateToSet.set(year, month, dayOfMonth);
                // Format the selected date as a string
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(dateToSet.getTime());
                // Set the formatted date in the TextView
                textView.setText(formattedDate);
            }
        }, dateToSet.get(Calendar.YEAR), dateToSet.get(Calendar.MONTH), dateToSet.get(Calendar.DAY_OF_MONTH));
    
        // Show the DatePickerDialog
        dialogDate.show();
    }
    
    /**
     * Opens the initial calendar dialog for selecting a date when adding a new weight entry.
     */
    private void openInitialCalendarDialog() {
        // Open the calendar dialog with the initial date and the tvDate TextView
        openCalendarDialog(initialDate, tvDate);
    }

    /**
     * Shows a dialog to edit an existing weight entry.
     *
     * @param weightModel The WeightModel object representing the weight entry to be edited.
     * @param position The position of the weight entry in the weightModels list.
     */
    public void showEditWeightDialog(WeightModel weightModel, int position) {
        // Create a new Dialog instance with the DialogStyle
        editWeightDialog = new Dialog(this, R.style.DialogStyle);
        // Set the layout for the dialog
        editWeightDialog.setContentView(R.layout.fragment_edit_weight_popup);

        // Initialize the views
        edit_tvDate = editWeightDialog.findViewById(R.id.edit_tvDate);
        EditText edit_etWeight = editWeightDialog.findViewById(R.id.edit_etWeight);
        btn_deleteWeight = editWeightDialog.findViewById(R.id.button_deleteWeightConfirm);

        // Get the weight model at the specified position
        WeightModel weight = weightModels.get(position);

        // Set the values from the WeightModel object
        edit_tvDate.setText(weightModel.getWeight_Date());
        edit_etWeight.setText(weightModel.getWeight_Weight());

        // Parse the date string from the WeightModel object
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(weightModel.getWeight_Date());
            dateToEdit = Calendar.getInstance();
            dateToEdit.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Handle the case where dateToEdit is null
        if (dateToEdit == null) {
            // Use the current date as a fallback
            dateToEdit = Calendar.getInstance();
        }

        // Set up the cancel button
        closeDialog_editWeight = editWeightDialog.findViewById(R.id.button_editWeightCancel);
        closeDialog_editWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWeightDialog.dismiss();
            }
        });

        // Set up the date picker button
        btn_openEditCalendar = editWeightDialog.findViewById(R.id.edit_date_button);
        btn_openEditCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditCalendarDialog(dateToEdit);
            }
        });

        // Set up the submit button
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

//                // Calculate the "plus minus" value for the updated weight and subsequent weights
//                int updatedIndex = weightModels.indexOf(weightModel);
//                weightModel.calculatePlusMinus(weightModels, updatedIndex);
//                // Set the calculated "plus minus" value in the WeightModel object
//                weightModel.setWeight_PlusMinus(weightModel.getWeight_PlusMinus());
//
//                for (int i = updatedIndex + 1; i < weightModels.size(); i++) {
//                    WeightModel weight = weightModels.get(i);
//                    weight.calculatePlusMinus(weightModels, i);
//                    // Set the calculated "plus minus" value in the WeightModel object
//                    weight.setWeight_PlusMinus(weight.getWeight_PlusMinus());
//                    // Update the weight entry in the database
//                    weightDAO.update(weight);
//                }

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

//                 // Update the "plus minus" values for the remaining weights
//                 for (int i = deletedIndex; i < weightModels.size(); i++) {
//                     weightModels.get(i).calculatePlusMinus(weightModels, i);
//
//                 }

                 loadWeightsFromDatabase();

                 String deleteText = "Deleted Weight";
                 Toast.makeText(MainActivity.this, deleteText, Toast.LENGTH_LONG).show();
             }
         });
        editWeightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editWeightDialog.show();
    }


    /**
     * Opens a calendar dialog to allow the user to edit the date of an existing weight entry.
     *
     * @param dateToEdit The Calendar object representing the initial date of the weight entry to be edited.
     */
    private void openEditCalendarDialog(Calendar dateToEdit) {
        openCalendarDialog(dateToEdit, edit_tvDate);
    }

 /**
     * Loads the weight entries from the database and populates the weightModels list.
     * This method also updates the visibility of the empty message TextView based on
     * whether the weightModels list is empty or not.
     */
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

    /**
     * This method is called when the options menu is created. It inflates the menu resource
     * (R.menu.menu) and adds the menu items to the options menu.
     *
     * @param menu The options menu in which menu items are placed.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This method is called when an option menu item is selected. It handles the actions
     * for the "Log Out", "Settings", and "Goal Weight" menu items.
     *
     * @param item The selected menu item.
     * @return true if the menu item was handled successfully, false otherwise.
     */
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
                // Show goal weight dialog
                showGoalDialog();
                return true;
            default:
                // If the menu item is not recognized, pass it to the superclass
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Shows a dialog for the user to set their goal weight.
     */
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

    /**
     * Submits the user's goal weight to the database.
     *
     * @param targetWeight The goal weight entered by the user.
     */
    private void submitGoalWeight(String targetWeight) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.updateTargetWeight(loggedInUsername, targetWeight);
        // Update the local targetWeight variable
        this.targetWeight = targetWeight;
    }

    /**
     * Logs out the current user and starts the LoginActivity.
     */
    private void logoutUser() {
        // Start the LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Shows a dialog for the user to configure settings such as phone number and SMS notifications.
     * The dialog allows the user to enter their phone number and toggle SMS notifications on or off.
     * It also provides an option to delete the user's account and all associated data.
     */
    private void showSettingsDialog() {
        Dialog settingsDialog = new Dialog(this, R.style.DialogStyle);
        settingsDialog.setContentView(R.layout.fragment_settings);

        // Initialize the EditText view for entering the phone number
        etPhoneNum = settingsDialog.findViewById(R.id.editPhoneNum);

        // Get the phone number from the database and set it in the EditText
        DBHelper dbHelper = new DBHelper(this);
        String phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
        etPhoneNum.setText(phoneNumber);

        // Initialize the SwitchMaterial view for toggling SMS notifications
        switchSMS = settingsDialog.findViewById(R.id.switch_SMS);
        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchChecked(isChecked);
            }
        });

        // Initialize the button for deleting the user's account and data
        WeightDAO weightDAO = new WeightDAO(this);
        deleteUser = settingsDialog.findViewById(R.id.button_EraseEverything);
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete all weight entries for the user
                weightDAO.deleteAllWeightsForUser(loggedInUsername);
                // Delete the user from the database
                dbHelper.deleteUser(loggedInUsername);
                // Log out the user
                logoutUser();
            }
        });

        // Set the dialog background to be transparent
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Show the settings dialog
        settingsDialog.show();
    }

    /**
     * Handles the result of the SMS permission request.
     * If the permission is granted, it enables the SMS notification switch and shows a toast message.
     * If the permission is denied, it disables the SMS notification switch and shows a toast message.
     *
     * @param requestCode  The request code passed in requestPermissions()
     * @param permissions  The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable the SMS notification switch
                switchSMS.setChecked(true);
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, disable the SMS notification switch
                switchSMS.setChecked(false);
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Sends an SMS notification to the user's phone number with the provided message.
     *
     * @param phoneNumber The user's phone number to send the SMS notification to.
     * @param message     The message to be sent in the SMS notification.
     */
    private void sendSMSNotification(String phoneNumber, String message) {
        // Check if the app has permission to send SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // If the SMS notification switch is checked, send the SMS
            if (switchSMS.isChecked()) {
                String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
                notificationsSMS.sendSMS(formattedPhoneNumber, message);
            }
        } else {
            // If the app doesn't have permission to send SMS, request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);
        }
    }

    /**
     * Formats the provided phone number to include the country code (+1 for US).
     *
     * @param phoneNumber The phone number to be formatted.
     * @return The formatted phone number with the country code.
     */
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

    /**
     * Handles the state change of the SMS notification switch.
     *
     * @param isChecked True if the SMS notification switch is checked, false otherwise.
     */
    public void onSwitchChecked(boolean isChecked) {
        if (isChecked) {
            // If the SMS notification switch is checked
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                // If the app has permission to send SMS, send a welcome message
                DBHelper dbHelper = new DBHelper(this);
                String phoneNumber = dbHelper.getPhoneNumber(loggedInUsername);
                String message = "You just subscribed to Goals for WeightTracker! Welcome! \n We will text you when you reach your goal! \n Good luck!";
                sendSMSNotification(phoneNumber, message);
            } else {
                // If the app doesn't have permission to send SMS, request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);
            }
        }
    }


    /**
     * Called when an item in the weight list is clicked.
     * Displays a short Toast message indicating that the user needs to long-click on a weight to edit it.
     *
     * @param position The position of the clicked item in the list.
     */
    @Override
    public void onItemClick(int position) {
        String toEditText = "Hold on a weight to edit.";
        Toast.makeText(MainActivity.this, toEditText, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when an item in the weight list is long-clicked.
     * Shows a dialog to allow the user to edit the selected weight entry.
     *
     * @param position    The position of the long-clicked item in the list.
     * @param weightModel The WeightModel object representing the selected weight entry.
     */
    @Override
    public void onItemLongClick(int position, WeightModel weightModel) {
        showEditWeightDialog(weightModel, position);
    }
}

