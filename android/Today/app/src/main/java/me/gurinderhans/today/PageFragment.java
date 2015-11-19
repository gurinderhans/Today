package me.gurinderhans.today;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import me.gurinderhans.today.Keys.PageFragmentKeys;

/**
 * Created by ghans on 11/18/15.
 */
public class PageFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = PageFragment.class.getSimpleName();

    private TodoItemView prevEditingView;

    public static PageFragment newInstance(String title) {
        PageFragment fragment = new PageFragment();

        Bundle args = new Bundle();
        args.putString(PageFragmentKeys.TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    TodayPagerDataAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_layout, container, false);

        ((TextView) rootView.findViewById(R.id.day_title))
                .setText(getArguments().getString(PageFragmentKeys.TITLE));

        mAdapter = new TodayPagerDataAdapter(getContext(), R.layout.fragment_page_layout_item);

        final ListView lv = (ListView) rootView.findViewById(R.id.items_list);
        lv.setAdapter(mAdapter);

        View footer = inflater.inflate(R.layout.todo_item_list_footer, null);
        lv.addFooterView(footer);

        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        final EditText addTodoText = (EditText) footer.findViewById(R.id.new_item_text);
        addTodoText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = addTodoText.getText().toString();
                    if (!text.isEmpty())
                        mAdapter.addItem(text);

                    addTodoText.setText("");
                    lv.setSelection(mAdapter.getCount() - 1);
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "clicked item @ " + position);
        if (prevEditingView != null) {
            prevEditingView.disableEditing();
            prevEditingView = null;
        }

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
        if (prevEditingView != null) {
            prevEditingView.disableEditing();
            prevEditingView = null;
        }

        prevEditingView = (TodoItemView) view.findViewById(R.id.item_content);
        prevEditingView.enableEditing();
        prevEditingView.requestFocus();

        return true;
    }
}