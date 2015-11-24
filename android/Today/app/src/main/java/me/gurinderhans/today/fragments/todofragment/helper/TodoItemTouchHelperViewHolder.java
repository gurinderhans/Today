package me.gurinderhans.today.fragments.todofragment.helper;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by ghans on 11/24/15.
 */
public interface TodoItemTouchHelperViewHolder {

    /**
     * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();
}