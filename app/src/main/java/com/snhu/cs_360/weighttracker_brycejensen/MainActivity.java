package com.snhu.cs_360.weighttracker_brycejensen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // For add weight popup
    Dialog addWeightDialog;

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

        // For add weight popup
        addWeightDialog = new Dialog(this);


        RecyclerView recyclerView = findViewById(R.id.recyclerWeightData);

        setUpWeightModels();

        // must be after setUpWeightModels()
        Weight_RecyclerViewAdapter adapter = new Weight_RecyclerViewAdapter(this, weightModels);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showAddWeightPopup(View view) {
        MaterialButton cancelPopup;
        addWeightDialog.setContentView(R.layout.fragment_add_weight_popup);

        cancelPopup = findViewById(R.id.button_addWeightCancel);

        cancelPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeightDialog.dismiss();
            }
        });
        addWeightDialog.show();
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