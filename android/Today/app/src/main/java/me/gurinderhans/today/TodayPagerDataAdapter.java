package me.gurinderhans.today;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    public static Realm REALM;

    private List<TodoItem> mTodoItemsList = new ArrayList<>();

    public TodayPagerDataAdapter(Context context) {
        REALM = Realm.getInstance(context);
    }

    @Override
    public TodoItemViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_page_layout_item, parent, false);
        return new TodoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoItemViewHolder holder, int pos) {
        TodoItem todo = mTodoItemsList.get(pos);
        Log.i(TAG, "bindTo");
        holder.bindTodoItem(todo);
    }

    @Override
    public int getItemCount() {
        return mTodoItemsList.size();
    }

    public void addItem(String text, Date date) {

        TodoItem todoItem = new TodoItem(text);
        todoItem.setCreatedAt(date);

        REALM.beginTransaction();

        REALM.copyToRealmOrUpdate(todoItem);

        REALM.commitTransaction();

        mTodoItemsList.add(0, todoItem);
        this.notifyDataSetChanged();
    }

    public void setAll(List<TodoItem> items) {
        mTodoItemsList.addAll(items);
        this.notifyDataSetChanged();
    }
}
