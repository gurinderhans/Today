package me.gurinderhans.today;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghans on 11/18/15.
 */
public class TodayPagerDataAdapter extends ArrayAdapter<TodoItem> {

    public static final String TAG = TodayPagerDataAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private List<TodoItem> mTodoItemsList = new ArrayList<>();

    public TodayPagerDataAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.addAll(mTodoItemsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodoItemViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_page_layout_item, parent, false);

            holder = new TodoItemViewHolder();
            holder.itemContent = (TodoItemView) convertView.findViewById(R.id.item_content);

            convertView.setTag(holder);
        }

        holder = (TodoItemViewHolder) convertView.getTag();

        TodoItem todoItem = mTodoItemsList.get(position);
        holder.itemContent.setText(todoItem.getText());

        if (todoItem.isDone()) {
            holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return convertView;
    }

    public void insertItem(String text, int index) {
        TodoItem todoItem = new TodoItem(text);
        mTodoItemsList.add(index, todoItem);
        this.insert(todoItem, index);
        this.notifyDataSetChanged();
    }

    public static final class TodoItemViewHolder {
        TodoItemView itemContent;
    }
}
