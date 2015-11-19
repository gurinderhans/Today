package me.gurinderhans.today;

import android.content.Context;
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

        holder.itemContent.setText(mTodoItemsList.get(position).getText());

        return convertView;
    }

    public void addItem(String text) {
        TodoItem todoItem = new TodoItem(text);
        mTodoItemsList.add(todoItem);
        this.add(todoItem);
        this.notifyDataSetChanged();
    }

    private static final class TodoItemViewHolder {
        TodoItemView itemContent;
    }
}
