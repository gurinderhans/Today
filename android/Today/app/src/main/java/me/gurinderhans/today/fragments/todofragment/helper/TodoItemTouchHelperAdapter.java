package me.gurinderhans.today.fragments.todofragment.helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ghans on 11/24/15.
 */
public interface TodoItemTouchHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    void onItemMove(int fromPosition, int toPosition);

    void onClearView();
}