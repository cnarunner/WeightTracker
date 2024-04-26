package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * The Weight_RecyclerViewAdapter class is an adapter for the RecyclerView that displays a list of weight entries.
 * It manages the data and provides a way to bind the data to the views in the RecyclerView.
 */
public class Weight_RecyclerViewAdapter extends RecyclerView.Adapter<Weight_RecyclerViewAdapter.MyViewHolder> {
    // Context object to access resources, classes, etc.
    Context context;

    // ArrayList to hold the WeightModel objects
    private ArrayList<WeightModel> weightModels;

    // Interface for handling click events
    private final RecyclerViewInterface recyclerViewInterface;

    /**
     * Constructor for the Weight_RecyclerViewAdapter class.
     *
     * @param context              The context object.
     * @param weightModels         The ArrayList containing WeightModel objects.
     * @param recyclerViewInterface The interface for handling click events.
     */
    public Weight_RecyclerViewAdapter(Context context, ArrayList<WeightModel> weightModels,
                                      RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.weightModels = weightModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    /**
     * Called when a ViewHolder is needed to represent an item in the RecyclerView.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public Weight_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout (gives a look to the rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Weight_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface, weightModels);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull Weight_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // Get the WeightModel object at the current position
        WeightModel weightModel = weightModels.get(position);

        // Assign the weight value
        holder.tvWeight.setText(weightModel.getWeight_Weight());

        // Assign the date value
        holder.tvDate.setText(weightModel.getWeight_Date());

        // Check if the weight_PlusMinus value is null or empty
        String plusMinusValue = weightModel.getWeight_PlusMinus();
        if (plusMinusValue == null || plusMinusValue.isEmpty()) {
            // If weight_PlusMinus is null or empty, set a default value (e.g., "+0.0")
            plusMinusValue = "";
        }

        // Assign the plus/minus value
        holder.tvPlusMinus.setText(plusMinusValue);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        // determines how many items to display
        return weightModels.size();
    }

    /**
     * The ViewHolder class that represents a single row in the RecyclerView.
     * It holds the views for displaying the weight, date, and plus/minus value.
     * It also handles click and long-click events on the row items.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Views for displaying weight, date, and plus/minus value
        ImageView imageView;
        TextView tvWeight;
        TextView tvDate;
        TextView tvPlusMinus;

        /**
         * Constructor for the MyViewHolder class.
         *
         * @param itemView             The inflated view for the row item.
         * @param recyclerViewInterface The interface for handling click events.
         * @param weightModels         The ArrayList containing WeightModel objects.
         */
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface,
                            ArrayList<WeightModel> weightModels) {
            super(itemView);

            // Find the views from the recycler_view_row.xml layout
            imageView = itemView.findViewById(R.id.iScale);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPlusMinus = itemView.findViewById(R.id.tvPlusMinus);

            // Set click listener for the row item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call the onItemClick method of the RecyclerViewInterface
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            // Set long-click listener for the row item
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Call the onItemLongClick method of the RecyclerViewInterface
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            WeightModel clickedItem = weightModels.get(pos);
                            recyclerViewInterface.onItemLongClick(pos, clickedItem);
                        }
                    }
                    return true;
                }
            });
        }
    }
}

