package me.gurinderhans.today.fragments.todofragment.controller;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import me.gurinderhans.today.R;
import me.gurinderhans.today.app.Utils;
import me.gurinderhans.today.fragments.todofragment.helper.TodoItemTouchHelperAdapter;
import me.gurinderhans.today.fragments.todofragment.helper.TodoItemTouchHelperViewHolder;
import me.gurinderhans.today.fragments.todofragment.model.TodoItem;
import me.gurinderhans.today.fragments.todofragment.view.TodoItemView;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoItemDataAdapter extends RecyclerView.Adapter<TodoItemDataAdapter.TodoItemViewHolder> implements TodoItemTouchHelperAdapter {

    public static final String TAG = TodoItemDataAdapter.class.getSimpleName();

    public Realm REALM_INSTANCE;

    private List<TodoItem> mTodoItemsList = new ArrayList<>();
    private Context mContext;
    private View emptyView;

    public TodoItemDataAdapter(Context context, View emptyView) {
        mContext = context;
        this.emptyView = emptyView;
        REALM_INSTANCE = Realm.getInstance(context);
    }

    @Override
    public TodoItemViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        return new TodoItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_todo_page_layout_item, parent, false)
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

    public void addItem(String text, @Nullable Date date) {
        REALM_INSTANCE.beginTransaction();

        TodoItem todoItem = new TodoItem(text, date);
        todoItem.setOrderNumber(mTodoItemsList.size());

        mTodoItemsList.add(0, REALM_INSTANCE.copyToRealmOrUpdate(todoItem));
        REALM_INSTANCE.commitTransaction();
    }

    public void setAll(List<TodoItem> items) {
        mTodoItemsList.addAll(items);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        TodoItem prev = mTodoItemsList.remove(fromPosition);
        mTodoItemsList.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClearView() {
        REALM_INSTANCE.beginTransaction();
        for (int i = 0; i < mTodoItemsList.size(); i++)
            mTodoItemsList.get(i).setOrderNumber(mTodoItemsList.size() - 1 - i);
        REALM_INSTANCE.commitTransaction();
    }

    class TodoItemViewHolder extends RecyclerView.ViewHolder implements OnEditorActionListener
            , OnGestureListener
            , OnDoubleTapListener
            , TodoItemTouchHelperViewHolder {

        private final TodoItemView todoTextView;
        private TodoItem todoItem;
        private boolean todoTextViewHasFocus;
        private GestureDetector gestureDetector;

        private TodoItem tmpDeleteItem;

        public TodoItemViewHolder(View itemView) {
            super(itemView);

            todoTextView = (TodoItemView) itemView.findViewById(R.id.item_content);

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            todoTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });

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
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.item_content:
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Utils.hideKeyboard((Activity) mContext);
                        todoTextView.allowEditing(false);
                        todoTextViewHasFocus = false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!todoTextViewHasFocus) {
                try {
                    TodoItemView currentFocus = (TodoItemView) ((Activity) mContext).getCurrentFocus();
                    if (currentFocus != null) {
                        Utils.hideKeyboard((Activity) mContext);
                        currentFocus.allowEditing(false);
                    }
                } catch (Exception ex) {/**/}

                toggleDone();

                // remove item
                final int clickedPos = getLayoutPosition();
                tmpDeleteItem = mTodoItemsList.get(clickedPos);
                mTodoItemsList.remove(clickedPos);
                notifyItemRemoved(clickedPos);
                notifyItemRangeChanged(clickedPos, mTodoItemsList.size());

                Snackbar snackbar = Snackbar.make(todoTextView, "Todo was deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add back in
                        toggleDone();
                        mTodoItemsList.add(clickedPos, tmpDeleteItem);
                        notifyItemInserted(clickedPos);
                        notifyItemRangeChanged(clickedPos, mTodoItemsList.size());
                    }
                });
                snackbar.setActionTextColor(mContext.getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            todoTextView.allowEditing(true);
            todoTextView.requestFocus();
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(todoTextView, InputMethodManager.SHOW_IMPLICIT);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
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

                try {
                    todoItem.setText(todoTextView.getText().toString());

                    if (todoItem.isDone()) todoItem.setDone(false);

                } catch (RealmPrimaryKeyConstraintException re) {
                    Snackbar.make(todoTextView, "Todo item already exists", Snackbar.LENGTH_LONG).show();
                }

                bindTodoItem(todoItem);
            }

            REALM_INSTANCE.commitTransaction();
        }

        @Override
        public void onItemSelected() {
        }
    }
}
