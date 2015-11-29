package me.gurinderhans.today.fragments.todofragment.controller;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.joda.time.DateTime;

import java.util.Date;

import io.realm.Realm;
import me.gurinderhans.today.R;
import me.gurinderhans.today.app.Keys.PagerTab;
import me.gurinderhans.today.app.Keys.TodoFragmentKeys;
import me.gurinderhans.today.app.Utils;
import me.gurinderhans.today.app.Utils.NotificationAlarmTimes;
import me.gurinderhans.today.fragments.todofragment.helper.TodoItemTouchHelperCallback;
import me.gurinderhans.today.fragments.todofragment.model.TodoItem;

import static me.gurinderhans.today.app.Keys.PagerTab.TODAY;
import static me.gurinderhans.today.app.Keys.PagerTab.TOMORROW;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoFragment extends Fragment {

    @SuppressWarnings("unused")
    public static final String TAG = TodoFragment.class.getSimpleName();

    private TodoItemDataAdapter mAdapter;

    private EditText mAddTodoText;

    private String mPageTitle;

    public static TodoFragment newInstance(PagerTab tab) {
        TodoFragment fragment = new TodoFragment();

        Bundle args = new Bundle();
        args.putString(TodoFragmentKeys.TITLE, tab.title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_todo_page_layout, container, false);

        mAdapter = new TodoItemDataAdapter(getContext(), rootView.findViewById(R.id.empty_list_view));

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.items_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        // assign item touch callback to adapter and recycler view
        ItemTouchHelper.Callback callback = new TodoItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAddTodoText = (EditText) rootView.findViewById(R.id.new_item_text);

        // add typeface
        Typeface robotoCondensed = Typeface.create("sans-serif-condensed", Typeface.BOLD);
        if (robotoCondensed != null)
            mAddTodoText.setTypeface(robotoCondensed);

        mPageTitle = getArguments().getString(TodoFragmentKeys.TITLE);
        mAddTodoText.setHint(mPageTitle);

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
                    if (text.isEmpty()) {
                        mAddTodoText.clearFocus();
                        Utils.hideKeyboard(getActivity());
                        return true;
                    }


                    Date date = null;
                    if (mPageTitle.equals(TODAY.title) || mPageTitle.equals(TOMORROW.title)) {
                        DateTime time = DateTime.now();
                        date = mPageTitle.equals(TODAY.title)
                                ? time.toDate()
                                : time.plusDays(1).toDate();
                    }
                    mAdapter.addItem(text, date);

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
                Utils.hideKeyboard(getActivity());
                rootView.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
            }
        });

        Realm realm = Realm.getInstance(getContext());

        // Delete `done` items
        realm.beginTransaction();
        realm.where(TodoItem.class)
                .equalTo("done", true)
                .findAll().clear();
        realm.commitTransaction();

        mAdapter.setAll(Utils.fetchAdapterData(realm, mPageTitle));

        NotificationAlarmTimes.createAlarm(getContext(), NotificationAlarmTimes.nextTime());

        return rootView;
    }

}