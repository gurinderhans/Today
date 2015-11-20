package me.gurinderhans.today;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.joda.time.DateTime;

import io.realm.Realm;
import io.realm.RealmResults;
import me.gurinderhans.today.Keys.PageFragmentKeys;

/**
 * Created by ghans on 11/18/15.
 */
public class PageFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    public static final String TAG = PageFragment.class.getSimpleName();

    private TodayPagerDataAdapter mAdapter;

    private TodoItemView prevEditingView;
    private TodoItem editingItem;
    private ListView mListView;
    private EditText mAddTodoText;
    private Realm realm;

    private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                realm.beginTransaction();

                editingItem.setText(prevEditingView.getText().toString());

                realm.commitTransaction();

                clearEditing();
                hideKeyboard(getActivity());
            }
            return true;
        }
    };

    public static PageFragment newInstance(String title) {
        PageFragment fragment = new PageFragment();

        Bundle args = new Bundle();
        args.putString(PageFragmentKeys.TITLE, title);
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
        View rootView = inflater.inflate(R.layout.fragment_page_layout, container, false);

        mAdapter = new TodayPagerDataAdapter(getContext(), R.layout.fragment_page_layout_item);

        mListView = (ListView) rootView.findViewById(R.id.items_list);
        mListView.setAdapter(mAdapter);

        final View header = inflater.inflate(R.layout.todo_item_list_header, null);
        mListView.addHeaderView(header);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mAddTodoText = (EditText) header.findViewById(R.id.new_item_text);

        final String title = getArguments().getString(PageFragmentKeys.TITLE);
        mAddTodoText.setHint(title);

        mAddTodoText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearEditing();
            }
        });

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
                    header.findViewById(R.id.cancel_adding_item).setVisibility(View.VISIBLE);
                else header.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
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
                    mListView.setSelection(mAdapter.getCount() - 1);
                    clearEditing();
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        header.findViewById(R.id.cancel_adding_item).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddTodoText.setText("");
                mAddTodoText.clearFocus();
                hideKeyboard(getActivity());
                header.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
            }
        });

        // Or alternatively do the same all at once (the "Fluent interface"):
        realm = Realm.getInstance(getContext());

        DateTime now = DateTime.now();
        DateTime start = now.withTimeAtStartOfDay();
        DateTime end = start.plusDays(1);
        if (title != null && title.equals("TOMORROW")) {
            start = end;
            end = start.plusDays(1);
        }

        RealmResults<TodoItem> results = realm.where(TodoItem.class)
                .between("createdAt", start.toDate(), end.toDate())
                .findAllSorted("createdAt", RealmResults.SORT_ORDER_DESCENDING);
        mAdapter.setAll(results);

        return rootView;
    }

    private void clearEditing() {
        if (prevEditingView != null) {
            prevEditingView.disableEditing();
            prevEditingView = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "clicked item @ " + position);

        clearEditing();
        mAddTodoText.clearFocus();
        hideKeyboard(getActivity());

        position -= mListView.getHeaderViewsCount();

        realm.beginTransaction();

        mAdapter.getItem(position).setDone(!mAdapter.getItem(position).isDone());

        realm.commitTransaction();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "editing item");

        position -= mListView.getHeaderViewsCount();

        clearEditing();
        mAddTodoText.clearFocus();

        prevEditingView = (TodoItemView) view.findViewById(R.id.item_content);

        editingItem = mAdapter.getItem(position);

        prevEditingView.setSelection(prevEditingView.getText().length());
        prevEditingView.enableEditing();

        // listen for done event to hide keyboard and disable editing
        prevEditingView.setOnEditorActionListener(onEditorActionListener);

        // make sure the field gets focused
        prevEditingView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(prevEditingView, InputMethodManager.SHOW_IMPLICIT);

        return true;
    }
}