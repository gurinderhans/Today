package me.gurinderhans.today;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoItem {

    protected static final String TAG = TodoItem.class.getSimpleName();

    private String text;
    private boolean done = false;

    public TodoItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void toggleDone() {
        this.done = !this.done;
    }

    public boolean isDone() {
        return done;
    }
}
