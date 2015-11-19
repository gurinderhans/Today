package me.gurinderhans.today;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoItemView extends EditText {

    public static final String TAG = TodoItemView.class.getSimpleName();

    private boolean mIsEditable = false;

    public TodoItemView(Context context) {
        super(context);
        setEditable(false);
    }

    public TodoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEditable(false);
    }

    public TodoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEditable(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEditable)
            return super.onTouchEvent(event);
        else
            return false;
    }

    private void setEditable(boolean editable) {
        this.setFocusable(editable);
        if (editable)
            this.setFocusableInTouchMode(true);

        this.setEnabled(editable);
        this.setClickable(editable);
        this.setCursorVisible(editable);
    }

    public void enableEditing() {
        mIsEditable = true;
        setEditable(true);
    }

    public void disableEditing() {
        mIsEditable = false;
        setEditable(false);
    }

}
