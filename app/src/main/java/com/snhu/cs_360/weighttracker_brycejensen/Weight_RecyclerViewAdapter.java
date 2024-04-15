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

public class Weight_RecyclerViewAdapter extends RecyclerView.Adapter<Weight_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    private ArrayList<WeightModel> weightModels;
    private final RecyclerViewInterface recyclerViewInterface;
    public Weight_RecyclerViewAdapter(Context context, ArrayList<WeightModel> weightModels,
                                      RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.weightModels = weightModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public Weight_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout (gives a look to the rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Weight_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface, weightModels);
    }

    @Override
    public void onBindViewHolder(@NonNull Weight_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigns values to the views created in the recycler_view_row.xml file
        // based on the position of the recycler view
        holder.tvWeight.setText(weightModels.get(position).getWeight_Weight());
        holder.tvDate.setText(weightModels.get(position).getWeight_Date());
        holder.tvPlusMinus.setText(weightModels.get(position).getWeight_PlusMinus());

    }

    @Override
    public int getItemCount() {
        // determines how many items to display
        return weightModels.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // grabs the views from the recycler_view_row.xml file
        // pretty much the onCreate()

        ImageView imageView;
        TextView tvWeight;
        TextView tvDate;
        TextView tvPlusMinus;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface,
                            ArrayList<WeightModel> weightModels) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iScale);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPlusMinus = itemView.findViewById(R.id.tvPlusMinus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

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
