package me.gurinderhans.today.fragments.todofragment.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.joda.time.DateTime;

import io.realm.Realm;
import io.realm.RealmResults;
import me.gurinderhans.today.R;
import me.gurinderhans.today.app.Keys.TodoFragmentKeys;
import me.gurinderhans.today.app.Utils;
import me.gurinderhans.today.fragments.todofragment.model.TodoItem;
import me.gurinderhans.today.recievers.TodoNotificationReceiver;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoFragment extends Fragment {

    @SuppressWarnings("unused")
    public static final String TAG = TodoFragment.class.getSimpleName();

    private TodoItemDataAdapter mAdapter;

    private RecyclerView mListView;
    private EditText mAddTodoText;
    private Realm realm;

    public static TodoFragment newInstance(String title) {
        TodoFragment fragment = new TodoFragment();

        Bundle args = new Bundle();
        args.putString(TodoFragmentKeys.TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // Ignore exceptions if any
            Log.e(TAG, e.toString(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_todo_page_layout, container, false);

        mAdapter = new TodoItemDataAdapter(getContext(), rootView.findViewById(R.id.empty_list_view));

        mListView = (RecyclerView) rootView.findViewById(R.id.items_list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);

        mAddTodoText = (EditText) rootView.findViewById(R.id.new_item_text);

        final String title = getArguments().getString(TodoFragmentKeys.TITLE);
        mAddTodoText.setHint(title);

        mAddTodoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mAddTodoText.getText().length() > 0)
                    rootView.findViewById(R.id.cancel_adding_item).setVisibility(View.VISIBLE);
                else rootView.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
            }
        });

        mAddTodoText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = mAddTodoText.getText().toString();
                    if (!text.isEmpty()) {
                        DateTime time = DateTime.now();
                        if (title != null && title.equals("TOMORROW"))
                            time = time.plusDays(1);

                        mAdapter.addItem(text, time.toDate());
                    } else {
                        mAddTodoText.clearFocus();
                        hideKeyboard(getActivity());
                    }

                    mAddTodoText.setText("");
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        (rootView.findViewById(R.id.cancel_adding_item)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddTodoText.setText("");
                mAddTodoText.clearFocus();
                hideKeyboard(getActivity());
                rootView.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
            }
        });

        // Or alternatively do the same all at once (the "Fluent interface"):
        realm = Realm.getInstance(getContext());

        // delete `done` items
        realm.beginTransaction();
        realm.where(TodoItem.class)
                .equalTo("done", true)
                .findAll().clear();
        realm.commitTransaction();

        DateTime now = DateTime.now();
        DateTime start = now.withTimeAtStartOfDay();
        DateTime end = start.plusDays(1);

        RealmResults<TodoItem> results;
        if (title != null && title.equals("TOMORROW")) {
            start = end;
            end = start.plusDays(1);

            results = realm.where(TodoItem.class)
                    .between("setForDate", start.toDate(), end.toDate())
                    .equalTo("done", false)
                    .findAllSorted("createdAt", RealmResults.SORT_ORDER_DESCENDING);
        } else {
            results = realm.where(TodoItem.class)
                    .lessThan("setForDate", end.toDate())
                    .equalTo("done", false)
                    .findAllSorted("createdAt", RealmResults.SORT_ORDER_DESCENDING);
        }

        mAdapter.setAll(results);

        TodoNotificationReceiver.createAlarm(getContext(), Utils.NotificationAlarmTimes.nextTime());

        return rootView;
    }


}