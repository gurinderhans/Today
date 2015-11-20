package me.gurinderhans.today;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoItem extends RealmObject {

    protected static final String TAG = TodoItem.class.getSimpleName();

    @PrimaryKey
    private String text;

    private boolean done;

    private Date createdAt;

    public void setText(String text) {
        this.text = text;
    }

    public TodoItem(String text) {
        this.text = text;
    }

    public TodoItem() {/* empty */}

    public String getText() {
        return text;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
