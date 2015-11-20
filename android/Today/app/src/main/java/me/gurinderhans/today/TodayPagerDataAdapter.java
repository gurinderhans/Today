package me.gurinderhans.today;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by ghans on 11/18/15.
 */
public class TodayPagerDataAdapter extends RecyclerView.Adapter<TodoItemViewHolder> {

    public static final String TAG = TodayPagerDataAdapter.class.getSimpleName();

    public static Realm REALM_INSTANCE;

    private List<TodoItem> mTodoItemsList = new ArrayList<>();
    private View emptyView;

    public TodayPagerDataAdapter(Context context, View emptyView) {
        this.emptyView = emptyView;
        REALM_INSTANCE = Realm.getInstance(context);
    }

    @Override
    public TodoItemViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        return new TodoItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_page_layout_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(TodoItemViewHolder holder, int pos) {
        holder.bindTodoItem(mTodoItemsList.get(pos));
    }

    @Override
    public int getItemCount() {
        if (mTodoItemsList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else emptyView.setVisibility(View.INVISIBLE);

        return mTodoItemsList.size();
    }

    public void addItem(String text, Date date) {
        REALM_INSTANCE.beginTransaction();
        mTodoItemsList.add(0, REALM_INSTANCE.copyToRealmOrUpdate(new TodoItem(text, date)));
        REALM_INSTANCE.commitTransaction();
    }

    public void setAll(List<TodoItem> items) {
        mTodoItemsList.addAll(items);
    }
}
