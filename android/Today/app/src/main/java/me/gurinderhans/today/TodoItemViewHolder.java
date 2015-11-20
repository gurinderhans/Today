package me.gurinderhans.today;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import io.realm.Realm;

import static me.gurinderhans.today.PageFragment.hideKeyboard;

/**
 * Created by ghans on 11/19/15.
 */
public class TodoItemViewHolder extends RecyclerView.ViewHolder implements
        OnClickListener
        , OnLongClickListener
        , OnEditorActionListener {

    public static final String TAG = TodoItemViewHolder.class.getSimpleName();

    private final TodoItemView todoTextView;
    private TodoItem todoItem;
    public Realm realm;
    private Context mContext;


    public TodoItemViewHolder(View itemView) {
        super(itemView);

        mContext = itemView.getContext();

        realm = Realm.getInstance(mContext);

        todoTextView = (TodoItemView) itemView.findViewById(R.id.item_content);

        todoTextView.setOnClickListener(this);
        todoTextView.setOnLongClickListener(this);
        todoTextView.setOnEditorActionListener(this);
    }

    public void bindTodoItem(TodoItem item) {
        todoItem = item;
        todoTextView.setText(todoItem.getText());
        todoTextView.strikeThrough(todoItem.isDone());
    }

    private void toggleDone() {
        realm.beginTransaction();
        todoItem.setDone(!todoItem.isDone());
        realm.commitTransaction();

        Log.i(TAG, "toggleDone, done:" + todoItem.isDone());

        todoTextView.strikeThrough(todoItem.isDone());
    }

    private void onTodoTextChanged() {
        realm.beginTransaction();

        todoItem.setText(todoTextView.getText().toString());

//        if (todoItem.isDone()) todoItem.setDone(false);

        realm.commitTransaction();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_content:
                toggleDone();
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
                    onTodoTextChanged();
                    todoTextView.allowEditing(false);
                    hideKeyboard((Activity) mContext);
                }
                break;
            default:
                break;
        }
        return true;
    }
}