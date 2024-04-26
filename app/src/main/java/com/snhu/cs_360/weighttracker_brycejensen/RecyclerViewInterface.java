package com.snhu.cs_360.weighttracker_brycejensen;

/**
 * The RecyclerViewInterface is an interface that defines two methods to be implemented
 * by classes that handle click events on items in a RecyclerView.
 */
public interface RecyclerViewInterface {

    /**
     * This method is called when an item in the RecyclerView is clicked.
     *
     * @param position The position of the item that was clicked.
     */
    void onItemClick(int position);

    /**
     * This method is called when an item in the RecyclerView is long-clicked.
     *
     * @param position The position of the item that was long-clicked.
     * @param weightModel The WeightModel object associated with the item that was long-clicked.
     */
    void onItemLongClick(int position, WeightModel weightModel);
}
