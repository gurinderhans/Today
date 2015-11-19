package me.gurinderhans.today;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import me.gurinderhans.today.Keys.PageFragmentKeys;

/**
 * Created by ghans on 11/18/15.
 */
public class PageFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    public static final String TAG = PageFragment.class.getSimpleName();

    private TodayPagerDataAdapter mAdapter;

    private TodoItemView prevEditingView;

    private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearEditing();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_layout, container, false);

        ((TextView) rootView.findViewById(R.id.day_title))
                .setText(getArguments().getString(PageFragmentKeys.TITLE));

        mAdapter = new TodayPagerDataAdapter(getContext(), R.layout.fragment_page_layout_item);

        final ListView lv = (ListView) rootView.findViewById(R.id.items_list);
        lv.setAdapter(mAdapter);

        final View footer = inflater.inflate(R.layout.todo_item_list_footer, null);
        lv.addFooterView(footer);

        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        final EditText addTodoText = (EditText) footer.findViewById(R.id.new_item_text);

        addTodoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearEditing();
            }
        });

        addTodoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (addTodoText.getText().length() > 0)
                    footer.findViewById(R.id.cancel_adding_item).setVisibility(View.VISIBLE);
                else footer.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
            }
        });

        addTodoText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = addTodoText.getText().toString();
                    if (!text.isEmpty())
                        mAdapter.addItem(text);

                    addTodoText.setText("");
                    lv.setSelection(mAdapter.getCount() - 1);
                    clearEditing();
                }
                return true;
            }
        });

        footer.findViewById(R.id.cancel_adding_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodoText.setText("");
                addTodoText.clearFocus();
                hideKeyboard(getActivity());
                footer.findViewById(R.id.cancel_adding_item).setVisibility(View.INVISIBLE);
            }
        });

        return rootView;
    }

    private void clearEditing() {
        if (prevEditingView != null) {
            prevEditingView.disableEditing();
            prevEditingView = null;
            hideKeyboard(getActivity());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "clicked item @ " + position);
        clearEditing();

        TodoItemView contentView = (TodoItemView) view.findViewById(R.id.item_content);
        if (mAdapter.getItem(position).isDone()) {
            contentView.setPaintFlags(contentView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            contentView.setPaintFlags(contentView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        mAdapter.getItem(position).toggleDone();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "editing item");
        clearEditing();

        prevEditingView = (TodoItemView) view.findViewById(R.id.item_content);
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

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // Ignore exceptions if any
            Log.e(TAG, e.toString(), e);
        }
    }
}