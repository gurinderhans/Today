package me.gurinderhans.today;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static me.gurinderhans.today.PageFragment.hideKeyboard;

/**
 * Created by ghans on 11/18/15.
 */
public class TodayPagerDataAdapter extends RecyclerView.Adapter<TodayPagerDataAdapter.TodoItemViewHolder> {

    public static final String TAG = TodayPagerDataAdapter.class.getSimpleName();

    public Realm REALM_INSTANCE;

    private List<TodoItem> mTodoItemsList = new ArrayList<>();
    private Context mContext;
    private View emptyView;

    public TodayPagerDataAdapter(Context context, View emptyView) {
        mContext = context;
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

    class TodoItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener
            , OnLongClickListener
            , OnEditorActionListener {

        private final TodoItemView todoTextView;
        private TodoItem todoItem;
        private boolean todoTextViewHasFocus;

        public TodoItemViewHolder(View itemView) {
            super(itemView);

            todoTextView = (TodoItemView) itemView.findViewById(R.id.item_content);

            todoTextView.setOnClickListener(this);
            todoTextView.setOnLongClickListener(this);
            todoTextView.setOnEditorActionListener(this);
            todoTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    todoTextViewHasFocus = hasFocus;
                    if (!hasFocus) {
                        todoTextView.allowEditing(false);
                        onTodoTextChanged();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_content:
                    if (!todoTextViewHasFocus) {
                        try {
                            TodoItemView currentFocus = (TodoItemView) ((Activity) mContext).getCurrentFocus();
                            if (currentFocus != null) {
                                hideKeyboard((Activity) mContext);
                                currentFocus.allowEditing(false);
                            }
                        } catch (Exception e) {/**/}

                        toggleDone();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.item_content:
                    todoTextView.allowEditing(true);
                    todoTextView.requestFocus();
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(todoTextView, InputMethodManager.SHOW_IMPLICIT);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.item_content:
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard((Activity) mContext);
                        todoTextView.allowEditing(false);
                        todoTextViewHasFocus = false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }


        //
        // MARK: Custom methods
        //


        public void bindTodoItem(TodoItem item) {
            todoItem = item;
            todoTextView.setText(todoItem.getText());
            todoTextView.strikeThrough(todoItem.isDone());
        }

        private void toggleDone() {
            REALM_INSTANCE.beginTransaction();
            todoItem.setDone(!todoItem.isDone());
            REALM_INSTANCE.commitTransaction();

            bindTodoItem(todoItem);
        }

        private void onTodoTextChanged() {
            REALM_INSTANCE.beginTransaction();

            if (todoTextView.getText().toString().isEmpty()) {

                todoItem.setText("");
                todoItem.removeFromRealm();

                notifyItemRemoved(this.getLayoutPosition());

                mTodoItemsList.remove(this.getLayoutPosition());

            } else {

                todoItem.setText(todoTextView.getText().toString());

                if (todoItem.isDone()) todoItem.setDone(false);

                bindTodoItem(todoItem);
            }

            REALM_INSTANCE.commitTransaction();
        }
    }
}
