package me.gurinderhans.today.fragments.todofragment.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoItemView extends EditText {

    public static final String TAG = TodoItemView.class.getSimpleName();

    public TodoItemView(Context context) {
        super(context);
        allowEditing(false);
    }

    public TodoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        allowEditing(false);
    }

    public TodoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        allowEditing(false);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }

    public void allowEditing(boolean editingAllowed) {
        this.setFocusable(editingAllowed);
        this.setCursorVisible(editingAllowed);
        this.setFocusableInTouchMode(editingAllowed);

        if (editingAllowed)
            setInputType(getInputType() & (~InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS));
        else
            setInputType(getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if (editingAllowed) strikeThrough(false);
    }

    public void strikeThrough(boolean y) {
        if (y) {
            setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            setPaintFlags(getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
